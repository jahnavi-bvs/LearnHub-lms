import Icon from './Icon';

/** One number with a label, as in the dashboard sketches. */
export default function StatCard({ icon, label, value, hint }) {
  return (
    <div className="stat-card">
      <span className="stat-icon"><Icon name={icon} /></span>
      <div>
        <p className="stat-value">{value}</p>
        <p className="stat-label">{label}</p>
        {hint && <p className="stat-hint">{hint}</p>}
      </div>
    </div>
  );
}
