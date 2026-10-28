import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <section>
      <h1>Page not found</h1>
      <p className="muted">There is nothing at this address.</p>
      <Link to="/">Back to courses</Link>
    </section>
  );
}
