import { Link } from 'react-router-dom';
import { courseTags } from '../api/courses';
import CourseCover from './CourseCover';

/**
 * Box card: picture, title, description, "View course".
 * @param showMeta show the instructor and category line (catalog yes, My courses no)
 * @param badge    optional label over the picture, e.g. Draft
 * @param children optional page-specific buttons beside "View course"
 */
export default function CourseCard({ course, showMeta = true, badge, children }) {
  const url = `/courses/${course.id}`;
  return (
    <li className="card course-card">
      <CourseCover course={course} />
      {badge && <span className="course-badge">{badge}</span>}
      <div className="course-body">
        <h3 className="card-title"><Link to={url}>{course.title}</Link></h3>
        {showMeta && <p className="card-meta">by {course.instructorName}</p>}
        {showMeta && courseTags(course) && <p className="card-tags">{courseTags(course)}</p>}
        {course.description && <p className="card-text">{course.description}</p>}
        <div className="course-footer">
          <Link to={url} className="view-link">View course</Link>
          {children && <div className="row-actions">{children}</div>}
        </div>
      </div>
    </li>
  );
}
