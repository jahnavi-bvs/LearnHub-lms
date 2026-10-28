import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';
import FormField from '../components/FormField';

export default function LoginPage() {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  // Where RequireAuth was sending the user before redirecting here
  const from = location.state?.from?.pathname || '/';

  // Controlled form: the inputs' values live in state
  const [form, setForm] = useState({ email: '', password: '' });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});

  if (isAuthenticated) return <Navigate to={from} replace />;

  function handleChange(event) {
    setForm({ ...form, [event.target.name]: event.target.value });
  }

  async function handleSubmit(event) {
    event.preventDefault(); // stop the browser's default full-page form submit
    setSubmitting(true);
    setError(null);
    setFieldErrors({});
    try {
      await login(form.email, form.password);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.message);
      setFieldErrors(err.errors ?? {});
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="form-page">
      <h1>Log in</h1>
      <form onSubmit={handleSubmit} className="form" noValidate>
        {error && <p className="alert alert-error" role="alert">{error}</p>}
        <FormField label="Email" name="email" type="email" autoComplete="email"
                   value={form.email} onChange={handleChange} error={fieldErrors.email} />
        <FormField label="Password" name="password" type="password" autoComplete="current-password"
                   value={form.password} onChange={handleChange} error={fieldErrors.password} />
        {/* Disabled while waiting: stops double-submits */}
        <button type="submit" className="button" disabled={submitting}>
          {submitting ? 'Logging in…' : 'Log in'}
        </button>
      </form>
      <p className="muted">No account yet? <Link to="/register">Register</Link></p>
    </section>
  );
}
