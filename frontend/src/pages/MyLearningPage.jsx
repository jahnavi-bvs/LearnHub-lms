import { useState } from 'react';
import { Link } from 'react-router-dom';
import { dropEnrollment, myEnrollments } from '../api/enrollments';
import { useAuth } from '../auth/useAuth';
import { useAsync } from '../hooks/useAsync';

export default function MyLearningPage() {
  const { token } = useAuth();
  const { data: enrollments, loading, error, reload } = useAsync(() => myEnrollments(token), [token]);
  const [droppingId, setDroppingId] = useState(null); // which row's button is busy
  const [actionError, setActionError] = useState(null);

  async function handleDrop(enrollment) {
    if (!window.confirm(`Drop "${enrollment.courseTitle}"?`)) return;
    setDroppingId(enrollment.id);
    setActionError(null);
    try {
      await dropEnrollment(enrollment.id, token);
      reload();
    } catch (err) {
      setActionError(err.message);
    } finally {
      setDroppingId(null);
    }
  }

  if (loading) return <p className="muted">Loading your courses…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  return (
    <section>
      <h1>My learning</h1>
      {actionError && <p className="alert alert-error" role="alert">{actionError}</p>}
      {enrollments.length === 0 ? (
        <p className="muted">
          You are not enrolled in any courses yet. <Link to="/">Browse courses</Link>
        </p>
      ) : (
        <ul className="list">
          {enrollments.map((e) => (
            <li key={e.id} className="list-item">
              <div>
                <Link to={`/courses/${e.courseId}`} className="list-title">{e.courseTitle}</Link>
                <p className="muted small">Enrolled {new Date(e.enrolledAt).toLocaleDateString()}</p>
              </div>
              <button type="button" className="button button-secondary"
                      onClick={() => handleDrop(e)} disabled={droppingId === e.id}>
                {droppingId === e.id ? 'Dropping…' : 'Drop'}
              </button>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
