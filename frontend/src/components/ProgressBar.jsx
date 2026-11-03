/** "3 of 8 modules" and "38%" above a bar. progress is the API's ProgressResponse. */
export default function ProgressBar({ progress }) {
  const { completedCount, totalModules, percentage } = progress;
  return (
    <div className="progress">
      <div className="progress-label">
        <span>{completedCount} of {totalModules} modules</span>
        <strong>{percentage}%</strong>
      </div>
      <div className="progress-track" role="progressbar" aria-valuenow={percentage} aria-valuemin={0}
           aria-valuemax={100} aria-label="Course progress">
        <div className="progress-fill" style={{ width: `${percentage}%` }} />
      </div>
    </div>
  );
}
