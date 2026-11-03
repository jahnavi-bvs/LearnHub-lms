import { Link } from 'react-router-dom';
import { listAssessmentsForCourse } from '../api/assessments';
import { myCourses } from '../api/courses';
import { courseRoster } from '../api/enrollments';
import { useAuth } from '../auth/useAuth';
import StatCard from '../components/StatCard';
import { useAsync } from '../hooks/useAsync';

/** Loads the instructor's courses, then each course's roster and quizzes in parallel. */
async function loadOverview(token) {
  const courses = await myCourses(token);
  const [rosters, quizzes] = await Promise.all([
    Promise.all(courses.map((c) => courseRoster(c.id, token))),
    Promise.all(courses.map((c) => listAssessmentsForCourse(c.id, token))),
  ]);
  return courses.map((course, i) => ({ course, roster: rosters[i], quizCount: quizzes[i].length }));
}

export default function InstructorDashboard() {
  const { token } = useAuth();
  const { data: rows, loading, error } = useAsync(() => loadOverview(token), [token]);

  if (loading) return <p className="muted">Loading your dashboard…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  const published = rows.filter((r) => r.course.published).length;
  // A Set keeps each student once, even if they're enrolled in several of your courses
  const uniqueStudents = new Set(rows.flatMap((r) => r.roster.map((e) => e.studentId))).size;
  const totalQuizzes = rows.reduce((sum, r) => sum + r.quizCount, 0);

  return (
    <>
      <div className="page-header">
        <h1>Teaching overview</h1>
        <Link to="/teaching/new" className="button">Create course</Link>
      </div>
      <div className="stats">
        <StatCard icon="layers" label="Courses" value={rows.length}
                  hint={`${published} published · ${rows.length - published} draft`} />
        <StatCard icon="users" label="Students" value={uniqueStudents} />
        <StatCard icon="quiz" label="Quizzes" value={totalQuizzes} />
      </div>

      <section>
        <h2 className="section-title">Your courses</h2>
        {rows.length === 0 ? (
          <div className="empty-state">
            <p>You haven't created any courses yet.</p>
            <Link to="/teaching/new" className="button">Create your first course</Link>
          </div>
        ) : (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th scope="col">Course</th>
                  <th scope="col">Status</th>
                  <th scope="col" className="num">Students</th>
                  <th scope="col" className="num">Quizzes</th>
                </tr>
              </thead>
              <tbody>
                {rows.map(({ course, roster, quizCount }) => (
                  <tr key={course.id}>
                    <td><Link to={`/courses/${course.id}`}>{course.title}</Link></td>
                    <td>
                      <span className={course.published ? 'badge badge-published' : 'badge'}>
                        {course.published ? 'Published' : 'Draft'}
                      </span>
                    </td>
                    <td className="num">{roster.length}</td>
                    <td className="num">{quizCount}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </>
  );
}
