import { Link } from 'react-router-dom';
import { courseTags } from '../api/courses';

/**
 * A course tile. The coloured cover is decorative: its hue is computed from the course id,
 * so each course keeps the same colour (there are no course images in the backend).
 */
export default function CourseCard({ course }) {
  const hue = (course.id * 47) % 360;
  return (
    <li className="card course-card">
      <div className="course-cover" style={{ '--hue': hue }} aria-hidden="true">
        {course.title.charAt(0).toUpperCase()}
      </div>
      <div className="course-body">
        <h3 className="card-title">
          {/* The ::after on this link makes the whole card clickable */}
          <Link to={`/courses/${course.id}`} className="stretched-link">{course.title}</Link>
        </h3>
        <p className="card-meta">by {course.instructorName}</p>
        {courseTags(course) && <p className="card-tags">{courseTags(course)}</p>}
        {course.description && <p className="card-text">{course.description}</p>}
      </div>
    </li>
  );
}
