import { useEffect, useState } from 'react';
import { getMe } from '../api/auth';
import { useAuth } from '../auth/useAuth';

/** Calls GET /api/auth/me with the token - proof that authenticated requests work. */
export default function ProfilePage() {
  const { token } = useAuth();
  const [me, setMe] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    let ignore = false;
    getMe(token)
      .then((data) => { if (!ignore) setMe(data); })
      .catch((err) => { if (!ignore) setError(err.message); });
    return () => { ignore = true; };
  }, [token]); // re-run if the token changes

  if (error) return <p className="alert alert-error" role="alert">{error}</p>;
  if (!me) return <p className="muted">Loading profile…</p>;

  return (
    <section>
      <h1>My profile</h1>
      <dl className="details">
        <dt>Name</dt>
        <dd>{me.fullName}</dd>
        <dt>Email</dt>
        <dd>{me.email}</dd>
        <dt>Role</dt>
        <dd>{me.role}</dd>
        <dt>Member since</dt>
        {/* createdAt is UTC; toLocaleDateString shows it in the user's own time zone */}
        <dd>{new Date(me.createdAt).toLocaleDateString()}</dd>
      </dl>
    </section>
  );
}
