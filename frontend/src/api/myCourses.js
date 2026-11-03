import { getCourse } from './courses';
import { myEnrollments, myProgress } from './enrollments';

/**
 * The student's enrollments, each with its full course and progress. An enrollment only stores
 * the course title, so details come from course-service. If a course has since been deleted, the
 * saved title is still shown. If progress can't be loaded, cards simply appear without a bar.
 */
export async function loadMyCourses(token) {
  const [enrollments, progressList] = await Promise.all([myEnrollments(token), myProgress(token).catch(() => [])]);
  const courses = await Promise.all(enrollments.map((e) => getCourse(e.courseId, token).catch(() => null)));
  return enrollments.map((enrollment, i) => ({
    enrollment,
    course: courses[i] ?? { id: enrollment.courseId, title: enrollment.courseTitle },
    progress: progressList.find((p) => p.enrollmentId === enrollment.id) ?? null,
  }));
}

/** Where the student should go next in a course, and what to call that link. */
export function nextStep(courseId, progress) {
  if (!progress) return { to: `/courses/${courseId}`, label: 'View course' };
  if (!progress.nextModuleId) return { to: `/courses/${courseId}/learn`, label: 'Review course' };
  return {
    to: `/courses/${courseId}/learn/${progress.nextModuleId}`,
    label: progress.completedCount === 0 ? 'Start learning' : 'Continue learning',
  };
}
