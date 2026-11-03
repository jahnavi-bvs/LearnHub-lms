import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { createModule, getModule, updateModule } from '../api/modules';
import { useAuth } from '../auth/useAuth';
import FormField from '../components/FormField';
import { useAsync } from '../hooks/useAsync';

function ModuleForm({ initial, onSubmit, submitLabel }) {
  const [form, setForm] = useState({
    title: initial?.title ?? '',
    description: initial?.description ?? '',
    youtubeUrl: initial?.youtubeVideoId ? `https://www.youtube.com/watch?v=${initial.youtubeVideoId}` : '',
    notes: initial?.notes ?? '',
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});

  function handleChange(event) {
    setForm({ ...form, [event.target.name]: event.target.value });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    setFieldErrors({});
    try {
      await onSubmit({
        title: form.title,
        description: form.description.trim() === '' ? null : form.description,
        youtubeUrl: form.youtubeUrl.trim() === '' ? null : form.youtubeUrl.trim(),
        notes: form.notes.trim() === '' ? null : form.notes,
      });
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
      <FormField as="textarea" label="Short description (optional)" name="description" rows={2}
                 value={form.description} onChange={handleChange} error={fieldErrors.description} />
      <FormField label="YouTube video link (leave empty if you don't have one yet)" name="youtubeUrl"
                 value={form.youtubeUrl} onChange={handleChange} error={fieldErrors.youtubeUrlValid}
                 placeholder="https://www.youtube.com/watch?v=..." />
      <FormField as="textarea" label="Lesson notes (Markdown: ### heading, - list, **bold**)" name="notes" rows={14}
                 value={form.notes} onChange={handleChange} error={fieldErrors.notes} />
      <button type="submit" className="button" disabled={submitting}>
        {submitting ? 'Saving…' : submitLabel}
      </button>
    </form>
  );
}

/** Add a module (no moduleId in the URL) or edit one. */
export default function ModuleFormPage() {
  const { id, moduleId } = useParams();
  const courseId = Number(id);
  const { token } = useAuth();
  const navigate = useNavigate();
  const listUrl = `/teaching/courses/${courseId}/modules`;

  const existing = useAsync(
    () => (moduleId ? getModule(courseId, moduleId, token) : Promise.resolve(null)),
    [courseId, moduleId, token],
  );

  async function handleSave(values) {
    if (moduleId) {
      await updateModule(courseId, moduleId, values, token);
    } else {
      await createModule(courseId, values, token);
    }
    navigate(listUrl);
  }

  if (existing.loading) return <p className="muted">Loading module…</p>;
  if (existing.error) return <p className="alert alert-error" role="alert">{existing.error.message}</p>;

  return (
    <section className="form-page form-page-wide">
      <p><Link to={listUrl}>← Modules</Link></p>
      <h1>{moduleId ? 'Edit module' : 'Add module'}</h1>
      {/* Rendered only after loading, so the form starts with the current values */}
      <ModuleForm initial={existing.data} onSubmit={handleSave}
                  submitLabel={moduleId ? 'Save changes' : 'Add module'} />
    </section>
  );
}
