import { useAuth } from '../auth/useAuth';
import InstructorDashboard from './InstructorDashboard';
import StudentDashboard from './StudentDashboard';

/** /dashboard - the right home page for the logged-in user's role. */
export default function DashboardPage() {
  const { user } = useAuth();
  return user.role === 'STUDENT' ? <StudentDashboard /> : <InstructorDashboard />;
}
