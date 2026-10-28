import { useState } from 'react';
import { submitAnswers } from '../api/assessments';

/**
 * One radio group per question. Calls onSubmitted(result) after a successful submit,
 * or onAlreadySubmitted() if the server says this student has already submitted (409).
 */
export default function QuizForm({ assessment, token, onSubmitted, onAlreadySubmitted }) {
  // One entry per question: the chosen option index, or null while unanswered
  const [answers, setAnswers] = useState(() => assessment.questions.map(() => null));
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const answeredCount = answers.filter((a) => a !== null).length;
  const allAnswered = answeredCount === answers.length;

  function choose(questionIndex, optionIndex) {
    // Build a NEW array - React only re-renders when it gets a new value, not a mutated one
    setAnswers((previous) => previous.map((a, i) => (i === questionIndex ? optionIndex : a)));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (!window.confirm('Submit your answers? You only get one attempt.')) return;
    setSubmitting(true);
    setError(null);
    try {
      const result = await submitAnswers(assessment.id, answers, token);
      onSubmitted(result);
    } catch (err) {
      if (err.status === 409) {
        onAlreadySubmitted(); // e.g. submitted from another tab - show that result instead
        return;
      }
      setError(err.message);
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="quiz">
      {assessment.questions.map((question, qIndex) => (
        <fieldset key={question.id} className="question">
          <legend>
            <span className="question-number">Question {qIndex + 1}</span>
            {question.text}
          </legend>
          {question.options.map((option, oIndex) => (
            <label key={oIndex} className="option">
              <input
                type="radio"
                name={`question-${question.id}`}
                checked={answers[qIndex] === oIndex}
                onChange={() => choose(qIndex, oIndex)}
              />
              {option}
            </label>
          ))}
        </fieldset>
      ))}

      {error && <p className="alert alert-error" role="alert">{error}</p>}

      <div className="quiz-footer">
        <span className="muted">{answeredCount} of {answers.length} answered</span>
        <button type="submit" className="button" disabled={!allAnswered || submitting}>
          {submitting ? 'Submitting…' : 'Submit answers'}
        </button>
      </div>
    </form>
  );
}
