import { apiRequest } from './client';

/** GET /api/courses - public catalog of published courses, newest first. */
export function listCourses() {
  return apiRequest('/api/courses');
}
