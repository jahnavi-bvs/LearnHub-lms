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
  "active": true,
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
the same for an unknown email and a wrong password), 403 `Account deactivated` (correct password, but
an admin has deactivated the account).

### `GET /api/auth/me` - any logged-in user

**200** → user object for the token's owner. Use it on app start to check that a stored token still works.
Errors: 401, 404 (user deleted after the token was issued).

### Admin endpoints - ADMIN only

Other roles get 403. The first admin account (`admin@learnhub.com`) is created when auth-service starts,
if `ADMIN_PASSWORD` is set in `local-secrets.properties`. The API can't create admins.

| Endpoint | Body | Result |
|---|---|---|
| `GET /api/auth/users` | | **200** → every user, newest first |
| `PUT /api/auth/users/{id}/active` | `{ "active": false }` | **200** → updated user |
| `PUT /api/auth/users/{id}/role` | `{ "role": "INSTRUCTOR" }` | **200** → updated user |
| `GET /api/courses/all` | | **200** → every course, unpublished ones included |
| `PUT /api/courses/{id}/unpublish` | | **200** → updated course |

Errors for the two user changes: 400 `Validation failed` (`role` must be `STUDENT` or `INSTRUCTOR`),
404, 409 (admin accounts, including your own, can't be changed).

### `GET /api/enrollments/stats/by-course` - public

**200** → `[{ "courseId": 6, "enrollments": 12 }]`, most enrolled first. Totals only, no student details.
Powers "Popular now" on the landing page and the admin dashboard chart. Unpublished courses are included.

---

## 4. Courses - `/api/courses`

### Course object

```json
{
  "id": 6,
  "title": "Java for Beginners",
  "description": "Variables, loops, OOP",
  "category": "Programming",
  "level": "Beginner",
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
| `category` | optional, ≤ 100 chars |
| `level` | optional: `Beginner`, `Intermediate` or `Advanced` |
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

### Modules - `/api/courses/{id}/modules`

Each course has modules numbered from 1. A module holds a YouTube video and Markdown lesson notes.

**Outline item** (public; no video or notes):

```json
{ "id": 1, "position": 1, "title": "Introduction to Java and JDK Setup", "description": null, "hasVideo": true }
```

**Module content** (`youtubeVideoId` is `null` when a video still needs instructor input):

```json
{ "id": 1, "position": 1, "title": "Introduction to Java and JDK Setup", "description": null,
  "youtubeVideoId": "4EP8YzcN0hQ", "notes": "### Introduction\nJava is..." }
```

**Module body** (create and update):

| Field | Rules |
|---|---|
| `title` | required, ≤ 200 chars, unique within the course |
| `description` | optional, ≤ 500 chars |
| `youtubeUrl` | optional: any YouTube video link or bare id. Invalid values report `errors.youtubeUrlValid` |
| `notes` | optional Markdown, ≤ 20000 chars |

| Method & path | Who | Success | Notes and errors |
|---|---|---|---|
| `GET /api/courses/{id}/modules` | public | 200, array of outline items | A draft course's outline is hidden like the course: 404 |
| `GET /api/courses/{id}/modules/{moduleId}` | enrolled student, the course's instructor, or ADMIN | 200, content | 401 no token; 403 not enrolled; 404 `Module not found`; 503 `Dependency unavailable` |
| `POST /api/courses/{id}/modules` | owner or ADMIN | 201, content + `Location` | Added last. 400; 403; 409 `Duplicate module` |
| `PUT /api/courses/{id}/modules/{moduleId}` | owner or ADMIN | 200, content | **Full replacement**: omitting `youtubeUrl` removes the video. 400; 403; 404; 409 |
| `DELETE /api/courses/{id}/modules/{moduleId}` | owner or ADMIN | 204 | The remaining modules are renumbered. 403; 404 |
| `PUT /api/courses/{id}/modules/order` | owner or ADMIN | 200, array of outline items | Body `{"moduleIds": [3, 1, 2]}`, listing every module once. 400 `Invalid module order`; 403 |

Deleting a course also deletes its modules.

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
| `DELETE /api/enrollments/{id}` | the enrolled student, or ADMIN | 204 | Drops the course and deletes its progress. 403, 404 |

### Progress

**Progress object:** `percentage` is calculated from the course's current modules. `nextModuleId` is the first
module not yet completed, or `null` when none remain.

```json
{ "enrollmentId": 9, "courseId": 6, "completedModuleIds": [1, 2], "completedCount": 2, "totalModules": 8,
  "percentage": 25, "nextModuleId": 3 }
```

| Method & path | Who | Success | Notes and errors |
|---|---|---|---|
| `GET /api/enrollments/mine/progress` | STUDENT | 200, array | One per enrollment. 503 if course-service is down |
| `PUT /api/enrollments/{id}/completed-modules/{moduleId}` | the enrolled student | 200, progress | Marks as complete; safe to repeat. 403; 404 `Module not found` (not in this course); 503 |
| `DELETE /api/enrollments/{id}/completed-modules/{moduleId}` | the enrolled student | 200, progress | Undoes it. 403, 404 |

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
- **Account changes reach existing tokens late.** A deactivated user can't log in again, but a token
  they already hold keeps working until it expires (up to 1 hour). A role change also applies from
  the next login.
- **Instructors can republish.** After an admin unpublishes a course, its instructor can still publish
  it again from the edit page.
- **403 `detail` text varies by service.** assessment-service explains the reason; course- and
  enrollment-service return a generic message.
