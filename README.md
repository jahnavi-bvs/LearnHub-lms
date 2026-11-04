# LearnHub LMS

A learning management system built as **Spring Boot microservices** with a **React** frontend.
Instructors create courses and multiple-choice quizzes; students enroll, take quizzes once and get
graded instantly with per-question feedback.

## Architecture

```
                 ┌──────────────────────────── API Gateway :8080 ────────────────────────────┐
React :5173 ───► │ CORS · JWT check at the edge · routing · 503 when a service is down        │
                 └──────┬──────────────────┬───────────────────┬────────────────────┬────────┘
                        │ /api/auth/**     │ /api/courses/**   │ /api/enrollments/**│ /api/assessments/**
                        ▼                  ▼                   ▼                    ▼
                  auth-service       course-service     enrollment-service   assessment-service
                     :8081               :8082               :8083                 :8084
                  issues JWTs        courses, drafts     enroll / drop /      quizzes, grading,
                  (BCrypt, HS256)    ownership rules     roster               results
                        │                  │                ▲   │              │   │
                        ▼                  ▼     HTTP: does │   ▼    HTTP: owner?   ▼
                  learnhub_auth     learnhub_course  course exist? learnhub_   enrolled? learnhub_
                                                     (RestClient)  enrollment  ─────────  assessment
```

- **Database per service** (MySQL): no service reads another's tables; they call each other's APIs.
- **Stateless JWT auth**: auth-service signs tokens; every other service and the gateway verify them
  with the shared secret, without calling auth-service.
- **Defense in depth**: the gateway rejects missing/invalid tokens; each service re-checks the token
  and enforces roles (STUDENT / INSTRUCTOR / ADMIN) and ownership.
- **Consistent errors**: every error is an RFC 9457 problem detail (`application/problem+json`).

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 25, Spring Boot 4.1, Spring Web MVC, Spring Data JPA / Hibernate 7, Spring Security (OAuth2 Resource Server, JWT), Bean Validation, RestClient |
| Gateway | Spring Cloud Gateway Server WebMVC (Spring Cloud 2025.1) |
| Database | MySQL 8.0 |
| Frontend | React 19, React Router 7, Vite 8, plain CSS |
| Tests | JUnit 5, Mockito, MockMvc, MockRestServiceServer (139 backend tests) |

## Prerequisites

- JDK 25 (`java -version`)
- MySQL 8.0 with a root account
- Node.js 20+ and npm (`node -v`)
- Maven is **not** required: each module includes the Maven wrapper (`mvnw`)

## Setup (once)

### 1. Databases

In MySQL Workbench, connected as **root**:

```sql
CREATE USER IF NOT EXISTS 'learnhub'@'localhost' IDENTIFIED BY 'choose-a-password';

CREATE DATABASE IF NOT EXISTS learnhub_auth;
CREATE DATABASE IF NOT EXISTS learnhub_course;
CREATE DATABASE IF NOT EXISTS learnhub_enrollment;
CREATE DATABASE IF NOT EXISTS learnhub_assessment;
-- separate databases for automated tests
CREATE DATABASE IF NOT EXISTS learnhub_auth_test;
CREATE DATABASE IF NOT EXISTS learnhub_course_test;
CREATE DATABASE IF NOT EXISTS learnhub_enrollment_test;
CREATE DATABASE IF NOT EXISTS learnhub_assessment_test;

GRANT ALL PRIVILEGES ON learnhub_auth.*            TO 'learnhub'@'localhost';
GRANT ALL PRIVILEGES ON learnhub_course.*          TO 'learnhub'@'localhost';
GRANT ALL PRIVILEGES ON learnhub_enrollment.*      TO 'learnhub'@'localhost';
GRANT ALL PRIVILEGES ON learnhub_assessment.*      TO 'learnhub'@'localhost';
GRANT ALL PRIVILEGES ON learnhub_auth_test.*       TO 'learnhub'@'localhost';
GRANT ALL PRIVILEGES ON learnhub_course_test.*     TO 'learnhub'@'localhost';
GRANT ALL PRIVILEGES ON learnhub_enrollment_test.* TO 'learnhub'@'localhost';
GRANT ALL PRIVILEGES ON learnhub_assessment_test.* TO 'learnhub'@'localhost';
```

Tables are created automatically by Hibernate on first start.

### 2. Secrets

Copy `backend/local-secrets.properties.example` to `backend/local-secrets.properties` and fill in
`DB_PASSWORD`, `JWT_SECRET` (the template explains how to generate it) and `ADMIN_PASSWORD`, which
creates the `admin@learnhub.com` account the next time auth-service starts. This file is git-ignored.
Environment variables with the same names override it.

### 3. Frontend dependencies

```powershell
cd frontend
npm install
```

### 4. Course catalog (optional)

To load the 15 courses, their 117 modules (YouTube videos and lesson notes) and 45 quizzes:

1. Register the instructor accounts listed in `backend/seed/courses.sql`.
2. Start `course-service` and `assessment-service` once, so Hibernate creates their tables.
3. Run the scripts. They are safe to run more than once:
   ```powershell
   mysql -u learnhub -p --default-character-set=utf8mb4 -e "source backend/seed/courses.sql"
   mysql -u learnhub -p --default-character-set=utf8mb4 -e "source backend/seed/modules.sql"
   mysql -u learnhub -p --default-character-set=utf8mb4 -e "source backend/seed/quizzes.sql"
   ```

Lesson content lives in `backend/seed/course-content/*.md`. After editing it, rebuild the SQL with
`node backend/seed/build-modules-sql.mjs`. Existing modules are not overwritten by the seed; change
those through the app's Manage modules page.

## Running

1. Start the five backend applications (IntelliJ: run each `*Application` class, or from a
   terminal in each service folder: `.\mvnw.cmd spring-boot:run`):
   `auth-service` 8081, `course-service` 8082, `enrollment-service` 8083,
   `assessment-service` 8084, `api-gateway` 8080.
2. Start the frontend:
   ```powershell
   cd frontend
   npm run dev
   ```
3. Open http://localhost:5173

Register an **Instructor** to create courses and quizzes, and a **Student** to enroll and take them.
Log in as `admin@learnhub.com` to manage users and courses.

## Testing

```powershell
cd backend
.\mvnw.cmd test          # all 139 tests in all five services (uses the *_test databases)

cd ..\frontend
npm run lint
npm run build
```

## Project layout

```
backend/
  pom.xml                     aggregator: build/test all services with one command
  local-secrets.properties    (git-ignored) DB credentials + JWT secret
  auth-service/               users, registration, login, JWT issuing
  course-service/             courses (draft/published), ownership
  enrollment-service/         enrollments; calls course-service
  assessment-service/         quizzes, submissions, grading; calls course- and enrollment-service
  api-gateway/                routing, CORS, edge JWT validation
frontend/
  src/api/                    one module per backend area; client.js wraps fetch
  src/auth/                   AuthProvider, useAuth, RequireAuth, JWT helpers
  src/pages/  src/components/ screens and reusable UI
docs/
  API.md                      every endpoint, body, role and error
  DEMO-AND-INTERVIEW-GUIDE.md demo script, interview Q&A, troubleshooting
```

## Known limitations

No token refresh (log in again after 1 hour); deleting a course doesn't remove its enrollments or
quizzes in the other services; no pagination; quizzes can't be edited after creation; deactivating
a user stops new logins, but a token they already hold works until it expires.
See [docs/API.md](docs/API.md#8-known-limitations).
