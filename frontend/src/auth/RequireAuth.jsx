import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './useAuth';

/**
 * Wrap a page to require login (and optionally a role):
 *   <RequireAuth roles={['INSTRUCTOR', 'ADMIN']}><MyPage /></RequireAuth>
 * This only hides pages - the real protection is the backend rejecting the requests.
 */
export default function RequireAuth({ roles, children }) {
  const { isAuthenticated, user } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    // Remember where the user was going, so LoginPage can send them back
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  if (roles && !roles.includes(user.role)) {
    return <p className="alert alert-error" role="alert">You don't have access to this page.</p>;
  }
  return children;
}
