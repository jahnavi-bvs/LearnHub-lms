import { listAllCourses, listUsers } from '../api/admin';
import { enrollmentCounts } from '../api/enrollments';
import { useAuth } from '../auth/useAuth';
import DonutChart from '../components/DonutChart';
import StatCard from '../components/StatCard';
import { useAsync } from '../hooks/useAsync';
import { RoleBadge } from './ManageUsersPage';

const RECENT_USERS = 5;
const TOP_COURSES = 5; // the rest fold into "Other", so the chart stays readable

// Colours follow the part's rank; defined in index.css so they can change with the theme
const SLICE_COLORS = ['var(--series-1)', 'var(--series-2)', 'var(--series-3)', 'var(--series-4)', 'var(--series-5)'];

/** The most enrolled courses plus "Other". Enrollments of since-deleted courses aren't counted. */
function chartParts(counts, courses) {
  const named = counts
    .map((c) => ({ key: c.courseId, label: courses.find((course) => course.id === c.courseId)?.title, value: c.enrollments }))
    .filter((p) => p.label);
  const parts = named.slice(0, TOP_COURSES).map((p, i) => ({ ...p, color: SLICE_COLORS[i] }));
  const others = named.slice(TOP_COURSES);
  if (others.length > 0) {
    parts.push({
      key: 'other',
      label: `Other (${others.length} courses)`,
      value: others.reduce((sum, p) => sum + p.value, 0),
      color: 'var(--series-other)',
    });
  }
  return parts;
}

export default function AdminDashboard() {
  const { user, token } = useAuth();
  const users = useAsync(() => listUsers(token), [token]);
  const courses = useAsync(() => listAllCourses(token), [token]);
  const counts = useAsync(() => enrollmentCounts(), []);

  const allUsers = users.data ?? [];
  const allCourses = courses.data ?? [];
  const count = (role) => (users.data ? allUsers.filter((u) => u.role === role).length : '–');
  const published = allCourses.filter((c) => c.published).length;
  const parts = counts.data && courses.data ? chartParts(counts.data, allCourses) : [];

  return (
    <>
      <div className="page-header">
        <div>
          <h1>Admin Dashboard</h1>
          <p className="muted small">Welcome back, {user.fullName}! Here's your platform overview.</p>
        </div>
      </div>

      <div className="stats">
        <StatCard icon="users" label="Students" value={count('STUDENT')} />
        <StatCard icon="user" label="Instructors" value={count('INSTRUCTOR')} />
        <StatCard icon="layers" label="Courses" value={courses.data ? allCourses.length : '–'}
                  hint={courses.data ? `${published} published · ${allCourses.length - published} unpublished` : undefined} />
      </div>

      <div className="admin-panels">
        <section className="admin-panel">
          <h2>Recent users</h2>
          {users.loading && <p className="muted">Loading users…</p>}
          {users.error && <p className="alert alert-error" role="alert">{users.error.message}</p>}
          {users.data && (
            <div className="table-scroll">
              <table className="table">
                <thead>
                  <tr>
                    <th scope="col">Name</th>
                    <th scope="col">Role</th>
                    <th scope="col">Joined</th>
                  </tr>
                </thead>
                <tbody>
                  {allUsers.slice(0, RECENT_USERS).map((u) => (
                    <tr key={u.id}>
                      <td>
                        <strong>{u.fullName}</strong>
                        <p className="muted small">{u.email}</p>
                      </td>
                      <td><RoleBadge role={u.role} /></td>
                      <td className="nowrap">{new Date(u.createdAt).toLocaleDateString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <section className="admin-panel">
          <h2>Enrollments by course</h2>
          {(counts.loading || courses.loading) && <p className="muted">Loading enrollments…</p>}
          {counts.error && <p className="alert alert-error" role="alert">{counts.error.message}</p>}
          {!counts.loading && !courses.loading && !counts.error && courses.data && (
            parts.length === 0
              ? <p className="muted">No students have enrolled in a course yet.</p>
              : <DonutChart parts={parts} totalLabel="enrollments" />
          )}
        </section>
      </div>
    </>
  );
}
