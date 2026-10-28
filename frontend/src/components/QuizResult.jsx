/** A graded submission: overall score, then each question with the student's and the correct answer. */
export default function QuizResult({ result }) {
  return (
    <div className="quiz">
      <div className="score-card">
        <p className="score-value">{result.score} / {result.maxScore}</p>
        <p className="score-percent">{result.percentage}%</p>
        <p className="muted small">Submitted {new Date(result.submittedAt).toLocaleString()}</p>
      </div>

      {result.results.map((r, qIndex) => (
        <section key={r.questionId} className={`question ${r.correct ? 'question-correct' : 'question-wrong'}`}>
          <h3 className="question-heading">
            <span className="question-number">
              Question {qIndex + 1} · {r.correct ? 'Correct' : 'Incorrect'}
            </span>
            {r.questionText}
          </h3>
          <ul className="option-list">
            {r.options.map((option, oIndex) => {
              const isCorrect = oIndex === r.correctOptionIndex;
              const isChosen = oIndex === r.selectedOptionIndex;
              let className = 'option-result';
              if (isCorrect) className += ' option-correct';
              else if (isChosen) className += ' option-wrong';
              return (
                <li key={oIndex} className={className}>
                  {option}
                  {isChosen && <span className="option-tag">Your answer</span>}
                  {isCorrect && !isChosen && <span className="option-tag">Correct answer</span>}
                </li>
              );
            })}
          </ul>
        </section>
      ))}
    </div>
  );
}
