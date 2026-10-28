import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getAssessment, getMySubmission } from '../api/assessments';
import { useAuth } from '../auth/useAuth';
import AnswerKey from '../components/AnswerKey';
import QuizForm from '../components/QuizForm';
import QuizResult from '../components/QuizResult';
import { useAsync } from '../hooks/useAsync';

/**
 * One URL, three views:
 *   student, not submitted yet -> QuizForm
 *   student, already submitted -> QuizResult
 *   course instructor / admin  -> AnswerKey
 */
export default function AssessmentPage() {
  const { id } = useParams();
  const assessmentId = Number(id);
  const { user, token } = useAuth();
  const isStudent = user.role === 'STUDENT';

  const assessment = useAsync(() => getAssessment(assessmentId, token), [assessmentId, token]);
  const mySubmission = useAsync(
    () => (isStudent ? getMySubmission(assessmentId, token) : Promise.resolve(null)),
    [isStudent, assessmentId, token],
  );
  // Set right after submitting, so we can show the result without another request
  const [justSubmitted, setJustSubmitted] = useState(null);

  if (assessment.loading || mySubmission.loading) return <p className="muted">Loading quiz…</p>;
  if (assessment.error || mySubmission.error) {
    const error = assessment.error ?? mySubmission.error;
    return (
      <section>
        <p className="alert alert-error" role="alert">{error.message}</p>
        <Link to="/">Back to courses</Link>
      </section>
    );
  }

  const a = assessment.data;
  const result = justSubmitted ?? mySubmission.data;

  return (
    <article className="detail">
      <p><Link to={`/courses/${a.courseId}`}>← Back to course</Link></p>
      <h1>{a.title}</h1>
      {a.description && <p className="detail-text">{a.description}</p>}

      {!isStudent && <AnswerKey assessment={a} />}
      {isStudent && result && <QuizResult result={result} />}
      {isStudent && !result && (
        <QuizForm
          assessment={a}
          token={token}
          onSubmitted={setJustSubmitted}
          onAlreadySubmitted={mySubmission.reload}
        />
      )}
    </article>
  );
}
