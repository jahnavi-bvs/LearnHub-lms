import { useState } from 'react';
import FormField from './FormField';

/**
 * Used by both the Create and Edit pages. It doesn't know which one it is:
 * the page passes the starting values and what to do on submit.
 *
 * @param initialValues { title, description, published } - read once, on the first render
 * @param onSubmit      async (values) => ...; throw (an ApiError) to show errors in the form
 */
export default function CourseForm({ initialValues, submitLabel, submittingLabel, onSubmit }) {
  const [form, setForm] = useState({
    title: initialValues?.title ?? '',
    description: initialValues?.description ?? '',
    published: initialValues?.published ?? false,
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});

  function handleChange(event) {
    const { name, type, value, checked } = event.target;
    // A checkbox's state is in .checked; text inputs use .value
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    setFieldErrors({});
    try {
      // Always send all three fields - PUT replaces the whole course
      await onSubmit({
        title: form.title,
        description: form.description.trim() === '' ? null : form.description,
        published: form.published,
      });
      // On success the page navigates away, so there's nothing to reset here
    } catch (err) {
      setError(err.message);
      setFieldErrors(err.errors ?? {});
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="form" noValidate>
      {error && <p className="alert alert-error" role="alert">{error}</p>}
      <FormField label="Title" name="title" value={form.title} onChange={handleChange}
                 error={fieldErrors.title} maxLength={200} />
      <FormField as="textarea" label="Description" name="description" rows={6}
                 value={form.description} onChange={handleChange} error={fieldErrors.description} />
      <label className="checkbox">
        <input type="checkbox" name="published" checked={form.published} onChange={handleChange} />
        <span>
          Published
          <span className="muted small-inline"> - visible in the catalog and open for enrollment</span>
        </span>
      </label>
      <button type="submit" className="button" disabled={submitting}>
        {submitting ? submittingLabel : submitLabel}
      </button>
    </form>
  );
}
