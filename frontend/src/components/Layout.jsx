import { NavLink, Outlet } from 'react-router-dom';

/** The frame shared by every page. <Outlet /> is where the current route's page appears. */
export default function Layout() {
  return (
    <>
      <header className="site-header">
        <div className="container header-inner">
          <NavLink to="/" className="brand">LearnHub</NavLink>
          <nav className="nav">
            {/* NavLink adds class "active" when its URL is the current one */}
            <NavLink to="/" end>Courses</NavLink>
          </nav>
        </div>
      </header>
      <main className="container page">
        <Outlet />
      </main>
    </>
  );
}
