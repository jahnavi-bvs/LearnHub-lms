import { Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import CourseListPage from './pages/CourseListPage';
import NotFoundPage from './pages/NotFoundPage';

/** URL -> page. Routes nested inside <Layout> render in its <Outlet />. */
export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<CourseListPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
