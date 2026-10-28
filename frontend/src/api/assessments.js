import { apiRequest } from './client';

/** GET /api/assessments?courseId= - for the course's instructor, an admin, or an enrolled student. */
export function listAssessmentsForCourse(courseId, token) {
  return apiRequest(`/api/assessments?courseId=${encodeURIComponent(courseId)}`, { token });
}
