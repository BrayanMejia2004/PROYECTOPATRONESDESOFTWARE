import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../Context/AuthContext';
import './Dashboard.css';

const AdminDashboard = () => {
  const navigate = useNavigate();
  const { user, perfil, logout, isAdmin } = useAuth();

  const getDisplayName = () => {
    if (perfil?.nombre && perfil?.apellido) {
      return `${perfil.nombre} ${perfil.apellido}`;
    }
    return user?.username || 'Administrador';
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!isAdmin()) {
    navigate('/dashboard');
    return null;
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <div className="dashboard-header-content">
          <div className="dashboard-logo">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M12 2L2 7l10 5 10-5-10-5z" />
              <path d="M2 17l10 5 10-5" />
              <path d="M2 12l10 5 10-5" />
            </svg>
            <span>Portal Gubernamental - Administración</span>
          </div>
          <div className="dashboard-user">
            <span className="user-name">Admin, {getDisplayName()}</span>
            <Link to="/mi-perfil" className="profile-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              Mi Perfil
            </Link>
            <button onClick={handleLogout} className="logout-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4" />
                <polyline points="16 17 21 12 16 7" />
                <line x1="21" y1="12" x2="9" y2="12" />
              </svg>
              Cerrar Sesión
            </button>
          </div>
        </div>
      </header>

      <main className="dashboard-main">
        <div className="dashboard-welcome">
          <h1>Panel de Administración</h1>
          <p>Gestiona usuarios, roles y permisos del sistema.</p>
        </div>

        <div className="dashboard-cards">
          <Link to="/admin/usuarios" className="dashboard-card-link">
            <div className="dashboard-card">
              <div className="card-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                  <circle cx="9" cy="7" r="4" />
                  <path d="M23 21v-2a4 4 0 00-3-3.87" />
                  <path d="M16 3.13a4 4 0 010 7.75" />
                </svg>
              </div>
              <h3>Gestión de Usuarios</h3>
              <p>Administra cuentas de usuario, asigna y revoca roles</p>
            </div>
          </Link>

          <Link to="/admin/roles" className="dashboard-card-link">
            <div className="dashboard-card">
              <div className="card-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                  <path d="M9 12l2 2 4-4" />
                </svg>
              </div>
              <h3>Gestión de Roles</h3>
              <p>Configura permisos y atributos de cada rol</p>
            </div>
          </Link>

          <Link to="/dashboard" className="dashboard-card-link">
            <div className="dashboard-card">
              <div className="card-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="15 18 9 12 15 6" />
                </svg>
              </div>
              <h3>Volver al Dashboard</h3>
              <p>Regresa al panel principal de usuario</p>
            </div>
          </Link>
        </div>
      </main>
    </div>
  );
};

export default AdminDashboard;
