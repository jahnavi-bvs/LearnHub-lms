import { Link } from 'react-router-dom';
import { listAssessmentsForCourse } from '../api/assessments';
import { myCourses } from '../api/courses';
import { useAuth } from '../auth/useAuth';
import ArrowLink from '../components/ArrowLink';
import { useAsync } from '../hooks/useAsync';

async function loadQuizzes(token) {
  const courses = await myCourses(token);
  const quizzes = await Promise.all(courses.map((c) => listAssessmentsForCourse(c.id, token)));
  return courses.map((course, i) => ({ course, quizzes: quizzes[i] }));
}

/** The instructor's quizzes grouped by course, with links to create, review and see submissions. */
export default function TeachingQuizzesPage() {
  const { token } = useAuth();
  const { data: groups, loading, error } = useAsync(() => loadQuizzes(token), [token]);

  if (loading) return <p className="muted">Loading quizzes…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  if (groups.length === 0) {
    return (
      <section>
        <h1>Quizzes</h1>
        <p className="muted">Create a course first, then add quizzes to it. <Link to="/teaching/new">Create course</Link></p>
      </section>
    );
  }

  return (
    <section>
      <h1>Quizzes</h1>
      {groups.map(({ course, quizzes }) => (
        <section key={course.id} className="group">
          <div className="section-header">
            <h2><Link to={`/courses/${course.id}`}>{course.title}</Link></h2>
            <Link to={`/teaching/courses/${course.id}/quizzes/new`} className="button button-secondary">New quiz</Link>
          </div>
          {quizzes.length === 0 ? (
            <p className="muted">No quizzes yet.</p>
          ) : (
            <ul className="list">
              {quizzes.map((quiz) => (
                <li key={quiz.id} className="list-item">
                  <div>
                    <span className="list-title">{quiz.title}</span>
                    <p className="muted small">{quiz.questionCount} questions</p>
                  </div>
                  <span className="row-actions">
                    <Link to={`/teaching/assessments/${quiz.id}/submissions`}>Submissions</Link>
                    <ArrowLink to={`/assessments/${quiz.id}`} label={`Open ${quiz.title}`} />
                  </span>
                </li>
              ))}
            </ul>
          )}
        </section>
      ))}
    </section>
  );
}
