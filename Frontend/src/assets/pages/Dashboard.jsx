import { useAuth } from '../Context/AuthContext';
import './Dashboard.css';

const Dashboard = () => {
  const { perfil, user } = useAuth();

  const getShortDisplayName = () => {
    if (perfil?.nombre && perfil?.apellido) {
      const primerNombre = perfil.nombre.split(' ')[0];
      const primerApellido = perfil.apellido.split(' ')[0];
      return `${primerNombre} ${primerApellido}`;
    }
    return user?.username || 'Usuario';
  };

  return (
    <div className="dashboard">
      <main className="dashboard-main">
        <div className="dashboard-welcome">
          <h1>Bienvenido, {getShortDisplayName()}</h1>
          <p>Has iniciado sesión correctamente en el Portal Gubernamental.</p>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
