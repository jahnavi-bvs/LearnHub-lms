import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { createAssessment } from '../api/assessments';
import { getCourse } from '../api/courses';
import { useAuth } from '../auth/useAuth';
import FormField from '../components/FormField';
import { useAsync } from '../hooks/useAsync';

const MIN_OPTIONS = 2;
const MAX_OPTIONS = 6;

// Every question and option gets a permanent key, so React keeps each input attached to the
// right item when others are added or removed (array indexes would shift and mix them up).
let nextKey = 1;
const newKey = () => nextKey++;
const newOption = () => ({ key: newKey(), text: '' });
const newQuestion = () => ({ key: newKey(), text: '', options: [newOption(), newOption()], correctOptionIndex: 0 });

export default function QuizBuilderPage() {
  const { id } = useParams();
  const courseId = Number(id);
  const { user, token } = useAuth();
  const navigate = useNavigate();
  const course = useAsync(() => getCourse(courseId, token), [courseId, token]);

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [questions, setQuestions] = useState(() => [newQuestion()]);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});

  // --- State updates: always build new arrays/objects, never modify the old ones ---

  function updateQuestion(qKey, changes) {
    setQuestions((qs) => qs.map((q) => (q.key === qKey ? { ...q, ...changes } : q)));
  }

  function addQuestion() {
    setQuestions((qs) => [...qs, newQuestion()]);
    setFieldErrors({}); // error paths like questions[2] would no longer match
  }

  function removeQuestion(qKey) {
    setQuestions((qs) => qs.filter((q) => q.key !== qKey));
    setFieldErrors({});
  }

  function updateOptionText(qKey, oKey, text) {
    setQuestions((qs) => qs.map((q) => (q.key !== qKey ? q : {
      ...q,
      options: q.options.map((o) => (o.key === oKey ? { ...o, text } : o)),
    })));
  }

  function addOption(qKey) {
    setQuestions((qs) => qs.map((q) => (q.key !== qKey ? q : { ...q, options: [...q.options, newOption()] })));
  }

  function removeOption(qKey, removeIndex) {
    setQuestions((qs) => qs.map((q) => {
      if (q.key !== qKey) return q;
      // Keep correctOptionIndex pointing at the same answer after the list shrinks
      let correct = q.correctOptionIndex;
      if (removeIndex === correct) correct = 0;       // the correct one was removed: pick the first
      else if (removeIndex < correct) correct -= 1;  // an earlier one was removed: shift down
      return { ...q, options: q.options.filter((_, i) => i !== removeIndex), correctOptionIndex: correct };
    }));
  }

  /** Backend errors for question i, e.g. keys "questions[1].text", "questions[1].options[0]". */
  function errorsForQuestion(index) {
    const prefix = `questions[${index}].`;
    return Object.entries(fieldErrors).filter(([path]) => path.startsWith(prefix)).map(([, message]) => message);
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    setFieldErrors({});
    try {
      // Convert UI state to exactly the shape the API expects (drop the React-only keys)
      const created = await createAssessment({
        courseId,
        title,
        description: description.trim() === '' ? null : description,
        questions: questions.map((q) => ({
          text: q.text,
          options: q.options.map((o) => o.text),
          correctOptionIndex: q.correctOptionIndex,
        })),
      }, token);
      navigate(`/assessments/${created.id}`); // the answer-key view of the new quiz
    } catch (err) {
      setError(err.message);
      setFieldErrors(err.errors ?? {});
      setSubmitting(false);
    }
  }

  if (course.loading) return <p className="muted">Loading course…</p>;
  if (course.error) return <p className="alert alert-error" role="alert">{course.error.message}</p>;
  if (course.data.instructorId !== user.id && user.role !== 'ADMIN') {
    return <p className="alert alert-error" role="alert">You can only add quizzes to your own courses.</p>;
  }

  return (
    <section className="form-page form-page-wide">
      <p><Link to={`/courses/${courseId}`}>← {course.data.title}</Link></p>
      <h1>New quiz</h1>

      <form onSubmit={handleSubmit} className="quiz-builder" noValidate>
        <div className="form">
          {error && <p className="alert alert-error" role="alert">{error}</p>}
          <FormField label="Quiz title" name="title" value={title}
                     onChange={(e) => setTitle(e.target.value)} error={fieldErrors.title} maxLength={200} />
          <FormField as="textarea" label="Description (optional)" name="description" rows={3}
                     value={description} onChange={(e) => setDescription(e.target.value)}
                     error={fieldErrors.description} />
          {fieldErrors.questions && <p className="field-error">{fieldErrors.questions}</p>}
        </div>

        {questions.map((q, qIndex) => {
          const questionErrors = errorsForQuestion(qIndex);
          return (
            <fieldset key={q.key} className="question builder-question">
              <legend>
                <span className="question-number">Question {qIndex + 1}</span>
              </legend>

              <FormField label="Question text" name={`q-${q.key}-text`} value={q.text}
                         onChange={(e) => updateQuestion(q.key, { text: e.target.value })} />

              <p className="builder-hint">Options - select the correct answer</p>
              {q.options.map((o, oIndex) => (
                <div key={o.key} className="builder-option">
                  <input type="radio" name={`q-${q.key}-correct`}
                         checked={q.correctOptionIndex === oIndex}
                         onChange={() => updateQuestion(q.key, { correctOptionIndex: oIndex })}
                         aria-label={`Option ${oIndex + 1} is the correct answer`} />
                  <input type="text" className="builder-option-text" value={o.text}
                         placeholder={`Option ${oIndex + 1}`}
                         onChange={(e) => updateOptionText(q.key, o.key, e.target.value)}
                         aria-label={`Question ${qIndex + 1}, option ${oIndex + 1}`} />
                  <button type="button" className="button button-danger"
                          onClick={() => removeOption(q.key, oIndex)}
                          disabled={q.options.length <= MIN_OPTIONS}
                          aria-label={`Remove option ${oIndex + 1}`}>
                    ✕
                  </button>
                </div>
              ))}

              {questionErrors.length > 0 && (
                <ul className="field-error builder-errors">
                  {questionErrors.map((message) => <li key={message}>{message}</li>)}
                </ul>
              )}

              <div className="row-actions">
                <button type="button" className="button button-secondary"
                        onClick={() => addOption(q.key)} disabled={q.options.length >= MAX_OPTIONS}>
                  Add option
                </button>
                <button type="button" className="button button-danger"
                        onClick={() => removeQuestion(q.key)} disabled={questions.length === 1}>
                  Remove question
                </button>
              </div>
            </fieldset>
          );
        })}

        <div className="quiz-footer">
          <button type="button" className="button button-secondary" onClick={addQuestion}>Add question</button>
          <button type="submit" className="button" disabled={submitting}>
            {submitting ? 'Creating quiz…' : `Create quiz (${questions.length} question${questions.length === 1 ? '' : 's'})`}
          </button>
        </div>
      </form>
    </section>
  );
}
