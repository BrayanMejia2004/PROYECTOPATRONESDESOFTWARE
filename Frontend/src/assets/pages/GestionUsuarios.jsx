import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../Context/AuthContext';
import adminService from '../Services/adminService';
import './GestionUsuarios.css';

const ROLES_DISPONIBLES = ['ADMIN', 'USER', 'AUDITOR'];

const GestionUsuarios = () => {
  const navigate = useNavigate();
  const { user, perfil, token, logout, isAdmin } = useAuth();
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);
  const [userRoles, setUserRoles] = useState({});
  const [loadingRoles, setLoadingRoles] = useState({});
  const [showEditarModal, setShowEditarModal] = useState(false);
  const [usuarioEnEdicion, setUsuarioEnEdicion] = useState(null);
  const [editarUsername, setEditarUsername] = useState('');
  const [editarEmail, setEditarEmail] = useState('');
  const [editarPassword, setEditarPassword] = useState('');

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

  useEffect(() => {
    if (!isAdmin()) {
      navigate('/dashboard');
      return;
    }
    cargarUsuarios();
  }, []);

  const cargarUsuarios = async () => {
    setLoading(true);
    setError(null);
    const result = await adminService.obtenerUsuarios(token);
    if (result.success) {
      setUsuarios(result.data || []);
      result.data?.forEach((u) => {
        cargarRolesDeUsuario(u.username);
      });
    } else {
      setError(result.message || 'Error al cargar usuarios');
    }
    setLoading(false);
  };

  const cargarRolesDeUsuario = async (username) => {
    setLoadingRoles((prev) => ({ ...prev, [username]: true }));
    const result = await adminService.obtenerRolesDeUsuario(username, token);
    if (result.success) {
      setUserRoles((prev) => ({ ...prev, [username]: result.data || [] }));
    }
    setLoadingRoles((prev) => ({ ...prev, [username]: false }));
  };

  const asignarRol = async (username, rol) => {
    const result = await adminService.asignarRolAUsuario(username, rol, token);
    if (result.success) {
      setSuccessMessage(result.message);
      await cargarRolesDeUsuario(username);
      setTimeout(() => setSuccessMessage(null), 3000);
    } else {
      setError(result.message);
      setTimeout(() => setError(null), 3000);
    }
  };

  const quitarRol = async (username, rol) => {
    if (username === user?.username && rol === 'ADMIN') {
      setError('No puedes removerte el rol ADMIN a ti mismo');
      setTimeout(() => setError(null), 3000);
      return;
    }
    const result = await adminService.quitarRolAUsuario(username, rol, token);
    if (result.success) {
      setSuccessMessage(result.message);
      await cargarRolesDeUsuario(username);
      setTimeout(() => setSuccessMessage(null), 3000);
    } else {
      setError(result.message);
      setTimeout(() => setError(null), 3000);
    }
  };

  const getRolesDisponibles = (username) => {
    const rolesActuales = userRoles[username] || [];
    return ROLES_DISPONIBLES.filter((rol) => !rolesActuales.includes(rol));
  };

  const handleEditarUsuario = (usuario) => {
    if (usuario.username === user?.username) {
      setError('No puedes editarte a ti mismo');
      setTimeout(() => setError(null), 3000);
      return;
    }
    setUsuarioEnEdicion(usuario);
    setEditarUsername(usuario.username);
    setEditarEmail(usuario.email || '');
    setEditarPassword('');
    setShowEditarModal(true);
  };

  const handleGuardarEdicion = async (e) => {
    e.preventDefault();
    const result = await adminService.actualizarUsuario(
      usuarioEnEdicion.username,
      editarUsername,
      editarEmail,
      editarPassword || null,
      token
    );
    if (result.success) {
      setSuccessMessage(result.message);
      setShowEditarModal(false);
      await cargarUsuarios();
      setTimeout(() => setSuccessMessage(null), 3000);
    } else {
      setError(result.message);
      setTimeout(() => setError(null), 3000);
    }
  };

  const handleEliminarUsuario = async (username) => {
    if (username === user?.username) {
      setError('No puedes eliminarte a ti mismo');
      setTimeout(() => setError(null), 3000);
      return;
    }
    if (!window.confirm(`¿Estás seguro de eliminar el usuario ${username}?`)) {
      return;
    }
    const result = await adminService.eliminarUsuario(username, token);
    if (result.success) {
      setSuccessMessage(result.message);
      await cargarUsuarios();
      setTimeout(() => setSuccessMessage(null), 3000);
    } else {
      setError(result.message);
      setTimeout(() => setError(null), 3000);
    }
  };

  if (!isAdmin()) {
    return null;
  }

  return (
    <div className="gestion-container">
      <header className="gestion-header">
        <div className="gestion-header-content">
          <div className="gestion-logo">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M12 2L2 7l10 5 10-5-10-5z" />
              <path d="M2 17l10 5 10-5" />
              <path d="M2 12l10 5 10-5" />
            </svg>
            <span>Portal Gubernamental</span>
          </div>
          <div className="gestion-nav">
            <Link to="/admin" className="nav-link">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="15 18 9 12 15 6" />
              </svg>
              Panel Admin
            </Link>
            <Link to="/mi-perfil" className="nav-link">
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

      <main className="gestion-main">
        <div className="gestion-title">
          <h1>Gestión de Usuarios</h1>
          <p>Administra los usuarios del sistema y asigna roles</p>
        </div>

        {error && (
          <div className="alert alert-error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="15" y1="9" x2="9" y2="15" />
              <line x1="9" y1="9" x2="15" y2="15" />
            </svg>
            {error}
          </div>
        )}

        {successMessage && (
          <div className="alert alert-success">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
              <polyline points="22 4 12 14.01 9 11.01" />
            </svg>
            {successMessage}
          </div>
        )}

        {loading ? (
          <div className="loading-container">
            <div className="spinner"></div>
            <p>Cargando usuarios...</p>
          </div>
        ) : (
          <div className="usuarios-table-container">
            <table className="usuarios-table">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Email</th>
                  <th>Roles Actuales</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {usuarios.length === 0 ? (
                  <tr>
                    <td colSpan="4" className="empty-message">
                      No hay usuarios registrados
                    </td>
                  </tr>
                ) : (
                  usuarios.map((usuario) => (
                    <tr key={usuario.username}>
                      <td>
                        <div className="user-info">
                          <div className="user-avatar">
                            {usuario.username.charAt(0).toUpperCase()}
                          </div>
                          <span className="user-name">{usuario.username}</span>
                        </div>
                      </td>
                      <td>{usuario.email || '-'}</td>
                      <td>
                        <div className="roles-cell">
                          {loadingRoles[usuario.username] ? (
                            <span className="loading-roles">Cargando...</span>
                          ) : (
                            <>
                              {(userRoles[usuario.username] || []).map((rol) => (
                                <span
                                  key={rol}
                                  className={`role-badge role-${rol.toLowerCase()}`}
                                >
                                  {rol}
                                  <button
                                    onClick={() => quitarRol(usuario.username, rol)}
                                    className="role-remove"
                                    title={`Quitar rol ${rol}`}
                                  >
                                    ×
                                  </button>
                                </span>
                              ))}
                              {getRolesDisponibles(usuario.username).map((rol) => (
                                <button
                                  key={rol}
                                  onClick={() => asignarRol(usuario.username, rol)}
                                  className="role-add"
                                  title={`Asignar rol ${rol}`}
                                >
                                  + {rol}
                                </button>
                              ))}
                            </>
                          )}
                        </div>
                      </td>
                      <td>
                        <div className="actions-cell">
                          <button
                            onClick={() => handleEditarUsuario(usuario)}
                            className="action-btn edit"
                            title="Editar usuario"
                            disabled={usuario.username === user?.username}
                          >
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                              <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7" />
                              <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z" />
                            </svg>
                          </button>
                          <button
                            onClick={() => handleEliminarUsuario(usuario.username)}
                            className="action-btn delete"
                            title="Eliminar usuario"
                            disabled={usuario.username === user?.username}
                          >
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                              <polyline points="3 6 5 6 21 6" />
                              <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2" />
                            </svg>
                          </button>
                          <button
                            onClick={() => cargarRolesDeUsuario(usuario.username)}
                            className="action-btn refresh"
                            title="Actualizar roles"
                          >
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                              <polyline points="23 4 23 10 17 10" />
                              <path d="M20.49 15a9 9 0 11-2.12-9.36L23 10" />
                            </svg>
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </main>

      {showEditarModal && (
        <div className="modal-overlay">
          <div className="modal modal-editar">
            <div className="modal-header">
              <h2>Editar Usuario</h2>
              <button className="modal-close" onClick={() => setShowEditarModal(false)}>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <line x1="18" y1="6" x2="6" y2="18" />
                  <line x1="6" y1="6" x2="18" y2="18" />
                </svg>
              </button>
            </div>
            <form onSubmit={handleGuardarEdicion}>
              <div className="form-group">
                <label>Username</label>
                <input
                  type="text"
                  value={editarUsername}
                  onChange={(e) => setEditarUsername(e.target.value.toLowerCase())}
                  placeholder="Nombre de usuario"
                  required
                />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input
                  type="email"
                  value={editarEmail}
                  onChange={(e) => setEditarEmail(e.target.value)}
                  placeholder="Email del usuario"
                  required
                />
              </div>
              <div className="form-group">
                <label>Nueva Contraseña (dejar vacío para no cambiar)</label>
                <input
                  type="password"
                  value={editarPassword}
                  onChange={(e) => setEditarPassword(e.target.value)}
                  placeholder="Nueva contraseña"
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-cancelar" onClick={() => setShowEditarModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn-guardar">
                  Guardar Cambios
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default GestionUsuarios;
