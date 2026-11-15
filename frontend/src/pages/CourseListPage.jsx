import { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { listCourses } from '../api/courses';
import { enrollmentCounts } from '../api/enrollments';
import CourseCard from '../components/CourseCard';
import { useAsync } from '../hooks/useAsync';

const POPULAR_COUNT = 4;
const INITIAL_COURSES = 8;

/**
 * The published courses, newest first, and the most enrolled ones. Without the counts the
 * catalog still works: "Popular now" then just shows the newest courses.
 */
async function loadCatalog() {
  const [courses, counts] = await Promise.all([listCourses(), enrollmentCounts().catch(() => [])]);
  const enrollments = (course) => counts.find((c) => c.courseId === course.id)?.enrollments ?? 0;
  // toSorted keeps the catalog's order for ties, so equally popular courses show newest first
  const popular = courses.toSorted((a, b) => enrollments(b) - enrollments(a)).slice(0, POPULAR_COUNT);
  return { courses, popular };
}

/** Landing page for visitors, "Browse courses" for logged-in users. Search comes from ?q= in the URL. */
export default function CourseListPage() {
  const { data, loading, error } = useAsync(() => loadCatalog(), []);
  const [showAll, setShowAll] = useState(false);
  const [params] = useSearchParams();
  const query = (params.get('q') ?? '').trim();

  if (loading) return <p className="muted">Loading courses…</p>;
  if (error) return <p className="alert alert-error" role="alert">{error.message}</p>;

  const { courses, popular } = data;

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

  const shown = showAll ? courses : courses.slice(0, INITIAL_COURSES);

  return (
    <>
      <section>
        <h2 className="section-title">Popular now</h2>
        <ul className="popular-row">
          {popular.map((c) => <CourseCard key={c.id} course={c} />)}
        </ul>
      </section>
      <section>
        <h2 className="section-title">All courses</h2>
        <ul className="card-grid">{shown.map((c) => <CourseCard key={c.id} course={c} />)}</ul>
        {shown.length < courses.length && (
          <div className="view-more">
            <button type="button" className="button button-secondary" onClick={() => setShowAll(true)}>
              View more
            </button>
          </div>
        )}
      </section>
    </>
  );
}
