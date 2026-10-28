import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

/** The frame shared by every page. <Outlet /> is where the current route's page appears. */
export default function Layout() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/');
  }

  return (
    <>
      <header className="site-header">
        <div className="container header-inner">
          <NavLink to="/" className="brand">LearnHub</NavLink>
          <nav className="nav">
            {/* NavLink adds class "active" when its URL is the current one */}
            <NavLink to="/" end>Courses</NavLink>
            {isAuthenticated ? (
              <>
                {user.role === 'STUDENT' && <NavLink to="/my-learning">My learning</NavLink>}
                <NavLink to="/profile">Profile</NavLink>
                <span className="user-badge">{user.fullName} · {user.role}</span>
                <button type="button" className="button button-secondary" onClick={handleLogout}>
                  Log out
                </button>
              </>
            ) : (
              <>
                <NavLink to="/login">Log in</NavLink>
                <NavLink to="/register">Register</NavLink>
              </>
            )}
          </nav>
        </div>
      </header>
      <main className="container page">
        <Outlet />
      </main>
    </>
  );
}
