import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';
import FormField from '../components/FormField';

export default function RegisterPage() {
  const { register, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({ fullName: '', email: '', password: '', role: 'STUDENT' });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});

  if (isAuthenticated) return <Navigate to="/dashboard" replace />;

  function handleChange(event) {
    setForm({ ...form, [event.target.name]: event.target.value });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    setFieldErrors({});
    try {
      await register(form); // registers, then logs in
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err.message);
      setFieldErrors(err.errors ?? {});
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="form-page">
      <h1>Create an account</h1>
      <form onSubmit={handleSubmit} className="form" noValidate>
        {error && <p className="alert alert-error" role="alert">{error}</p>}
        <FormField label="Full name" name="fullName" autoComplete="name"
                   value={form.fullName} onChange={handleChange} error={fieldErrors.fullName} />
        <FormField label="Email" name="email" type="email" autoComplete="email"
                   value={form.email} onChange={handleChange} error={fieldErrors.email} />
        <FormField label="Password (8-72 characters)" name="password" type="password" autoComplete="new-password"
                   value={form.password} onChange={handleChange} error={fieldErrors.password} />

        <fieldset className="field">
          <legend>I am a</legend>
          <label className="radio">
            <input type="radio" name="role" value="STUDENT"
                   checked={form.role === 'STUDENT'} onChange={handleChange} />
            Student
          </label>
          <label className="radio">
            <input type="radio" name="role" value="INSTRUCTOR"
                   checked={form.role === 'INSTRUCTOR'} onChange={handleChange} />
            Instructor
          </label>
          {fieldErrors.roleAllowed && <p className="field-error">{fieldErrors.roleAllowed}</p>}
        </fieldset>

        <button type="submit" className="button" disabled={submitting}>
          {submitting ? 'Creating account…' : 'Create account'}
        </button>
      </form>
      <p className="muted">Already registered? <Link to="/login">Log in</Link></p>
    </section>
  );
}
