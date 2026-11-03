import { Route, Routes } from 'react-router-dom';
import RequireAuth from './auth/RequireAuth';
import Layout from './components/Layout';
import AssessmentPage from './pages/AssessmentPage';
import CourseCreatePage from './pages/CourseCreatePage';
import CourseDetailPage from './pages/CourseDetailPage';
import CourseEditPage from './pages/CourseEditPage';
import CourseListPage from './pages/CourseListPage';
import DashboardPage from './pages/DashboardPage';
import LoginPage from './pages/LoginPage';
import MyLearningPage from './pages/MyLearningPage';
import MyQuizzesPage from './pages/MyQuizzesPage';
import MyResultsPage from './pages/MyResultsPage';
import NotFoundPage from './pages/NotFoundPage';
import ProfilePage from './pages/ProfilePage';
import QuizBuilderPage from './pages/QuizBuilderPage';
import QuizSubmissionsPage from './pages/QuizSubmissionsPage';
import RegisterPage from './pages/RegisterPage';
import TeachingPage from './pages/TeachingPage';
import TeachingQuizzesPage from './pages/TeachingQuizzesPage';
import TeachingStudentsPage from './pages/TeachingStudentsPage';

const STUDENTS = ['STUDENT'];
const TEACHERS = ['INSTRUCTOR', 'ADMIN'];

/** URL -> page. Routes nested inside <Layout> render in its <Outlet />. */
export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Public */}
        <Route path="/" element={<CourseListPage />} />
        <Route path="/courses/:id" element={<CourseDetailPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Any logged-in user */}
        <Route path="/dashboard" element={<RequireAuth><DashboardPage /></RequireAuth>} />
        <Route path="/profile" element={<RequireAuth><ProfilePage /></RequireAuth>} />
        <Route path="/assessments/:id" element={<RequireAuth><AssessmentPage /></RequireAuth>} />

        {/* Student area */}
        <Route path="/my-learning" element={<RequireAuth roles={STUDENTS}><MyLearningPage /></RequireAuth>} />
        <Route path="/my-quizzes" element={<RequireAuth roles={STUDENTS}><MyQuizzesPage /></RequireAuth>} />
        <Route path="/my-results" element={<RequireAuth roles={STUDENTS}><MyResultsPage /></RequireAuth>} />

        {/* Instructor area */}
        <Route path="/teaching" element={<RequireAuth roles={TEACHERS}><TeachingPage /></RequireAuth>} />
        <Route path="/teaching/new" element={<RequireAuth roles={TEACHERS}><CourseCreatePage /></RequireAuth>} />
        <Route path="/teaching/students" element={<RequireAuth roles={TEACHERS}><TeachingStudentsPage /></RequireAuth>} />
        <Route path="/teaching/quizzes" element={<RequireAuth roles={TEACHERS}><TeachingQuizzesPage /></RequireAuth>} />
        <Route path="/teaching/courses/:id/edit" element={<RequireAuth roles={TEACHERS}><CourseEditPage /></RequireAuth>} />
        <Route path="/teaching/courses/:id/quizzes/new" element={<RequireAuth roles={TEACHERS}><QuizBuilderPage /></RequireAuth>} />
        <Route path="/teaching/assessments/:id/submissions" element={<RequireAuth roles={TEACHERS}><QuizSubmissionsPage /></RequireAuth>} />

        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
