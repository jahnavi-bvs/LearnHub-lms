import { useContext } from 'react';
import { AuthContext } from './authContext';

/** { token, user, isAuthenticated, login, register, logout } from the nearest <AuthProvider>. */
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside <AuthProvider>');
  }
  return context;
}
