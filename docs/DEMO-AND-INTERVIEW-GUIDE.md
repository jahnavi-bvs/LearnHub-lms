# LearnHub - Demo and Interview Guide

## 1. Five-minute demo script

**Before you start:** all five backend apps running, `npm run dev` running, two browser windows
(one normal, one private/incognito so two users can be logged in at once).

| # | Show | Say |
|---|---|---|
| 1 | Home page, logged out | "Public catalog: React calls the gateway on 8080, which routes to course-service. Only published courses appear." |
| 2 | Register a new **Instructor** | "Passwords are hashed with BCrypt; registration then logs straight in and stores the JWT." |
| 3 | Teaching → New course (draft) → open it → Edit → tick Published | "Drafts are invisible to the public; the PUT always sends every field because it's a full replacement." |
| 4 | New quiz → add/remove questions and options → Create | "Dynamic form with stable React keys. The server validates nested fields and the errors map back to each question." |
| 5 | Private window: register a **Student** → open the course → Enroll | "Enrollment-service calls course-service over HTTP to check the course exists and is published." |
| 6 | Take the quiz → submit | "One attempt, graded on the server. Students never receive the answer key until they've submitted." |
| 7 | Instructor window: course page → roster, quiz → View submissions | "Ownership is checked in each service, not just hidden in the UI." |
| 8 | Stop course-service in IntelliJ → reload the catalog | "The gateway turns a dead service into a clean 503 instead of a hang or a stack trace." |
| 9 | Optional: F12 → Application → edit the stored token → open Profile | "A tampered token fails the signature check, the API returns 401, and the app logs out automatically." |

## 2. Explaining the architecture (memorise this paragraph)

> LearnHub has four business services - auth, course, enrollment and assessment - each with its own
> MySQL database, behind a Spring Cloud Gateway. Auth-service issues HS256-signed JWTs; every other
> service is an OAuth2 resource server that verifies the token locally with the shared secret, so
> there's no call to auth-service per request. Services that need each other's data call over HTTP
> with RestClient and timeouts - for example, enrollment checks the course exists, and assessment
> checks ownership and enrollment. The gateway handles CORS and rejects bad tokens at the edge, and
> each service still enforces roles and ownership itself. The React frontend talks only to the gateway.

## 3. Interview questions, answered from this project

### Spring Boot and Java

**What does `@SpringBootApplication` do?**
It combines `@Configuration`, `@EnableAutoConfiguration` and `@ComponentScan`: Spring scans the
package for beans and auto-configures things like the DataSource and Tomcat from the classpath and
`application.yaml`.

**Why constructor injection?**
Dependencies are explicit and `final`, and classes can be unit-tested with plain `new` and mocks
(see `AuthServiceTest`). When a class has two constructors, Spring needs `@Autowired` on one - we hit
exactly that bug in `CourseClient`.

**How are secrets kept out of the code?**
`application.yaml` uses `${DB_PASSWORD}` and `${JWT_SECRET}` placeholders, filled from a git-ignored
`local-secrets.properties` imported with `spring.config.import`, or from environment variables.

**What is `@ConfigurationProperties`?**
Type-safe binding of a YAML block to a record, e.g. `learnhub.jwt.*` → `JwtProperties`, including
`1h` → `Duration`.

### JPA and databases

**Entity vs DTO?**
Entities map to tables; DTOs (records like `UserResponse`) define the JSON. Keeping them separate
means the password hash can never leak into a response.

**What is dirty checking?**
Inside a transaction Hibernate tracks loaded entities and writes changed fields on commit - which
is why `CourseService.update()` never calls `save()`.

**LazyInitializationException / N+1 - how did you avoid them?**
With `@EntityGraph` on repository methods (questions and options loaded in one query) and
`open-in-view: false`. A non-transactional integration test caught a real bug: a fetch join over
two collections returned duplicate questions, fixed with `@OrderColumn`.

**Why are there gaps in auto-increment ids?**
Rolled-back inserts still consume ids. Ids are identifiers, not counters.

**How do you prevent duplicate enrollments or submissions?**
A friendly `exists…` check plus a database unique constraint for the race where two requests arrive
at once; the constraint violation is mapped to 409.

