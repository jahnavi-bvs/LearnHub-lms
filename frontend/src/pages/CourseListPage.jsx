import { useEffect, useState } from 'react';
import { listCourses } from '../api/courses';

export default function CourseListPage() {
  // Three pieces of state = the three possible states of a request
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Runs after the first render. The empty [] means "only once, not on every re-render".
  useEffect(() => {
    // If the page is left before the response arrives, don't update state that no longer matters.
    // (In development, StrictMode runs this effect twice on purpose - this flag handles that too.)
    let ignore = false;

    listCourses()
      .then((data) => { if (!ignore) setCourses(data); })
      .catch((err) => { if (!ignore) setError(err.message); })
      .finally(() => { if (!ignore) setLoading(false); });

    return () => { ignore = true; }; // cleanup function
  }, []);

  if (loading) return <p className="muted">Loading courses…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error}</p>;

  return (
    <section>
      <h1>Courses</h1>
      {courses.length === 0 ? (
        <p className="muted">No courses have been published yet.</p>
      ) : (
        <ul className="card-grid">
          {/* key lets React track each item between re-renders - use a stable id, not the index */}
          {courses.map((course) => (
            <li key={course.id} className="card">
              <h2 className="card-title">{course.title}</h2>
              <p className="card-meta">by {course.instructorName}</p>
              {course.description && <p className="card-text">{course.description}</p>}
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
