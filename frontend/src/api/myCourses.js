import { getCourse } from './courses';
import { myEnrollments } from './enrollments';

/**
 * The student's enrollments, each with its full course. An enrollment only stores the course
 * title, so details come from course-service. If a course has since been deleted, the saved
 * title is still shown.
 */
export async function loadMyCourses(token) {
  const enrollments = await myEnrollments(token);
  const courses = await Promise.all(enrollments.map((e) => getCourse(e.courseId, token).catch(() => null)));
  return enrollments.map((enrollment, i) => ({
    enrollment,
    course: courses[i] ?? { id: enrollment.courseId, title: enrollment.courseTitle },
  }));
}
