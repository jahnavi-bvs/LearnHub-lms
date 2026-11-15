import { apiRequest } from './client';

/** GET /api/enrollments/stats/by-course - public: [{ courseId, enrollments }], most enrolled first. */
export function enrollmentCounts() {
  return apiRequest('/api/enrollments/stats/by-course');
}

/** POST /api/enrollments - STUDENT only. */
export function enroll(courseId, token) {
  return apiRequest('/api/enrollments', { method: 'POST', body: { courseId }, token });
}

/** GET /api/enrollments/mine - the student's enrollments, newest first. */
export function myEnrollments(token) {
  return apiRequest('/api/enrollments/mine', { token });
}

/** GET /api/enrollments/mine/progress - { enrollmentId, courseId, completedModuleIds, percentage, nextModuleId... } */
export function myProgress(token) {
  return apiRequest('/api/enrollments/mine/progress', { token });
}

/** PUT - safe to repeat; returns the updated progress. */
export function markModuleComplete(enrollmentId, moduleId, token) {
  return apiRequest(`/api/enrollments/${enrollmentId}/completed-modules/${moduleId}`, { method: 'PUT', token });
}

/** DELETE - returns the updated progress. */
export function markModuleIncomplete(enrollmentId, moduleId, token) {
  return apiRequest(`/api/enrollments/${enrollmentId}/completed-modules/${moduleId}`, { method: 'DELETE', token });
}

/** GET /api/enrollments/course/{courseId} - the roster; the course's instructor or an admin only. */
export function courseRoster(courseId, token) {
  return apiRequest(`/api/enrollments/course/${courseId}`, { token });
}

/** DELETE /api/enrollments/{id} - drop a course (204, returns null). */
export function dropEnrollment(enrollmentId, token) {
  return apiRequest(`/api/enrollments/${enrollmentId}`, { method: 'DELETE', token });
}
