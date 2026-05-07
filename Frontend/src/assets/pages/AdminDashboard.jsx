import { useAuth } from '../Context/AuthContext';
import EstadisticasPanel from '../Components/EstadisticasPanel';
import './Dashboard.css';

const AdminDashboard = () => {
  const { isAdmin } = useAuth();

  if (!isAdmin()) {
    return null;
  }

  return (
    <div className="dashboard">
      <main className="dashboard-main">
        <div className="dashboard-welcome">
          <h1>Panel de Administración</h1>
          <p>Gestiona usuarios, roles y permisos del sistema.</p>
        </div>

        <EstadisticasPanel />
      </main>
    </div>
  );
};

export default AdminDashboard;
