import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../Context/AuthContext';
import './Dashboard.css';

const Dashboard = () => {
  const navigate = useNavigate();
  const { user, perfil, logout, isAdmin, isAuditor } = useAuth();

  const getDisplayName = () => {
    if (perfil?.nombre && perfil?.apellido) {
      return `${perfil.nombre} ${perfil.apellido}`;
    }
    return user?.username || 'Usuario';
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

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
            <span>Portal Gubernamental</span>
          </div>
          <div className="dashboard-user">
            <span className="user-name">Bienvenido, {getDisplayName()}</span>
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
          <h1>Bienvenido, {perfil?.nombre || user?.username}</h1>
          <p>Has iniciado sesión correctamente en el Portal Gubernamental.</p>
        </div>

        <div className="dashboard-cards">
          <Link to="/mi-perfil" className="dashboard-card-link">
            <div className="dashboard-card">
              <div className="card-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              </div>
              <h3>Mi Perfil</h3>
              <p>Gestiona tu información personal y datos de contacto</p>
            </div>
          </Link>

          {isAdmin() && (
            <Link to="/admin" className="dashboard-card-link">
              <div className="dashboard-card admin-card">
                <div className="card-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                    <path d="M12 8v4" />
                    <path d="M12 16h.01" />
                  </svg>
                </div>
                <h3>Administración</h3>
                <p>Gestiona usuarios, roles y permisos del sistema</p>
              </div>
            </Link>
          )}

          {isAuditor() && (
            <Link to="/auditoria" className="dashboard-card-link">
              <div className="dashboard-card auditor-card">
                <div className="card-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                  </svg>
                </div>
                <h3>Auditoría</h3>
                <p>Revisa el historial de acciones y genera reportes del sistema</p>
              </div>
            </Link>
          )}
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
