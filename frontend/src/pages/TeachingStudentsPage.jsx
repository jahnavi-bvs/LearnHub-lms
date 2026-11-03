import { Link } from 'react-router-dom';
import { myCourses } from '../api/courses';
import { courseRoster } from '../api/enrollments';
import { useAuth } from '../auth/useAuth';
import { useAsync } from '../hooks/useAsync';

/** Every enrollment across the instructor's courses, newest first. */
async function loadStudents(token) {
  const courses = await myCourses(token);
  const rosters = await Promise.all(courses.map((c) => courseRoster(c.id, token)));
  return rosters.flat().sort((a, b) => b.enrolledAt.localeCompare(a.enrolledAt)); // ISO dates sort as text
}

export default function TeachingStudentsPage() {
  const { token } = useAuth();
  const { data: enrollments, loading, error } = useAsync(() => loadStudents(token), [token]);

  if (loading) return <p className="muted">Loading students…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  return (
    <section>
      <h1>Students</h1>
      {enrollments.length === 0 ? (
        <p className="muted">No students have enrolled in your courses yet.</p>
      ) : (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th scope="col">Student</th>
                <th scope="col">Course</th>
                <th scope="col">Enrolled</th>
              </tr>
            </thead>
            <tbody>
              {enrollments.map((e) => (
                <tr key={e.id}>
                  <td>{e.studentName}</td>
                  <td><Link to={`/courses/${e.courseId}`}>{e.courseTitle}</Link></td>
                  <td>{new Date(e.enrolledAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
