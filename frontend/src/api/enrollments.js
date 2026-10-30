import { apiRequest } from './client';

/** POST /api/enrollments - STUDENT only. */
export function enroll(courseId, token) {
  return apiRequest('/api/enrollments', { method: 'POST', body: { courseId }, token });
}

/** GET /api/enrollments/mine - the student's enrollments, newest first. */
export function myEnrollments(token) {
  return apiRequest('/api/enrollments/mine', { token });
}

/** GET /api/enrollments/course/{courseId} - the roster; the course's instructor or an admin only. */
export function courseRoster(courseId, token) {
  return apiRequest(`/api/enrollments/course/${courseId}`, { token });
}

/** DELETE /api/enrollments/{id} - drop a course (204, returns null). */
export function dropEnrollment(enrollmentId, token) {
  return apiRequest(`/api/enrollments/${enrollmentId}`, { method: 'DELETE', token });
}
