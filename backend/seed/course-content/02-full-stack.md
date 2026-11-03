# course: Full Stack Web Development

## module: Introduction to Web Development
video: GxmfcnU3feo

### Introduction
Web development is building applications that run in a browser and talk to servers. This module maps the whole landscape before you learn each piece.

### Key concepts
- **Frontend:** what users see, built with HTML, CSS and JavaScript.
- **Backend:** server code that applies rules and stores data.
- **Request and response:** the browser sends an HTTP request, and the server returns a response.
- **Tools:** a code editor, browser DevTools and Git.

### Definitions
- **Client:** the program making the request, usually a browser.
- **Server:** the program answering requests.
- **HTTP:** the protocol browsers and servers use to communicate.
- **Full stack:** working on both frontend and backend.

### Example
When you open a course page, the browser sends `GET /courses/6`. The server loads that course from the database and returns it, and JavaScript then displays it on the page.

### Key takeaways
- Learn in order: HTML, CSS, JavaScript, then a framework.
- DevTools' Network tab shows exactly what a page requests.
- Building small projects teaches more than only watching tutorials.

## module: HTML Fundamentals
video: qz0aGYrrlhU

### Introduction
HTML gives a web page its structure and meaning. Every website, however complex, is delivered to the browser as HTML.

### Key concepts
- **Elements:** tags such as `<h1>`, `<p>`, `<a>`, `<img>` and `<form>`.
- **Attributes:** extra information, such as `href` on a link.
- **Semantic HTML:** tags that describe meaning, such as `<nav>`, `<main>` and `<article>`.
- **Nesting:** elements contain other elements, forming a tree.

### Definitions
- **Tag:** a keyword in angle brackets marking an element.
- **DOM (Document Object Model):** the browser's tree representation of a page.
- **Accessibility:** making content usable by everyone, including screen-reader users.

### Example
```html
<article>
  <h1>Java Basics</h1>
  <p>Learn the <strong>fundamentals</strong>.</p>
  <a href="/courses/1">View course</a>
</article>
```

### Key takeaways
- Use tags for meaning, not for appearance; CSS handles appearance.
- Give every image `alt` text and every input a `<label>`.
- Semantic pages are better for accessibility and search engines.

## module: CSS and Responsive Web Design
video: x4u1yp3Msao

### Introduction
CSS controls how HTML looks. Responsive design makes one page work well on phones, tablets and desktops.

### Key concepts
- **Selectors:** target elements by tag, class or id.
- **Box model:** content, padding, border and margin.
- **Flexbox:** arranges items along one direction.
- **Grid:** arranges items in rows and columns.
- **Media queries:** apply styles at certain screen widths.

### Definitions
- **Cascade:** the rules deciding which style wins when several apply.
- **Viewport:** the visible area of the browser window.
- **Mobile-first:** designing for small screens first, then adding to larger ones.

### Example
```css
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1rem;
}
```
This grid fits as many 240px columns as the screen allows, with no media query.

### Key takeaways
- Use relative units like `rem` and `%` instead of fixed pixels where possible.
- Flexbox suits rows of items; Grid suits whole layouts.
- Always test at phone width.

## module: JavaScript Fundamentals
video: W6NZfCO5SIk

### Introduction
JavaScript makes pages interactive. It is the only programming language that browsers run natively, and it also runs on servers through Node.js.

### Key concepts
- **Variables:** `const` for values that are not reassigned, `let` for those that are.
- **Types:** strings, numbers, booleans, objects, arrays, `null`, `undefined`.
- **Functions:** reusable blocks of logic.
- **DOM access:** select and change page elements.
- **Events:** react to clicks and typing.

### Definitions
- **Object:** a collection of key-value pairs.
- **Array:** an ordered list of values.
- **Event listener:** a function that runs when an event happens.

### Example
```javascript
const button = document.querySelector('#enroll');
button.addEventListener('click', () => {
  button.textContent = 'Enrolled!';
});
```

### Key takeaways
- Prefer `const`, and use `let` only when reassigning.
- Use `===` for comparison, because `==` converts types first.
- `console.log` and DevTools are your first debugging tools.

## module: Advanced JavaScript and ES6
video: NCwa_xi0Uuc

### Introduction
Modern JavaScript (ES6 and later) added features that make code shorter and safer. Frameworks like React rely on them everywhere.

### Key concepts
- **Arrow functions:** a concise function syntax.
- **Destructuring:** unpack values from objects and arrays.
- **Spread:** copy or merge arrays and objects.
- **Modules:** `import` and `export` split code across files.
- **Promises and `async`/`await`:** handle operations that take time.

