import { useState } from 'react';
import { Link } from 'react-router-dom';
import { deleteCourse, myCourses } from '../api/courses';
import { useAuth } from '../auth/useAuth';
import CourseCard from '../components/CourseCard';
import { useAsync } from '../hooks/useAsync';

/** The instructor's own courses, drafts included. */
export default function TeachingPage() {
  const { token } = useAuth();
  const { data: courses, loading, error, reload } = useAsync(() => myCourses(token), [token]);
  const [deletingId, setDeletingId] = useState(null);
  const [actionError, setActionError] = useState(null);

  async function handleDelete(course) {
    if (!window.confirm(`Delete "${course.title}"? This cannot be undone.`)) return;
    setDeletingId(course.id);
    setActionError(null);
    try {
      await deleteCourse(course.id, token);
      reload();
    } catch (err) {
      setActionError(err.message);
    } finally {
      setDeletingId(null);
    }
  }

  if (loading) return <p className="muted">Loading your courses…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  return (
    <section>
      <div className="page-header">
        <h1>My courses</h1>
        <Link to="/teaching/new" className="button">New course</Link>
      </div>
      {actionError && <p className="alert alert-error" role="alert">{actionError}</p>}

      {courses.length === 0 ? (
        <p className="muted">You haven't created any courses yet.</p>
      ) : (
        <ul className="card-grid">
          {courses.map((c) => (
            <CourseCard key={c.id} course={c} showMeta={false} badge={c.published ? null : 'Draft'}>
              <Link to={`/teaching/courses/${c.id}/edit`} className="button button-secondary">Edit</Link>
              <button type="button" className="button button-danger"
                      onClick={() => handleDelete(c)} disabled={deletingId === c.id}>
                {deletingId === c.id ? 'Deleting…' : 'Delete'}
              </button>
            </CourseCard>
          ))}
        </ul>
      )}
    </section>
  );
}
