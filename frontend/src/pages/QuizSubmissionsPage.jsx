import { Link, useParams } from 'react-router-dom';
import { getAssessment, listSubmissions } from '../api/assessments';
import { useAuth } from '../auth/useAuth';
import { useAsync } from '../hooks/useAsync';

/** Every student's score on one quiz - for the course's instructor or an admin. */
export default function QuizSubmissionsPage() {
  const { id } = useParams();
  const assessmentId = Number(id);
  const { token } = useAuth();
  const assessment = useAsync(() => getAssessment(assessmentId, token), [assessmentId, token]);
  const submissions = useAsync(() => listSubmissions(assessmentId, token), [assessmentId, token]);

  if (assessment.loading || submissions.loading) return <p className="muted">Loading submissions…</p>;
  const error = assessment.error ?? submissions.error;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  const list = submissions.data;
  // Computed from the data we have - no extra endpoint needed
  const average = list.length === 0
    ? null
    : (list.reduce((sum, s) => sum + s.percentage, 0) / list.length).toFixed(1);

  return (
    <section>
      <p><Link to={`/assessments/${assessmentId}`}>← {assessment.data.title}</Link></p>
      <h1>Submissions</h1>

      {list.length === 0 ? (
        <p className="muted">No students have submitted this quiz yet.</p>
      ) : (
        <>
          <p className="muted">{list.length} submission{list.length === 1 ? '' : 's'} · class average {average}%</p>
          <ul className="list">
            {list.map((s) => (
              <li key={s.id} className="list-item">
                <div>
                  <span className="list-title">{s.studentName}</span>
                  <p className="muted small">Submitted {new Date(s.submittedAt).toLocaleString()}</p>
                </div>
                <span className="score-pill">{s.score} / {s.maxScore} · {s.percentage}%</span>
              </li>
            ))}
          </ul>
        </>
      )}
    </section>
  );
}
