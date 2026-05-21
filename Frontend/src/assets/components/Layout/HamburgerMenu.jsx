import { useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../Context/AuthContext';

const HamburgerMenu = ({ isOpen, onClose }) => {
  const { user, perfil, logout, isAdmin, isAuditor } = useAuth();
  const navigate = useNavigate();

  const getDisplayName = () => {
    if (perfil?.nombre && perfil?.apellido) {
      return `${perfil.nombre} ${perfil.apellido}`;
    }
    return user?.username || 'Usuario';
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
    onClose();
  };

  const handleLinkClick = () => {
    onClose();
  };

  const handleOverlayClick = useCallback((e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  }, [onClose]);

  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  return (
    <>
      <div
        className={`hamburger-overlay ${isOpen ? 'open' : ''}`}
        onClick={handleOverlayClick}
      />
      <nav className={`hamburger-menu ${isOpen ? 'open' : ''}`}>
        <div className="menu-header">
          <div className="menu-logo">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M12 2L2 7l10 5 10-5-10-5z" />
              <path d="M2 17l10 5 10-5" />
              <path d="M2 12l10 5 10-5" />
            </svg>
            <span>Portal Gubernamental</span>
          </div>
          <button className="menu-close" onClick={onClose}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </div>

        <div className="menu-user-info">
          <div className="menu-avatar">
            {(perfil?.nombre?.[0] || user?.username?.[0] || 'U').toUpperCase()}
          </div>
          <div className="menu-user-details">
            <span className="menu-user-name">{getDisplayName()}</span>
            <span className="menu-user-role">
              {isAdmin() ? 'Administrador' : isAuditor() ? 'Auditor' : 'Usuario'}
            </span>
            <span className="menu-user-email">{perfil?.email || ''}</span>
          </div>
        </div>

        <ul className="menu-items">
          <li>
            <Link to="/dashboard" className="menu-item" onClick={handleLinkClick}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
                <polyline points="9 22 9 12 15 12 15 22" />
              </svg>
              <span>Dashboard</span>
            </Link>
          </li>

          <li>
            <Link to="/mi-perfil" className="menu-item" onClick={handleLinkClick}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              <span>Mi Perfil</span>
            </Link>
          </li>

          {!isAdmin() && !isAuditor() && (
            <li>
              <Link to="/mi-actividad" className="menu-item" onClick={handleLinkClick}>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                  <polyline points="9 12 11 14 15 10" />
                </svg>
                <span>Mi Actividad</span>
              </Link>
            </li>
          )}

          {isAdmin() && (
            <>
              <li className="menu-divider">
                <span>Administración</span>
              </li>
              <li>
                <Link to="/admin/usuarios" className="menu-item" onClick={handleLinkClick}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                    <circle cx="9" cy="7" r="4" />
                    <path d="M23 21v-2a4 4 0 00-3-3.87" />
                    <path d="M16 3.13a4 4 0 010 7.75" />
                  </svg>
                  <span>Gestión de Usuarios</span>
                </Link>
              </li>
              <li>
                <Link to="/admin/roles" className="menu-item" onClick={handleLinkClick}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                    <path d="M9 12l2 2 4-4" />
                  </svg>
                  <span>Gestión de Roles</span>
                </Link>
              </li>
              <li>
                <Link to="/admin/sesiones" className="menu-item" onClick={handleLinkClick}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="3" y="3" width="18" height="18" rx="2" />
                    <line x1="3" y1="9" x2="21" y2="9" />
                    <line x1="9" y1="21" x2="9" y2="9" />
                  </svg>
                  <span>Sesiones Activas</span>
                </Link>
              </li>
              <li>
                <Link to="/admin/amenazas" className="menu-item" onClick={handleLinkClick}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 2L2 7l10 5 10-5-10-5z" />
                    <path d="M2 17l10 5 10-5" />
                    <path d="M2 12l10 5 10-5" />
                  </svg>
                  <span>Centro de Amenazas</span>
                </Link>
              </li>
              <li>
                <Link to="/mapadecalor" className="menu-item" onClick={handleLinkClick}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M21 16V8a2 2 0 00-1-1.73l-7-4a2 2 0 00-2 0l-7 4A2 2 0 002 8v8a2 2 0 001 1.73l7 4a2 2 0 002 0l7-4A2 2 0 0021 16z" />
                    <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
                    <line x1="12" y1="22.08" x2="12" y2="12" />
                  </svg>
                  <span>Mapa de Calor</span>
                </Link>
              </li>
              <li>
                <Link to="/admin/simulador" className="menu-item" onClick={handleLinkClick}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="10" />
                    <polyline points="12 6 12 12 16 14" />
                  </svg>
                  <span>Simulador</span>
                </Link>
              </li>
            </>
          )}

          {isAuditor() && (
            <>
              <li className="menu-divider">
                <span>Análisis</span>
              </li>
              <li>
                <Link to="/auditoria/analizador" className="menu-item" onClick={handleLinkClick}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="10" />
                    <line x1="12" y1="16" x2="12" y2="12" />
                    <line x1="12" y1="8" x2="12.01" y2="8" />
                  </svg>
                  <span>Analizador Inteligente</span>
                </Link>
              </li>
              <li className="menu-divider">
                <span>Reportes</span>
              </li>
              <li>
                <Link to="/auditoria" className="menu-item" onClick={handleLinkClick}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" />
                    <polyline points="14 2 14 8 20 8" />
                    <line x1="16" y1="13" x2="8" y2="13" />
                    <line x1="16" y1="17" x2="8" y2="17" />
                  </svg>
                  <span>Auditoría</span>
                </Link>
              </li>
              <li>
                <Link to="/mapadecalor" className="menu-item" onClick={handleLinkClick}>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M21 16V8a2 2 0 00-1-1.73l-7-4a2 2 0 00-2 0l-7 4A2 2 0 002 8v8a2 2 0 001 1.73l7 4a2 2 0 002 0l7-4A2 2 0 0021 16z" />
                    <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
                    <line x1="12" y1="22.08" x2="12" y2="12" />
                  </svg>
                  <span>Mapa de Calor</span>
                </Link>
              </li>
            </>
          )}
        </ul>

        <div className="menu-footer">
          <button className="menu-logout" onClick={handleLogout}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
            <span>Cerrar Sesión</span>
          </button>
        </div>
      </nav>
    </>
  );
};

export default HamburgerMenu;
