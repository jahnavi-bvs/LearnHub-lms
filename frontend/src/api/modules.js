import { apiRequest } from './client';

const base = (courseId) => `/api/courses/${courseId}/modules`;

/** GET - the public outline, numbered from 1: titles only, no video or notes. */
export function listModules(courseId, token) {
  return apiRequest(base(courseId), { token });
}

/** GET - video and notes: enrolled students, the course's instructor, or an admin. */
export function getModule(courseId, moduleId, token) {
  return apiRequest(`${base(courseId)}/${moduleId}`, { token });
}

/** POST - body { title, description, youtubeUrl, notes }. New modules go last. */
export function createModule(courseId, module, token) {
  return apiRequest(base(courseId), { method: 'POST', body: module, token });
}

/** PUT - full replacement: leaving out youtubeUrl removes the video. */
export function updateModule(courseId, moduleId, module, token) {
  return apiRequest(`${base(courseId)}/${moduleId}`, { method: 'PUT', body: module, token });
}

/** DELETE - 204, returns null. The remaining modules are renumbered. */
export function deleteModule(courseId, moduleId, token) {
  return apiRequest(`${base(courseId)}/${moduleId}`, { method: 'DELETE', token });
}

/** PUT - moduleIds must list every module in the course, in the new order. */
export function reorderModules(courseId, moduleIds, token) {
  return apiRequest(`${base(courseId)}/order`, { method: 'PUT', body: { moduleIds }, token });
}
