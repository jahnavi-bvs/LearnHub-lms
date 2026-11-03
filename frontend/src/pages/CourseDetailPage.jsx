import { useState } from 'react';
import { Link, useLocation, useParams } from 'react-router-dom';
import { listAssessmentsForCourse } from '../api/assessments';
import { courseTags, getCourse } from '../api/courses';
import { courseRoster, dropEnrollment, enroll, myEnrollments } from '../api/enrollments';
import { useAuth } from '../auth/useAuth';
import { useAsync } from '../hooks/useAsync';

export default function CourseDetailPage() {
  const { id } = useParams(); // always a string, e.g. "6"
  const courseId = Number(id);
  const location = useLocation();
  const { user, token, isAuthenticated } = useAuth();
  const isStudent = user?.role === 'STUDENT';

  const course = useAsync(() => getCourse(courseId, token), [courseId, token]);
  // Only students have enrollments; everyone else gets an empty list without a request
  const enrollments = useAsync(
    () => (isStudent ? myEnrollments(token) : Promise.resolve([])),
    [isStudent, token],
  );

  // Derived, not stored: "am I enrolled?" is computed from the enrollments we loaded
  const enrollment = enrollments.data?.find((e) => e.courseId === courseId) ?? null;
  const isOwner = course.data != null && user != null && course.data.instructorId === user.id;
  const canManage = isOwner || user?.role === 'ADMIN';
  const canSeeQuizzes = enrollment != null || canManage;

  const assessments = useAsync(
    () => (canSeeQuizzes ? listAssessmentsForCourse(courseId, token) : Promise.resolve([])),
    [canSeeQuizzes, courseId, token],
  );
  // Who is enrolled: only the course's instructor (or an admin) may see this
  const roster = useAsync(
    () => (canManage ? courseRoster(courseId, token) : Promise.resolve([])),
    [canManage, courseId, token],
  );

  const [busy, setBusy] = useState(false);
  const [actionError, setActionError] = useState(null);

  async function handleEnroll() {
    setBusy(true);
    setActionError(null);
    try {
      await enroll(courseId, token);
      enrollments.reload(); // show what the server now says
    } catch (err) {
      setActionError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleDrop() {
    if (!window.confirm(`Drop "${course.data.title}"? You can enroll again later.`)) return;
    setBusy(true);
    setActionError(null);
    try {
      await dropEnrollment(enrollment.id, token);
      enrollments.reload();
    } catch (err) {
      setActionError(err.message);
    } finally {
      setBusy(false);
    }
  }

  if (course.loading) return <p className="muted">Loading course…</p>;
  if (course.error) {
    return (
      <section>
        <p className="alert alert-error" role="alert">
          {course.error.status === 404 ? 'This course does not exist or is not published.' : course.error.message}
        </p>
        <Link to="/">Back to courses</Link>
      </section>
    );
  }

  const c = course.data;

  return (
    <article className="detail">
      <p><Link to="/">← All courses</Link></p>
      <h1>
        {c.title} {!c.published && <span className="badge">Draft</span>}
      </h1>
      <p className="card-meta">by {c.instructorName}</p>
      {courseTags(c) && <p className="card-tags">{courseTags(c)}</p>}
      {c.description && <p className="detail-text">{c.description}</p>}

      <div className="panel">
        {actionError && <p className="alert alert-error" role="alert">{actionError}</p>}

        {!isAuthenticated && (
          <p>
            {/* state.from: after logging in, come back to this course */}
            <Link to="/login" state={{ from: location }}>Log in</Link> as a student to enroll.
          </p>
        )}

        {isStudent && enrollments.loading && <p className="muted">Checking your enrollment…</p>}
        {isStudent && enrollments.error && (
          <p className="alert alert-error" role="alert">{enrollments.error.message}</p>
        )}

        {isStudent && !enrollments.loading && !enrollments.error && (
          enrollment ? (
            <div className="panel-row">
              <p className="alert alert-success">
                You enrolled on {new Date(enrollment.enrolledAt).toLocaleDateString()}.
              </p>
              <button type="button" className="button button-secondary" onClick={handleDrop} disabled={busy}>
                {busy ? 'Dropping…' : 'Drop course'}
              </button>
            </div>
          ) : (
            <button type="button" className="button" onClick={handleEnroll} disabled={busy}>
              {busy ? 'Enrolling…' : 'Enroll in this course'}
            </button>
          )
        )}

        {canManage && (
          <div className="panel-row">
            <p className="muted">
              {isOwner ? 'You are the instructor of this course.' : 'Admin view.'}
            </p>
            <Link to={`/teaching/courses/${courseId}/edit`} className="button button-secondary">Edit course</Link>
          </div>
        )}
        {isAuthenticated && !isStudent && !canManage && (
          <p className="muted">Only students can enroll in courses.</p>
        )}
      </div>

      {canManage && (
        <section>
          <h2>Enrolled students</h2>
          {roster.loading && <p className="muted">Loading students…</p>}
          {roster.error && <p className="alert alert-error" role="alert">{roster.error.message}</p>}
          {roster.data && roster.data.length === 0 && <p className="muted">No students have enrolled yet.</p>}
          {roster.data && roster.data.length > 0 && (
            <ul className="list">
              {roster.data.map((e) => (
                <li key={e.id} className="list-item">
                  <span className="list-title">{e.studentName}</span>
                  <span className="muted">Enrolled {new Date(e.enrolledAt).toLocaleDateString()}</span>
                </li>
              ))}
            </ul>
          )}
        </section>
      )}

      {canSeeQuizzes && (
        <section>
          <div className="section-header">
            <h2>Quizzes</h2>
            {canManage && (
              <Link to={`/teaching/courses/${courseId}/quizzes/new`} className="button button-secondary">New quiz</Link>
            )}
          </div>
          {assessments.loading && <p className="muted">Loading quizzes…</p>}
          {assessments.error && <p className="alert alert-error" role="alert">{assessments.error.message}</p>}
          {assessments.data && assessments.data.length === 0 && (
            <p className="muted">This course has no quizzes yet.</p>
          )}
          {assessments.data && assessments.data.length > 0 && (
            <ul className="list">
              {assessments.data.map((a) => (
                <li key={a.id} className="list-item">
                  <Link to={`/assessments/${a.id}`} className="list-title">{a.title}</Link>
                  <span className="row-actions">
                    <span className="muted">{a.questionCount} questions</span>
                    {canManage && <Link to={`/teaching/assessments/${a.id}/submissions`}>Submissions</Link>}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </section>
      )}
    </article>
  );
}
