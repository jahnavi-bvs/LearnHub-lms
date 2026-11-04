import { useState } from 'react';
import { Link } from 'react-router-dom';
import { listAllCourses, unpublishCourse } from '../api/admin';
import { useAuth } from '../auth/useAuth';
import { useAsync } from '../hooks/useAsync';

/** Every instructor's courses. Unpublishing hides a course from the catalog and deletes nothing. */
export default function ManageCoursesPage() {
  const { token } = useAuth();
  const courses = useAsync(() => listAllCourses(token), [token]);
  const [changed, setChanged] = useState({}); // id -> the server's answer after unpublishing
  const [busyId, setBusyId] = useState(null);
  const [error, setError] = useState(null);

  async function unpublish(course) {
    if (!window.confirm(`Unpublish "${course.title}"? It will disappear from the catalog, and enrolled students lose access to its lessons.`)) return;
    setBusyId(course.id);
    setError(null);
    try {
      const updated = await unpublishCourse(course.id, token);
      setChanged((previous) => ({ ...previous, [course.id]: updated }));
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyId(null);
    }
  }

  if (courses.loading) return <p className="muted">Loading courses…</p>;
  if (courses.error) return <p className="alert alert-error" role="alert">{courses.error.message}</p>;

  const rows = courses.data.map((c) => changed[c.id] ?? c);

  return (
    <section>
      <h1>Manage courses</h1>
      {error && <p className="alert alert-error" role="alert">{error}</p>}
      {rows.length === 0 ? (
        <p className="muted">No courses have been created yet.</p>
      ) : (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th scope="col">Course</th>
                <th scope="col">Instructor</th>
                <th scope="col">Category</th>
                <th scope="col">Status</th>
                <th scope="col">Actions</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((c) => (
                <tr key={c.id}>
                  <td><Link to={`/courses/${c.id}`}>{c.title}</Link></td>
                  <td>{c.instructorName}</td>
                  <td>{c.category ?? '–'}</td>
                  <td>
                    <span className={c.published ? 'badge badge-published' : 'badge'}>
                      {c.published ? 'Published' : 'Unpublished'}
                    </span>
                  </td>
                  <td>
                    {c.published ? (
                      <button type="button" className="button button-danger" disabled={busyId === c.id}
                              onClick={() => unpublish(c)}>
                        Unpublish
                      </button>
                    ) : (
                      <Link to={`/teaching/courses/${c.id}/edit`}>Edit</Link>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
