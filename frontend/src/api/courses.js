import { apiRequest } from './client';

/** GET /api/courses - public catalog of published courses, newest first. */
export function listCourses() {
  return apiRequest('/api/courses');
}

/**
 * GET /api/courses/{id}. Public for published courses; the token (if any) lets the
 * course's own instructor see it while it's still a draft.
 */
export function getCourse(id, token) {
  return apiRequest(`/api/courses/${id}`, { token });
}
