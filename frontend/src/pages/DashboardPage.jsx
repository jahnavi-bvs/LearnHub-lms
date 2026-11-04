import { useAuth } from '../auth/useAuth';
import AdminDashboard from './AdminDashboard';
import InstructorDashboard from './InstructorDashboard';
import StudentDashboard from './StudentDashboard';

const DASHBOARDS = { STUDENT: StudentDashboard, INSTRUCTOR: InstructorDashboard, ADMIN: AdminDashboard };

/** /dashboard - the right home page for the logged-in user's role. */
export default function DashboardPage() {
  const { user } = useAuth();
  const Dashboard = DASHBOARDS[user.role];
  return <Dashboard />;
}
