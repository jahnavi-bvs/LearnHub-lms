import { Link, useNavigate } from 'react-router-dom';
import { createCourse } from '../api/courses';
import { useAuth } from '../auth/useAuth';
import CourseForm from '../components/CourseForm';

export default function CourseCreatePage() {
  const { token } = useAuth();
  const navigate = useNavigate();

  async function handleCreate(values) {
    const created = await createCourse(values, token); // errors propagate to CourseForm
    navigate(`/courses/${created.id}`);
  }

  return (
    <section className="form-page form-page-wide">
      <p><Link to="/teaching">← My courses</Link></p>
      <h1>New course</h1>
      <CourseForm submitLabel="Create course" submittingLabel="Creating…" onSubmit={handleCreate} />
    </section>
  );
}
