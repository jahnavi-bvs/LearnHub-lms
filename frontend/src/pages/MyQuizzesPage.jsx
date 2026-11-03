import { Link } from 'react-router-dom';
import { listAssessmentsForCourse, mySubmissions } from '../api/assessments';
import { myEnrollments } from '../api/enrollments';
import { useAuth } from '../auth/useAuth';
import { useAsync } from '../hooks/useAsync';

/** Every quiz in every enrolled course, marked "taken" (with score) or "not taken". */
async function loadQuizzes(token) {
  const [enrollments, submissions] = await Promise.all([myEnrollments(token), mySubmissions(token)]);
  // Look up "have I submitted this quiz?" by assessment id in one step
  const resultByQuiz = new Map(submissions.map((s) => [s.assessmentId, s]));
  const groups = await Promise.all(enrollments.map(async (enrollment) => ({
    enrollment,
    quizzes: await listAssessmentsForCourse(enrollment.courseId, token),
  })));
  return { groups, resultByQuiz };
}

export default function MyQuizzesPage() {
  const { token } = useAuth();
  const { data, loading, error } = useAsync(() => loadQuizzes(token), [token]);

  if (loading) return <p className="muted">Loading your quizzes…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  const { groups, resultByQuiz } = data;

  if (groups.length === 0) {
    return (
      <section>
        <h1>Quizzes</h1>
        <p className="muted">Enroll in a course to see its quizzes. <Link to="/">Browse courses</Link></p>
      </section>
    );
  }

  return (
    <section>
      <h1>Quizzes</h1>
      {groups.map(({ enrollment, quizzes }) => (
        <section key={enrollment.id} className="group">
          <h2 className="section-title">
            <Link to={`/courses/${enrollment.courseId}`}>{enrollment.courseTitle}</Link>
          </h2>
          {quizzes.length === 0 ? (
            <p className="muted">No quizzes in this course yet.</p>
          ) : (
            <ul className="list">
              {quizzes.map((quiz) => {
                const result = resultByQuiz.get(quiz.id);
                return (
                  <li key={quiz.id} className="list-item">
                    <div>
                      <span className="list-title">{quiz.title}</span>
                      <p className="muted small">{quiz.questionCount} questions</p>
                    </div>
                    {result ? (
                      <Link to={`/assessments/${quiz.id}`} className="score-pill">
                        {result.score} / {result.maxScore} · {result.percentage}%
                      </Link>
                    ) : (
                      <Link to={`/assessments/${quiz.id}`} className="button">Take quiz</Link>
                    )}
                  </li>
                );
              })}
            </ul>
          )}
        </section>
      ))}
    </section>
  );
}
