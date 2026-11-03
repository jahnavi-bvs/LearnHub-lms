import { Link } from 'react-router-dom';
import { courseTags } from '../api/courses';
import CourseCover from './CourseCover';
import ProgressBar from './ProgressBar';

/**
 * Box card: picture, title, description, then a link.
 * @param showMeta show the instructor and category line (catalog yes, My courses no)
 * @param badge    optional label over the picture, e.g. Draft
 * @param progress optional ProgressResponse: shows a progress bar
 * @param link     optional { to, label } replacing the default "View course" link
 * @param children optional page-specific buttons beside the link
 */
export default function CourseCard({ course, showMeta = true, badge, progress, link, children }) {
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
          {progress && <ProgressBar progress={progress} />}
          <Link to={link?.to ?? url} className="view-link">{link?.label ?? 'View course'}</Link>
          {children && <div className="row-actions">{children}</div>}
        </div>
      </div>
    </li>
  );
}
