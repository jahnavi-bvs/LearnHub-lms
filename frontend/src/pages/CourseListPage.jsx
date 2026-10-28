import { Link } from 'react-router-dom';
import { listCourses } from '../api/courses';
import { useAsync } from '../hooks/useAsync';

export default function CourseListPage() {
  // The loading/error/data pattern now lives in the useAsync hook
  const { data: courses, loading, error } = useAsync(() => listCourses(), []);

  if (loading) return <p className="muted">Loading courses…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

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
              <h2 className="card-title">
                <Link to={`/courses/${course.id}`}>{course.title}</Link>
              </h2>
              <p className="card-meta">by {course.instructorName}</p>
              {course.description && <p className="card-text">{course.description}</p>}
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
