import { Link } from 'react-router-dom';
import { mySubmissions } from '../api/assessments';
import { loadMyCourses } from '../api/myCourses';
import { useAuth } from '../auth/useAuth';
import CourseCard from '../components/CourseCard';
import StatCard from '../components/StatCard';
import { useAsync } from '../hooks/useAsync';

const CONTINUE_LEARNING_COUNT = 4;

export default function StudentDashboard() {
  const { token } = useAuth();
  // Promise.all: both requests run at the same time; we wait for both
  const { data, loading, error } = useAsync(
    () => Promise.all([loadMyCourses(token), mySubmissions(token)]),
    [token],
  );

  if (loading) return <p className="muted">Loading your dashboard…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  const [rows, results] = data;
  const average = results.length === 0
    ? '—'
    : `${(results.reduce((sum, r) => sum + r.percentage, 0) / results.length).toFixed(1)}%`;

  return (
    <>
      <h1>Your learning</h1>
      <div className="stats">
        <StatCard icon="layers" label="Enrolled courses" value={rows.length} />
        <StatCard icon="quiz" label="Quizzes completed" value={results.length} />
        <StatCard icon="chart" label="Average score" value={average} />
      </div>

      <section>
        <div className="section-header">
          <h2>Continue learning</h2>
          <Link to="/my-learning">View all</Link>
        </div>
        {rows.length === 0 ? (
          <div className="empty-state">
            <p>You haven't enrolled in any courses yet.</p>
            <Link to="/" className="button">Browse courses</Link>
          </div>
        ) : (
          <ul className="card-grid">
            {rows.slice(0, CONTINUE_LEARNING_COUNT).map(({ enrollment, course }) => (
              <CourseCard key={enrollment.id} course={course} showMeta={false} />
            ))}
          </ul>
        )}
      </section>

      {results.length > 0 && (
        <section>
          <div className="section-header">
            <h2>Recent scores</h2>
            <Link to="/my-results">View all</Link>
          </div>
          <ul className="list">
            {results.slice(0, 3).map((r) => (
              <li key={r.id} className="list-item">
                <Link to={`/assessments/${r.assessmentId}`} className="list-title">{r.assessmentTitle}</Link>
                <span className="score-pill">{r.score} / {r.maxScore} · {r.percentage}%</span>
              </li>
            ))}
          </ul>
        </section>
      )}
    </>
  );
}
