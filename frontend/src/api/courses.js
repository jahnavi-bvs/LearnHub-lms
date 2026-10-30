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

/** GET /api/courses/mine - the instructor's own courses, drafts included. */
export function myCourses(token) {
  return apiRequest('/api/courses/mine', { token });
}

/** POST /api/courses - body { title, description, published }. Returns the new course. */
export function createCourse(course, token) {
  return apiRequest('/api/courses', { method: 'POST', body: course, token });
}

/**
 * PUT /api/courses/{id} - FULL replacement: always send title, description AND published,
 * or the course silently becomes a draft (see docs/API.md).
 */
export function updateCourse(id, course, token) {
  return apiRequest(`/api/courses/${id}`, { method: 'PUT', body: course, token });
}

/** DELETE /api/courses/{id} - 204, returns null. */
export function deleteCourse(id, token) {
  return apiRequest(`/api/courses/${id}`, { method: 'DELETE', token });
}
