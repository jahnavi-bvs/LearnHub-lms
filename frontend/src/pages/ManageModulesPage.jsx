import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getCourse } from '../api/courses';
import { deleteModule, listModules, reorderModules } from '../api/modules';
import { useAuth } from '../auth/useAuth';
import { useAsync } from '../hooks/useAsync';

/** The instructor's list of a course's modules: add, edit, delete, move up and down. */
export default function ManageModulesPage() {
  const { id } = useParams();
  const courseId = Number(id);
  const { token } = useAuth();
  const { data, loading, error, reload } = useAsync(
    () => Promise.all([getCourse(courseId, token), listModules(courseId, token)]),
    [courseId, token],
  );
  const [busy, setBusy] = useState(false);
  const [actionError, setActionError] = useState(null);

  async function run(action) {
    setBusy(true);
    setActionError(null);
    try {
      await action();
      reload();
    } catch (err) {
      setActionError(err.message);
    } finally {
      setBusy(false);
    }
  }

  if (loading && !data) return <p className="muted">Loading modules…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  const [course, modules] = data;

  function move(index, offset) {
    const ids = modules.map((m) => m.id);
    [ids[index], ids[index + offset]] = [ids[index + offset], ids[index]]; // swap with a neighbour
    run(() => reorderModules(courseId, ids, token));
  }

  function remove(module) {
    if (!window.confirm(`Delete module "${module.title}"? This cannot be undone.`)) return;
    run(() => deleteModule(courseId, module.id, token));
  }

  return (
    <section>
      <p><Link to={`/courses/${courseId}`}>← {course.title}</Link></p>
      <div className="page-header">
        <h1>Modules</h1>
        <Link to={`/teaching/courses/${courseId}/modules/new`} className="button">Add module</Link>
      </div>
      {actionError && <p className="alert alert-error" role="alert">{actionError}</p>}

      {modules.length === 0 ? (
        <p className="muted">This course doesn't have any modules yet.</p>
      ) : (
        <ol className="list">
          {modules.map((m, index) => (
            <li key={m.id} className="list-item">
              <div>
                <span className="list-title">{m.position}. {m.title}</span>
                {!m.hasVideo && <> <span className="badge">Needs video</span></>}
              </div>
              <div className="row-actions">
                <button type="button" className="button button-secondary" disabled={busy || index === 0}
                        onClick={() => move(index, -1)} aria-label={`Move ${m.title} up`}>↑</button>
                <button type="button" className="button button-secondary" disabled={busy || index === modules.length - 1}
                        onClick={() => move(index, 1)} aria-label={`Move ${m.title} down`}>↓</button>
                <Link to={`/teaching/courses/${courseId}/modules/${m.id}/edit`} className="button button-secondary">Edit</Link>
                <button type="button" className="button button-danger" disabled={busy} onClick={() => remove(m)}>Delete</button>
              </div>
            </li>
          ))}
        </ol>
      )}
    </section>
  );
}
