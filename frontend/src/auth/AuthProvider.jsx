import { useCallback, useEffect, useMemo, useState } from 'react';
import * as authApi from '../api/auth';
import { setUnauthorizedHandler } from '../api/client';
import { AuthContext } from './authContext';
import { tokenExpiresAt } from './jwt';

// Shared "who is logged in?" state. Any component can read it with useAuth().

const STORAGE_KEY = 'learnhub.auth';

/** { token, user } from localStorage, or null if absent, corrupt or expired. */
function readStoredAuth() {
  try {
    const stored = JSON.parse(localStorage.getItem(STORAGE_KEY));
    if (stored?.token && tokenExpiresAt(stored.token) > Date.now()) {
      return stored;
    }
  } catch {
    // corrupt JSON - fall through and clear it
  }
  localStorage.removeItem(STORAGE_KEY);
  return null;
}

export function AuthProvider({ children }) {
  // Passing a function (not its result) means it runs only on the first render
  const [auth, setAuth] = useState(readStoredAuth);

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    setAuth(null);
  }, []);

  const login = useCallback(async (email, password) => {
    const response = await authApi.login(email, password);
    const next = { token: response.accessToken, user: response.user };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    setAuth(next);
    return response.user;
  }, []);

  /** Register, then log straight in (registration doesn't return a token). */
  const register = useCallback(async (form) => {
    await authApi.register(form);
    return login(form.email, form.password);
  }, [login]);

  // Any 401 on a request that carried our token ends the session
  useEffect(() => {
    setUnauthorizedHandler(logout);
    return () => setUnauthorizedHandler(null);
  }, [logout]);

  // Log out automatically the moment the token expires
  useEffect(() => {
    if (!auth) return undefined;
    const timer = setTimeout(logout, tokenExpiresAt(auth.token) - Date.now());
    return () => clearTimeout(timer);
  }, [auth, logout]);

  // useMemo: consumers only re-render when auth actually changes
  const value = useMemo(() => ({
    token: auth?.token ?? null,
    user: auth?.user ?? null,
    isAuthenticated: auth !== null,
    login,
    register,
    logout,
  }), [auth, login, register, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
