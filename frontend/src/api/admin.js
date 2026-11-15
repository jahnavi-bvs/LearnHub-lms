import { apiRequest } from './client';

/** GET /api/auth/users - every account, newest first. */
export function listUsers(token) {
  return apiRequest('/api/auth/users', { token });
}

/** PUT /api/auth/users/{id}/active - a deactivated user can't log in. Returns the updated user. */
export function setUserActive(id, active, token) {
  return apiRequest(`/api/auth/users/${id}/active`, { method: 'PUT', body: { active }, token });
}

/** PUT /api/auth/users/{id}/role - STUDENT or INSTRUCTOR. Returns the updated user. */
export function setUserRole(id, role, token) {
  return apiRequest(`/api/auth/users/${id}/role`, { method: 'PUT', body: { role }, token });
}

/** GET /api/courses/all - every instructor's courses, unpublished ones included. */
export function listAllCourses(token) {
  return apiRequest('/api/courses/all', { token });
}

/** PUT /api/courses/{id}/unpublish - takes a course out of the catalog. Returns the updated course. */
export function unpublishCourse(id, token) {
  return apiRequest(`/api/courses/${id}/unpublish`, { method: 'PUT', token });
}
