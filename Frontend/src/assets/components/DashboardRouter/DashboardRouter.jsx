import { useAuth } from '../../Context/AuthContext';
import AdminDashboard from '../../Pages/Dashboard/AdminDashboard';
import AuditorDashboard from '../AuditorDashboard/AuditorDashboard';

const DashboardRouter = () => {
  const { isAdmin, isAuditor } = useAuth();

  if (isAdmin()) return <AdminDashboard />;
  if (isAuditor()) return <AuditorDashboard />;
  return null;
};

export default DashboardRouter;
