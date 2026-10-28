import { Route, Routes } from 'react-router-dom';
import RequireAuth from './auth/RequireAuth';
import Layout from './components/Layout';
import CourseDetailPage from './pages/CourseDetailPage';
import CourseListPage from './pages/CourseListPage';
import LoginPage from './pages/LoginPage';
import MyLearningPage from './pages/MyLearningPage';
import NotFoundPage from './pages/NotFoundPage';
import ProfilePage from './pages/ProfilePage';
import RegisterPage from './pages/RegisterPage';

/** URL -> page. Routes nested inside <Layout> render in its <Outlet />. */
export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<CourseListPage />} />
        {/* :id is a URL parameter - read it with useParams() */}
        <Route path="/courses/:id" element={<CourseDetailPage />} />
        <Route path="/my-learning" element={<RequireAuth roles={['STUDENT']}><MyLearningPage /></RequireAuth>} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/profile" element={<RequireAuth><ProfilePage /></RequireAuth>} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
