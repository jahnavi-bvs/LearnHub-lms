import { Link } from 'react-router-dom';
import { mySubmissions } from '../api/assessments';
import { useAuth } from '../auth/useAuth';
import { useAsync } from '../hooks/useAsync';

export default function MyResultsPage() {
  const { token } = useAuth();
  const { data: results, loading, error } = useAsync(() => mySubmissions(token), [token]);

  if (loading) return <p className="muted">Loading your results…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  return (
    <section>
      <h1>My scores</h1>
      {results.length === 0 ? (
        <p className="muted">You haven't submitted any quizzes yet.</p>
      ) : (
        <ul className="list">
          {results.map((r) => (
            <li key={r.id} className="list-item">
              <div>
                <Link to={`/assessments/${r.assessmentId}`} className="list-title">{r.assessmentTitle}</Link>
                <p className="muted small">Submitted {new Date(r.submittedAt).toLocaleString()}</p>
              </div>
              <span className="score-pill">{r.score} / {r.maxScore} · {r.percentage}%</span>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
