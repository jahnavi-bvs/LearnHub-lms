import { Link } from 'react-router-dom';
import Icon from './Icon';

/** The round arrow at the end of a module or quiz row. label is what screen readers announce. */
export default function ArrowLink({ to, label }) {
  return (
    <Link to={to} className="arrow-link" aria-label={label} title={label}>
      <Icon name="arrow" size={18} />
    </Link>
  );
}
