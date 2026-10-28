import { apiRequest } from './client';

/** POST /api/auth/login -> { accessToken, tokenType, expiresIn, user } */
export function login(email, password) {
  return apiRequest('/api/auth/login', { method: 'POST', body: { email, password } });
}

/** POST /api/auth/register -> user (no token: log in afterwards) */
export function register({ fullName, email, password, role }) {
  return apiRequest('/api/auth/register', { method: 'POST', body: { fullName, email, password, role } });
}

/** GET /api/auth/me -> the token owner's user object */
export function getMe(token) {
  return apiRequest('/api/auth/me', { token });
}
