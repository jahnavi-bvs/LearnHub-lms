import { useNavigate, useSearchParams } from 'react-router-dom';
import Icon from './Icon';

/**
 * Course search. The search term lives in the URL (/?q=java), not in React state:
 * the catalog page reads it from there, and Back/bookmarks just work.
 */
export default function SearchBox() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const current = params.get('q') ?? '';

  function handleSubmit(event) {
    event.preventDefault();
    const q = new FormData(event.currentTarget).get('q').toString().trim();
    navigate(q ? `/?q=${encodeURIComponent(q)}` : '/');
  }

  return (
    <form role="search" className="search" onSubmit={handleSubmit}>
      <Icon name="search" size={18} />
      {/* key: when the URL's q changes (e.g. Back button), re-create the input with the new value */}
      <input key={current} type="search" name="q" defaultValue={current}
             placeholder="Search courses" aria-label="Search courses" />
    </form>
  );
}
