import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import logoMark from '../assets/logo-mark.png';
import { useAuth } from '../auth/useAuth';
import Icon from './Icon';
import SearchBox from './SearchBox';
import ThemeToggle from './ThemeToggle';

// Sidebar menus from the sketches. "end" = only active on that exact URL.
const STUDENT_MENU = [
  { to: '/dashboard', label: 'Home', icon: 'home' },
  { to: '/', label: 'Browse courses', icon: 'book', end: true },
  { to: '/my-learning', label: 'My courses', icon: 'layers' },
  { to: '/my-quizzes', label: 'Quizzes', icon: 'quiz' },
  { to: '/my-results', label: 'My scores', icon: 'chart' },
];

const INSTRUCTOR_MENU = [
  { to: '/dashboard', label: 'Dashboard', icon: 'home' },
  { to: '/teaching', label: 'My courses', icon: 'layers', end: true },
  { to: '/teaching/new', label: 'Create course', icon: 'plus' },
  { to: '/teaching/students', label: 'Students', icon: 'users' },
  { to: '/teaching/quizzes', label: 'Quizzes', icon: 'quiz' },
  { to: '/', label: 'Browse courses', icon: 'book', end: true },
];

function Logo({ to }) {
  return (
    <Link to={to} className="brand">
      {/* alt="" because the name is written right beside it */}
      <img src={logoMark} alt="" className="brand-mark" />
      <span>Learn<span className="brand-hub">Hub</span></span>
    </Link>
  );
}

/** Visitors: a single top header (landing page sketch). */
function PublicLayout() {
  return (
    <>
      <header className="site-header">
        <div className="container header-inner">
          <Logo to="/" />
          <SearchBox />
          <nav className="nav" aria-label="Account">
            <ThemeToggle />
            <NavLink to="/login">Log in</NavLink>
            <Link to="/register" className="button">Register</Link>
          </nav>
        </div>
      </header>
      <main className="container page">
        <Outlet />
      </main>
    </>
  );
}

/** Logged-in users: sidebar + top bar (student and instructor sketches). */
function AppShell() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const menu = user.role === 'STUDENT' ? STUDENT_MENU : INSTRUCTOR_MENU;
  const firstName = user.fullName.split(' ')[0];

  function handleLogout() {
    logout();
    navigate('/');
  }

  return (
    <div className="shell">
      <aside className="sidebar">
        <Logo to="/dashboard" />
        <nav className="sidebar-nav" aria-label="Main">
          {menu.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end}>
              <Icon name={item.icon} />
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="shell-main">
        <header className="topbar">
          <p className="welcome">
            Welcome, <strong>{firstName}</strong>
          </p>
          <div className="topbar-actions">
            <SearchBox />
            <ThemeToggle />
            <NavLink to="/profile" className="icon-button" aria-label="My profile" title="My profile">
              <Icon name="user" />
            </NavLink>
            <button type="button" className="icon-button" onClick={handleLogout} aria-label="Log out" title="Log out">
              <Icon name="logout" />
            </button>
          </div>
        </header>
        <main className="shell-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

/** The frame around every page: which one depends on whether someone is logged in. */
export default function Layout() {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <AppShell /> : <PublicLayout />;
}
