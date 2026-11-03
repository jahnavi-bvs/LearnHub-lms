import { useState } from 'react';
import { Link } from 'react-router-dom';
import { getCourse } from '../api/courses';
import { dropEnrollment, myEnrollments } from '../api/enrollments';
import { useAuth } from '../auth/useAuth';
import CourseCard from '../components/CourseCard';
import { useAsync } from '../hooks/useAsync';

/**
 * An enrollment only stores the course title, so each course's details come from course-service.
 * If a course has since been deleted, the card still shows with the saved title.
 */
async function loadMyCourses(token) {
  const enrollments = await myEnrollments(token);
  const courses = await Promise.all(enrollments.map((e) => getCourse(e.courseId, token).catch(() => null)));
  return enrollments.map((enrollment, i) => ({
    enrollment,
    course: courses[i] ?? { id: enrollment.courseId, title: enrollment.courseTitle },
  }));
}

export default function MyLearningPage() {
  const { token } = useAuth();
  const { data: rows, loading, error, reload } = useAsync(() => loadMyCourses(token), [token]);
  const [droppingId, setDroppingId] = useState(null); // which card's button is busy
  const [actionError, setActionError] = useState(null);

  async function handleDrop(enrollment) {
    if (!window.confirm(`Drop "${enrollment.courseTitle}"?`)) return;
    setDroppingId(enrollment.id);
    setActionError(null);
    try {
      await dropEnrollment(enrollment.id, token);
      reload();
    } catch (err) {
      setActionError(err.message);
    } finally {
      setDroppingId(null);
    }
  }

  if (loading) return <p className="muted">Loading your courses…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  return (
    <section>
      <h1>My courses</h1>
      {actionError && <p className="alert alert-error" role="alert">{actionError}</p>}
      {rows.length === 0 ? (
        <p className="muted">
          You are not enrolled in any courses yet. <Link to="/">Browse courses</Link>
        </p>
      ) : (
        <ul className="card-grid">
          {rows.map(({ enrollment, course }) => (
            <CourseCard key={enrollment.id} course={course} showMeta={false}>
              <button type="button" className="button button-secondary"
                      onClick={() => handleDrop(enrollment)} disabled={droppingId === enrollment.id}>
                {droppingId === enrollment.id ? 'Dropping…' : 'Drop'}
              </button>
            </CourseCard>
          ))}
        </ul>
      )}
    </section>
  );
}
