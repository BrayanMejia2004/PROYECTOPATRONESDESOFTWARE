import { useAuth } from '../Context/AuthContext';
import MapaCalorIP from '../Components/MapaCalorIP';
import './Dashboard.css';

const Dashboard = () => {
  const { isAdmin, isAuditor } = useAuth();

  return (
    <div className="dashboard">
      <main className="dashboard-main">
        {(isAdmin() || isAuditor()) && (
          <section className="dashboard-mapa-section">
            <div className="dashboard-mapa-header">
              <h1>Mapa de Calor por IP</h1>
              <p>Análisis de actividad agrupada por dirección IP de origen</p>
            </div>
            <MapaCalorIP />
          </section>
        )}
      </main>
    </div>
  );
};

export default Dashboard;
