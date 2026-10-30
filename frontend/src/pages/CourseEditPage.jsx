import { Link, useNavigate, useParams } from 'react-router-dom';
import { getCourse, updateCourse } from '../api/courses';
import { useAuth } from '../auth/useAuth';
import CourseForm from '../components/CourseForm';
import { useAsync } from '../hooks/useAsync';

export default function CourseEditPage() {
  const { id } = useParams();
  const courseId = Number(id);
  const { user, token } = useAuth();
  const navigate = useNavigate();
  const { data: course, loading, error } = useAsync(() => getCourse(courseId, token), [courseId, token]);

  async function handleSave(values) {
    await updateCourse(courseId, values, token);
    navigate(`/courses/${courseId}`);
  }

  if (loading) return <p className="muted">Loading course…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;
  // The backend would refuse the save anyway (403); this just avoids showing a useless form
  if (course.instructorId !== user.id && user.role !== 'ADMIN') {
    return <p className="alert alert-error" role="alert">You can only edit your own courses.</p>;
  }

  return (
    <section className="form-page form-page-wide">
      <p><Link to={`/courses/${courseId}`}>← Back to course</Link></p>
      <h1>Edit course</h1>
      {/* Rendered only after loading, so the form starts with the course's current values */}
      <CourseForm key={course.id} initialValues={course}
                  submitLabel="Save changes" submittingLabel="Saving…" onSubmit={handleSave} />
    </section>
  );
}
