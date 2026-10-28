/** Instructor/admin view of a quiz: every question with its correct option highlighted. */
export default function AnswerKey({ assessment }) {
  return (
    <div className="quiz">
      <p className="alert alert-success">Instructor view: correct answers are highlighted.</p>
      {assessment.questions.map((question, qIndex) => (
        <section key={question.id} className="question">
          <h3 className="question-heading">
            <span className="question-number">Question {qIndex + 1}</span>
            {question.text}
          </h3>
          <ul className="option-list">
            {question.options.map((option, oIndex) => (
              <li key={oIndex}
                  className={`option-result${oIndex === question.correctOptionIndex ? ' option-correct' : ''}`}>
                {option}
                {oIndex === question.correctOptionIndex && <span className="option-tag">Correct answer</span>}
              </li>
            ))}
          </ul>
        </section>
      ))}
    </div>
  );
}
