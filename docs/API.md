# LearnHub LMS - API Reference

All clients (React, Postman) call the **API Gateway**:

```
Base URL: http://localhost:8080
```

The gateway forwards `/api/auth/**` to auth-service (8081), `/api/courses/**` to course-service (8082),
`/api/enrollments/**` to enrollment-service (8083) and `/api/assessments/**` to assessment-service (8084).
Clients never call those ports directly.

---

## 1. Conventions

| Topic | Rule |
|---|---|
| Format | JSON request and response bodies, `Content-Type: application/json` |
| Authentication | `Authorization: Bearer <accessToken>` (token from `POST /api/auth/login`) |
| Token lifetime | 1 hour (`expiresIn: 3600`). There is no refresh endpoint: after expiry, log in again |
| Logout | Client-side only: discard the token. The server keeps no sessions |
| Timestamps | ISO-8601 in **UTC**, e.g. `"2026-10-27T11:49:14.576616Z"`. Convert to local time in the UI |
| IDs | Numbers. Gaps in the sequence are normal |
| Roles | `STUDENT`, `INSTRUCTOR`, `ADMIN` |
| Lists | Returned as JSON arrays, not paginated |
| CORS | Allowed origin: `http://localhost:5173` (the React dev server) |

### JWT payload

The token's payload can be decoded on the client (it is signed, not encrypted):

```json
{
  "sub": "3",
  "role": "STUDENT",
  "name": "Jahnavi Student",
  "email": "student1@learnhub.com",
  "iss": "learnhub-auth",
  "iat": 1793102576,
  "exp": 1793106176
}
```

`sub` is the user id. `exp` is the expiry time in seconds since 1970-01-01 UTC.

---

## 2. Error format

Every error is an [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) *problem detail* with content type
`application/problem+json`:

```json
{
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields are invalid",
  "instance": "/api/auth/register",
  "errors": {
    "email": "Email must be a valid email address",
    "password": "Password must be between 8 and 72 characters"
  }
}
```

- `title` - short category; stable, safe to branch on in code
- `detail` - human-readable message, suitable to show the user
- `errors` - **only** for `"Validation failed"`: field name → message. Nested fields use paths such as
  `questions[0].options`

### Status codes and titles

| Status | `title` | Meaning |
|---|---|---|
| 400 | `Validation failed` | Body fields are invalid; see `errors` |
| 400 | `Malformed request` | Body missing, not JSON, or wrong type (e.g. unknown role) |
| 400 | `Invalid parameter` | Path or query value has the wrong type, e.g. `/api/courses/abc` |
| 400 | `Missing parameter` | Required query parameter missing |
| 400 | `Invalid submission` | Answers don't fit the quiz (see `detail`) |
| 401 | `Unauthorized` | Token missing, invalid or expired. Response includes `WWW-Authenticate: Bearer` |
| 401 | `Authentication failed` | Login with wrong email or password |
| 403 | `Forbidden` | Valid token, but the role or ownership doesn't allow this action |
| 404 | `Course not found`, `Enrollment not found`, `Assessment not found`, `Submission not found` | Resource doesn't exist or isn't visible to the caller |
| 409 | `Email already registered`, `Already enrolled`, `Already submitted`, `Data conflict` | Duplicate |
| 503 | `Service unavailable` | Gateway can't reach the service handling this URL |
| 503 | `Dependency unavailable` | A service can't reach another service it depends on |

Suggested client handling: **401 → clear the token and go to the login page**; 403 → show `detail`;
400 with `errors` → show each message next to its form field; 503 → "try again later".

---

## 3. Auth - `/api/auth`

### User object

```json
{
  "id": 3,
  "fullName": "Jahnavi Student",
  "email": "student1@learnhub.com",
  "role": "STUDENT",
  "createdAt": "2026-10-27T11:49:14.576616Z"
}
```

### `POST /api/auth/register` - public

| Field | Rules |
|---|---|
| `fullName` | required, ≤ 100 chars |
| `email` | required, valid email, ≤ 254 chars. Stored in lower case |
| `password` | required, 8–72 chars |
| `role` | optional: `STUDENT` (default) or `INSTRUCTOR`. `ADMIN` is rejected |