### Security

**Authentication vs authorization? 401 vs 403?**
401: we don't know who you are (missing/invalid/expired token). 403: we know, but you're not
allowed (wrong role or not the owner).

**How does JWT auth work here?**
Login checks the BCrypt hash and returns a token signed with HS256 containing `sub`, `role`, `name`,
`exp` and `iss`. Each service's `JwtDecoder` checks signature, expiry and issuer; a converter turns
`role` into `ROLE_STUDENT` etc. for `hasRole(...)` rules. Anyone can *read* a JWT; only the secret
holder can *create* one.

**Why disable CSRF?**
CSRF attacks abuse cookies the browser sends automatically. This API uses a bearer token the
JavaScript adds explicitly, and sessions are stateless.

**Where is the token stored in the browser and what's the risk?**
`localStorage`, so it survives reloads; the trade-off is exposure to XSS. An `httpOnly` cookie would
avoid that but needs CSRF protection and backend changes.

**Why validate the token in both the gateway and the services?**
Defense in depth: if anything bypasses the gateway, services still protect themselves. HS256
verification costs microseconds.

### Microservices

**Why one database per service?**
Services can change their schema and be deployed independently. The cost: no cross-service joins or
foreign keys, so we copy some data (course title, student name) and accept it can go stale.

**What happens if a service is down?**
Callers use connect/read timeouts and map failures to 503 ("Dependency unavailable"); the gateway
does the same for its routes. Without timeouts one hung service would hang every caller.

**Why not hold a DB transaction during an HTTP call?**
It would tie up a pooled connection for as long as the other service takes. Remote checks happen
first; the database write is a short transaction afterwards.

**What would you add for production?**
Docker/Compose, a config server or vault for secrets, Flyway migrations instead of `ddl-auto`,
service discovery, centralized logging and tracing, circuit breakers (Resilience4j), RS256 keys,
refresh tokens, and events (e.g. "course deleted") to clean up related data.

### React

**How does React talk to Spring Boot?**
Through `apiRequest()` in `api/client.js`: base URL from `VITE_API_BASE_URL`, JSON in/out, the bearer
token, and parsing of problem-detail errors into an `ApiError`. `fetch` doesn't throw on 4xx/5xx,
so we check `response.ok`.

**How is login state shared?**
`AuthProvider` (React Context) holds `{token, user}`, persists it in `localStorage`, logs out on any
401 for a request that carried a token, and on token expiry. Components read it with `useAuth()`.

**What is CORS and where is it configured?**
The browser blocks cross-origin calls (5173 → 8080) unless the server allows them; the gateway
answers the preflight `OPTIONS` request. Postman ignores CORS, which is why "it works in Postman".

**Why stable keys in lists?**
React matches list items by key between renders. Index keys break when items are removed - the quiz
builder uses a counter instead.

**Why never mutate state?**
React re-renders when it receives a new value. `answers.map(...)` and `{ ...q, options }` create new
objects; mutating the old array would leave the UI unchanged.

## 4. Debugging cheat sheet (errors we actually met)

| Symptom | Real cause | Fix |
|---|---|---|
| `Access denied for user '${DB_USERNAME}'` | Placeholder not resolved: secrets file not found from the working directory | Extra `spring.config.import` path; check "started by … in <folder>" in the log |
| `Unable to determine Dialect without JDBC metadata` | Hides a DB connection failure | Read the first `Caused by` |
| MySQL error 1044 | Logged in as `learnhub`, which can't create databases or grant | Use the root connection |
| `No default constructor found` | Two constructors, Spring couldn't choose | `@Autowired` on one |
| Duplicate items from a fetch join | Bag (unindexed List) + Cartesian product | `@OrderColumn` / Set |
| Test passes inside `@Transactional` but bug in prod | First-level cache hides real queries | Non-transactional test with cleanup |
| Test broke after manual testing | Tests shared the dev database | `*_test` databases |
| 400 `Invalid value '{12}'` | Braces typed into the URL | Use the real id |
| `npm error enoent package.json` | Terminal in the wrong folder | `cd frontend` |
| Works in Postman, fails in the browser | CORS | Allowed origin must match exactly |
