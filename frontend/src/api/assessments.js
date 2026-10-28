import { apiRequest } from './client';

/** GET /api/assessments?courseId= - for the course's instructor, an admin, or an enrolled student. */
export function listAssessmentsForCourse(courseId, token) {
  return apiRequest(`/api/assessments?courseId=${encodeURIComponent(courseId)}`, { token });
}

/** GET /api/assessments/{id} - students get questions only; instructor/admin also get correctOptionIndex. */
export function getAssessment(id, token) {
  return apiRequest(`/api/assessments/${id}`, { token });
}

/** POST /api/assessments/{id}/submissions - one attempt per student; returns the graded result. */
export function submitAnswers(id, answers, token) {
  return apiRequest(`/api/assessments/${id}/submissions`, { method: 'POST', body: { answers }, token });
}

/**
 * GET /api/assessments/{id}/submissions/mine - the student's own result,
 * or null if they haven't submitted yet (the API answers 404 in that case).
 */
export async function getMySubmission(id, token) {
  try {
    return await apiRequest(`/api/assessments/${id}/submissions/mine`, { token });
  } catch (err) {
    if (err.status === 404) return null; // "not submitted yet" is an expected answer, not an error
    throw err;
  }
}

/** GET /api/assessments/submissions/mine - all of the student's results, newest first. */
export function mySubmissions(token) {
  return apiRequest('/api/assessments/submissions/mine', { token });
}
