import { Link, useSearchParams } from 'react-router-dom';
import { listCourses } from '../api/courses';
import CourseCard from '../components/CourseCard';
import { useAsync } from '../hooks/useAsync';

const POPULAR_COUNT = 4;

/** Landing page for visitors, "Browse courses" for logged-in users. Search comes from ?q= in the URL. */
export default function CourseListPage() {
  const { data: courses, loading, error } = useAsync(() => listCourses(), []);
  const [params] = useSearchParams();
  const query = (params.get('q') ?? '').trim();

  if (loading) return <p className="muted">Loading courses…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  // Filtering in the browser is fine here: the catalog is small and not paginated
  const needle = query.toLowerCase();
  const results = needle
    ? courses.filter((c) =>
        [c.title, c.description, c.instructorName].some((field) => field?.toLowerCase().includes(needle)))
    : courses;

  if (query) {
    return (
      <section>
        <h1>Results for “{query}”</h1>
        <p className="muted">
          {results.length} course{results.length === 1 ? '' : 's'} found · <Link to="/">Clear search</Link>
        </p>
        {results.length > 0 && <ul className="card-grid">{results.map((c) => <CourseCard key={c.id} course={c} />)}</ul>}
      </section>
    );
  }

  if (courses.length === 0) {
    return (
      <section>
        <h1>Courses</h1>
        <p className="muted">No courses have been published yet.</p>
      </section>
    );
  }

  return (
    <>
      <section>
        {/* For now "popular" = newest; Step 22 switches this to most-enrolled */}
        <h2 className="section-title">Popular now</h2>
        <ul className="popular-row">
          {courses.slice(0, POPULAR_COUNT).map((c) => <CourseCard key={c.id} course={c} />)}
        </ul>
      </section>
      <section>
        <h2 className="section-title">All courses</h2>
        <ul className="card-grid">{courses.map((c) => <CourseCard key={c.id} course={c} />)}</ul>
      </section>
    </>
  );
}
