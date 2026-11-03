import { Link } from 'react-router-dom';
import { mySubmissions } from '../api/assessments';
import { myEnrollments } from '../api/enrollments';
import { useAuth } from '../auth/useAuth';
import Icon from '../components/Icon';
import StatCard from '../components/StatCard';
import { useAsync } from '../hooks/useAsync';

export default function StudentDashboard() {
  const { token } = useAuth();
  // Promise.all: both requests run at the same time; we wait for both
  const { data, loading, error } = useAsync(
    () => Promise.all([myEnrollments(token), mySubmissions(token)]),
    [token],
  );

  if (loading) return <p className="muted">Loading your dashboard…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  const [enrollments, results] = data;
  const average = results.length === 0
    ? '—'
    : `${(results.reduce((sum, r) => sum + r.percentage, 0) / results.length).toFixed(1)}%`;

  return (
    <>
      <h1>Your learning</h1>
      <div className="stats">
        <StatCard icon="layers" label="Enrolled courses" value={enrollments.length} />
        <StatCard icon="quiz" label="Quizzes completed" value={results.length} />
        <StatCard icon="chart" label="Average score" value={average} />
      </div>

      <section>
        <div className="section-header">
          <h2>Continue learning</h2>
          <Link to="/my-learning">View all</Link>
        </div>
        {enrollments.length === 0 ? (
          <div className="empty-state">
            <p>You haven't enrolled in any courses yet.</p>
            <Link to="/" className="button">Browse courses</Link>
          </div>
        ) : (
          <ul className="list">
            {enrollments.slice(0, 5).map((e) => (
              <li key={e.id} className="list-item list-link">
                <div>
                  <Link to={`/courses/${e.courseId}`} className="list-title stretched-link">{e.courseTitle}</Link>
                  <p className="muted small">Enrolled {new Date(e.enrolledAt).toLocaleDateString()}</p>
                </div>
                <Icon name="chevron" />
              </li>
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
