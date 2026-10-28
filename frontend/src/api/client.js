// The one place that talks HTTP. Every API call goes through apiRequest(), so the base URL,
// JSON handling, the auth token and error parsing are written once.

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Thrown for every failed request. Carries the backend's problem-detail fields
 * (see docs/API.md, "Error format") so pages can show the right message.
 */
export class ApiError extends Error {
  constructor(status, problem) {
    super(problem?.detail || problem?.title || `Request failed with status ${status}`);
    this.name = 'ApiError';
    this.status = status; // 0 means the server couldn't be reached at all
    this.title = problem?.title ?? null;
    this.errors = problem?.errors ?? {}; // field -> message, for "Validation failed"
  }
}

/**
 * @param {string} path    e.g. "/api/courses"
 * @param {object} options method, body (a JS object, sent as JSON), token (JWT)
 * @returns the parsed JSON response, or null for 204 No Content
 */
export async function apiRequest(path, { method = 'GET', body, token } = {}) {
  const headers = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (token) headers.Authorization = `Bearer ${token}`;

  let response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch {
    // fetch only throws when there is no HTTP response at all: server down, or blocked by CORS
    throw new ApiError(0, {
      title: 'Network error',
      detail: 'Cannot reach the server. Is the API gateway running on port 8080?',
    });
  }

  if (response.status === 204) return null;

  // Success bodies are application/json; errors are application/problem+json
  const isJson = response.headers.get('Content-Type')?.includes('json');
  const data = isJson ? await response.json() : null;

  // fetch does NOT throw for 4xx/5xx - we have to check response.ok ourselves
  if (!response.ok) throw new ApiError(response.status, data);
  return data;
}
