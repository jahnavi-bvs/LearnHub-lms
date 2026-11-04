import { useState } from 'react';
import { listUsers, setUserActive, setUserRole } from '../api/admin';
import { useAuth } from '../auth/useAuth';
import { useAsync } from '../hooks/useAsync';

const ROLE_NAMES = { STUDENT: 'Student', INSTRUCTOR: 'Instructor', ADMIN: 'Admin' };

export function RoleBadge({ role }) {
  return <span className={role === 'ADMIN' ? 'badge' : 'badge badge-role'}>{ROLE_NAMES[role]}</span>;
}

/**
 * The students, the instructors, or every user: the three sidebar pages share this table.
 * @param role          show only this role, or nothing for everyone
 * @param canChangeRole show the role dropdown (the Users page)
 */
export default function ManageUsersPage({ title, role, canChangeRole = false }) {
  const { user: me, token } = useAuth();
  const users = useAsync(() => listUsers(token), [token]);
  const [changed, setChanged] = useState({}); // id -> the server's answer after a change
  const [busyId, setBusyId] = useState(null);
  const [error, setError] = useState(null);

  async function apply(id, request) {
    setBusyId(id);
    setError(null);
    try {
      const updated = await request();
      setChanged((previous) => ({ ...previous, [id]: updated }));
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyId(null);
    }
  }

  function toggleActive(user) {
    if (user.active && !window.confirm(`Deactivate ${user.fullName}? They won't be able to log in.`)) return;
    apply(user.id, () => setUserActive(user.id, !user.active, token));
  }

  function changeRole(user, newRole) {
    if (!window.confirm(`Change ${user.fullName} to ${ROLE_NAMES[newRole]}? This applies from their next login.`)) return;
    apply(user.id, () => setUserRole(user.id, newRole, token));
  }

  if (users.loading) return <p className="muted">Loading users…</p>;
  if (users.error) return <p className="alert alert-error" role="alert">{users.error.message}</p>;

  const rows = users.data.map((u) => changed[u.id] ?? u).filter((u) => !role || u.role === role);

  return (
    <section>
      <h1>{title}</h1>
      {error && <p className="alert alert-error" role="alert">{error}</p>}
      {rows.length === 0 ? (
        <p className="muted">Nobody here yet.</p>
      ) : (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th scope="col">Name</th>
                <th scope="col">Role</th>
                <th scope="col">Status</th>
                <th scope="col">Joined</th>
                <th scope="col">Actions</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((u) => {
                const locked = u.role === 'ADMIN'; // the API refuses changes to admin accounts
                return (
                  <tr key={u.id}>
                    <td>
                      <strong>{u.fullName}</strong>
                      <p className="muted small">{u.email}</p>
                    </td>
                    <td>
                      {canChangeRole && !locked ? (
                        <select className="table-select" value={u.role} disabled={busyId === u.id}
                                aria-label={`Role of ${u.fullName}`}
                                onChange={(e) => changeRole(u, e.target.value)}>
                          <option value="STUDENT">Student</option>
                          <option value="INSTRUCTOR">Instructor</option>
                        </select>
                      ) : <RoleBadge role={u.role} />}
                    </td>
                    <td>
                      <span className={u.active ? 'badge badge-published' : 'badge badge-inactive'}>
                        {u.active ? 'Active' : 'Deactivated'}
                      </span>
                    </td>
                    <td className="nowrap">{new Date(u.createdAt).toLocaleDateString()}</td>
                    <td>
                      {locked ? (
                        <span className="muted small-count">{u.id === me.id ? 'You' : 'Admin'}</span>
                      ) : (
                        <button type="button" disabled={busyId === u.id} onClick={() => toggleActive(u)}
                                className={u.active ? 'button button-danger' : 'button button-secondary'}>
                          {u.active ? 'Deactivate' : 'Activate'}
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