```json
{ "fullName": "Jahnavi Student", "email": "student1@learnhub.com", "password": "Password123", "role": "STUDENT" }
```

**201** → user object.
Errors: 400 `Validation failed` (choosing `ADMIN` reports `errors.roleAllowed`), 400 `Malformed request`
(unknown role), 409 `Email already registered`.

### `POST /api/auth/login` - public

```json
{ "email": "student1@learnhub.com", "password": "Password123" }
```

**200**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": { "id": 3, "fullName": "Jahnavi Student", "email": "student1@learnhub.com", "role": "STUDENT", "createdAt": "..." }
}
```

Errors: 400 `Validation failed`, 401 `Authentication failed` (`detail`: `"Invalid email or password"` -
the same for an unknown email and a wrong password).

### `GET /api/auth/me` - any logged-in user

**200** → user object for the token's owner. Use it on app start to check that a stored token still works.
Errors: 401, 404 (user deleted after the token was issued).

---

## 4. Courses - `/api/courses`

### Course object

```json
{
  "id": 6,
  "title": "Java for Beginners",
  "description": "Variables, loops, OOP",
  "instructorId": 11,
  "instructorName": "Ira Instructor",
  "published": true,
  "createdAt": "2026-10-27T12:20:08.895368Z",
  "updatedAt": "2026-10-27T12:20:08.895368Z"
}
```

### Course body (create and update)

| Field | Rules |
|---|---|
| `title` | required, ≤ 200 chars |
| `description` | optional, ≤ 5000 chars |
| `published` | optional boolean, **defaults to `false` (draft)** |

| Method & path | Who | Success | Notes and errors |
|---|---|---|---|
| `GET /api/courses` | public | 200, array | Published courses only, newest first |
| `GET /api/courses/{id}` | public | 200, course | A draft is visible only to its owner or an ADMIN (send the token); others get 404. 400 `Invalid parameter` for a non-numeric id |
| `GET /api/courses/mine` | INSTRUCTOR, ADMIN | 200, array | Courses whose `instructorId` is the caller, drafts included, newest first |
| `POST /api/courses` | INSTRUCTOR, ADMIN | 201, course + `Location` header | The caller becomes the instructor. 400, 403 |
| `PUT /api/courses/{id}` | owner or ADMIN | 200, course | **Full replacement**: send all fields. Omitting `published` makes the course a draft. 400, 403, 404 |
| `DELETE /api/courses/{id}` | owner or ADMIN | 204, no body | 403, 404 |

---

## 5. Enrollments - `/api/enrollments`

### Enrollment object

```json
{
  "id": 9,
  "studentId": 3,
  "studentName": "Jahnavi Student",
  "courseId": 6,
  "courseTitle": "Java for Beginners",
  "enrolledAt": "2026-10-27T13:05:41.120Z"
}
```

`studentName` and `courseTitle` are copied when the student enrolls. They don't change if the course is renamed later.

| Method & path | Who | Success | Notes and errors |
|---|---|---|---|
| `POST /api/enrollments` | STUDENT | 201, enrollment + `Location` | Body `{"courseId": 6}`. 400 `Validation failed`; 404 `Course not found` (missing **or a draft**); 409 `Already enrolled`; 503 `Dependency unavailable` |
| `GET /api/enrollments/mine` | STUDENT | 200, array | Newest first |
| `GET /api/enrollments/course/{courseId}` | that course's instructor, or ADMIN | 200, array | The roster, oldest first. 403, 404, 503 |
| `DELETE /api/enrollments/{id}` | the enrolled student, or ADMIN | 204 | Drops the course. 403, 404 |

---

## 6. Assessments - `/api/assessments`

### Create body

```json
{
  "courseId": 6,
  "title": "Java Basics Quiz",
  "description": "Chapter 1 check",
  "questions": [
    { "text": "Which keyword creates an object?", "options": ["new", "make", "create"], "correctOptionIndex": 0 },
    { "text": "Is Java statically typed?", "options": ["Yes", "No"], "correctOptionIndex": 0 }
  ]
}
```

| Field | Rules |
|---|---|
| `courseId` | required, positive |
| `title` | required, ≤ 200 chars |
| `description` | optional, ≤ 2000 chars |
| `questions` | 1–50 questions |
| `questions[i].text` | required, ≤ 1000 chars |
| `questions[i].options` | 2–6 non-blank strings, each ≤ 500 chars |
| `questions[i].correctOptionIndex` | required, **0-based**, must point at one of the options (error key `questions[i].correctOptionInRange`) |

### Assessment object

```json
{
  "id": 12,
  "courseId": 6,
  "title": "Java Basics Quiz",
  "description": "Chapter 1 check",
  "createdAt": "2026-10-28T09:12:03.410Z",
  "questions": [
    { "id": 31, "text": "Which keyword creates an object?", "options": ["new", "make", "create"], "correctOptionIndex": 0 }
  ]
}
```

**`correctOptionIndex` is only present for the course's instructor and ADMIN.** Students receive the
questions without it.

### Assessment summary (list item)

```json
{ "id": 12, "courseId": 6, "title": "Java Basics Quiz", "questionCount": 3, "createdAt": "..." }
```

| Method & path | Who | Success | Notes and errors |
|---|---|---|---|
| `POST /api/assessments` | the course's instructor, or ADMIN | 201, assessment (with answers) + `Location` | 400; 403 (not your course); 404 `Course not found`; 503 |
| `GET /api/assessments?courseId={id}` | course instructor, ADMIN, or an enrolled student | 200, array of summaries | Oldest first. 400 `Missing parameter`; 403 (`detail` explains why); 503 |
| `GET /api/assessments/{id}` | course instructor, ADMIN, or an enrolled student | 200, assessment | Students don't get answers. 403, 404, 503 |

---

## 7. Submissions - `/api/assessments/...`

### Submit body

One **0-based** option index per question, in question order:

```json
{ "answers": [0, 1, 2] }
```

### Result object (the student's own result)

```json
{
  "id": 13,
  "assessmentId": 12,
  "assessmentTitle": "Java Basics Quiz",
  "score": 2,
  "maxScore": 3,
  "percentage": 66.7,
  "submittedAt": "2026-10-28T10:02:55.301Z",
  "results": [
    {
      "questionId": 32,
      "questionText": "Is Java statically typed?",
      "options": ["Yes", "No"],
      "selectedOptionIndex": 1,
      "correctOptionIndex": 0,
      "correct": false
    }
  ]
}
```

### Submission summary (list item)

```json
{
  "id": 13, "assessmentId": 12, "assessmentTitle": "Java Basics Quiz", "courseId": 6,
  "studentId": 3, "studentName": "Jahnavi Student",
  "score": 2, "maxScore": 3, "percentage": 66.7, "submittedAt": "..."
}
```

| Method & path | Who | Success | Notes and errors |
|---|---|---|---|
| `POST /api/assessments/{id}/submissions` | enrolled STUDENT | 201, result | **One attempt only.** 400 `Invalid submission` (`detail` e.g. `"Expected 3 answers but got 2"`); 403 not enrolled; 404; 409 `Already submitted`; 503 |
| `GET /api/assessments/{id}/submissions/mine` | STUDENT | 200, result | 404 `Submission not found` if not submitted yet |
| `GET /api/assessments/submissions/mine` | STUDENT | 200, array of summaries | All of the student's results, newest first |
| `GET /api/assessments/{id}/submissions` | the course's instructor, or ADMIN | 200, array of summaries | Everyone's scores, oldest first. 403, 404 |

---

## 8. Known limitations

- **No token refresh.** After 1 hour, any call returns 401; the user must log in again.
- **No cross-service clean-up.** Deleting a course does not remove its enrollments or assessments
  (each service owns its own database).
- **Copied names are not updated.** `instructorName`, `studentName` and `courseTitle` keep the value
  they had when the record was created.
- **No pagination or search** on list endpoints.
- **No admin endpoints yet.** ADMIN accounts can't be created through the API.
- **403 `detail` text varies by service.** assessment-service explains the reason; course- and
  enrollment-service return a generic message.