### Definitions
- **Promise:** an object representing a future result.
- **Asynchronous:** work that finishes later without blocking the page.
- **Closure:** a function that remembers variables from where it was created.

### Example
```javascript
async function loadCourses() {
  const response = await fetch('/api/courses');
  if (!response.ok) throw new Error('Request failed');
  const courses = await response.json();
  return courses.map(({ id, title }) => ({ id, title }));
}
```

### Key takeaways
- `await` makes asynchronous code read like normal code.
- `fetch` does not throw on 404 or 500, so check `response.ok`.
- Use `map`, `filter` and `reduce` to transform arrays.

## module: React.js Fundamentals
video: SqcY0GlETPk

### Introduction
React builds user interfaces from reusable components. You describe what the screen should look like for the current data, and React updates the page to match.

### Key concepts
- **Components:** functions that return JSX.
- **Props:** data passed from parent to child.
- **State:** data that changes over time, held with `useState`.
- **Effects:** `useEffect` runs code after rendering, such as loading data.
- **Keys:** stable ids that help React track list items.

### Definitions
- **JSX:** HTML-like syntax inside JavaScript.
- **Hook:** a function that adds React features, named `use...`.
- **Re-render:** React calling a component again after state changes.

### Example
```jsx
function Counter() {
  const [count, setCount] = useState(0);
  return <button onClick={() => setCount(count + 1)}>Clicked {count}</button>;
}
```

### Key takeaways
- Data flows down through props; events flow up through callbacks.
- Never change state directly; always create a new value.
- Split the screen into small components with one job each.

## module: Backend Development with Node.js and Express
video: KOutPbKc9UM

### Introduction
Node.js runs JavaScript outside the browser, and Express makes building web servers simple. Together they let you use one language across the full stack.

### Key concepts
- **Routes:** connect a method and URL to a handler.
- **Middleware:** functions that run before a handler, such as authentication or JSON parsing.
- **Request and response objects:** `req` holds input, and `res` sends output.
- **npm:** installs and manages packages.

### Definitions
- **Node.js:** a JavaScript runtime built on Chrome's V8 engine.
- **Express:** a minimal web framework for Node.js.
- **Event loop:** how Node handles many requests on one thread without blocking.

### Example
```javascript
const express = require('express');
const app = express();
app.use(express.json());

app.get('/api/health', (req, res) => {
  res.json({ status: 'ok' });
});

app.listen(3000);
```

### Key takeaways
- Keep secrets in environment variables, never in code.
- Long CPU work blocks the event loop, so avoid it in handlers.
- Separate routes from business logic as the application grows.

## module: REST APIs and Database Integration
video: -mN3VyJuCjM

### Introduction
REST is the most common style for web APIs. It organizes a server around resources, and it stores those resources in a database.

### Key concepts
- **Resources and URLs:** `/courses` and `/courses/6` name things, not actions.
- **HTTP methods:** `GET` reads, `POST` creates, `PUT` replaces, `DELETE` removes.
- **Status codes:** `200` OK, `201` Created, `400` bad input, `401` not logged in, `403` not allowed, `404` not found, `500` server error.
- **Persistence:** handlers read and write using SQL or an ORM.

### Definitions
- **API:** a defined way for programs to talk to each other.
- **Stateless:** each request carries everything needed to process it.
- **ORM:** a library mapping database rows to objects.
- **Idempotent:** repeating a request has the same effect as making it once.

### Example
```text
POST /api/courses   {"title": "Java"}   -> 201 Created
GET  /api/courses/6                     -> 200 OK
GET  /api/courses/999                   -> 404 Not Found
```

### Key takeaways
- Use nouns in URLs and let the method express the action.
- Return accurate status codes so clients can react correctly.
- Validate every input on the server, even if the frontend checks it too.

## module: Full Stack Project Development
video: vUXKot0bpWQ

### Introduction
A full stack project brings everything together. The main skill here is planning work so that frontend, backend and database connect smoothly.

### Key concepts
- **Plan features first:** decide what users can do before writing code.
- **Design the data model:** decide on tables and relationships.
- **Agree on the API:** the contract between frontend and backend.
- **Build vertical slices:** finish one feature end to end before starting the next.
- **Deploy early:** find environment problems while they are still small.

### Definitions
- **MVP (Minimum Viable Product):** the smallest version that is useful.
- **CORS:** browser rules controlling cross-origin requests.
- **Environment variable:** configuration kept outside the code.

### Example
A course-catalog MVP might have three slices: register and log in, list and view courses, and enroll in a course. Each slice includes its table, API endpoint and React page.

### Key takeaways
- Finish small working slices rather than half-building everything.
- Use Git and commit after each working step.
- Write down setup steps in a README so others can run your project.
