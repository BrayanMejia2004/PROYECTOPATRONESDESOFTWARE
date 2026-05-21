import { useState, useEffect } from 'react';
import { useAuth } from '../../Context/AuthContext';
import adminService from '../../Services/dashboard/adminService';
import TimelinePanel from '../../Components/Timeline/TimelinePanel';
import './GestionUsuarios.css';

const GestionUsuarios = () => {
  const { user, token, isAdmin } = useAuth();
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
  const [usuarioSeleccionadoTimeline, setUsuarioSeleccionadoTimeline] = useState(null);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [rolesDisponibles, setRolesDisponibles] = useState([]);
  const itemsPorPagina = 5;

  useEffect(() => {
    cargarUsuarios();
  }, []);

  const cargarUsuarios = async (nuevaPagina) => {
    const pageToLoad = nuevaPagina !== undefined ? nuevaPagina : pagina;
    setLoading(true);
    setError(null);
    setUserRoles({});
    const [usuariosResult, rolesResult] = await Promise.all([
      adminService.obtenerUsuarios(token, pageToLoad, itemsPorPagina),
      adminService.obtenerRoles(token),
    ]);
    if (usuariosResult.success) {
      setUsuarios(usuariosResult.data || []);
      setTotalPaginas(usuariosResult.totalPages);
      setPagina(pageToLoad);
      usuariosResult.data?.forEach((u) => {
        cargarRolesDeUsuario(u.username);
      });
    } else {
      setError(usuariosResult.message || 'Error al cargar usuarios');
    }
    if (rolesResult.success) {
      const roles = rolesResult.data || [];
      setRolesDisponibles(roles.map(r => (typeof r === 'string' ? r : r.nombre)));
    } else {
      setRolesDisponibles(['ADMIN', 'USER', 'AUDITOR']);
    }
    setLoading(false);
  };

  const irPagina = (n) => {
    if (n >= 0 && n < totalPaginas) {
      cargarUsuarios(n);
    }
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
    return rolesDisponibles.filter((rol) => !rolesActuales.includes(rol));
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

  return (
    <div className="gestion-container">
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
          <>
            <table className="usuarios-table">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Email</th>
                  <th>Roles Actuales</th>
                  <th>Timeline</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {usuarios.length === 0 ? (
                  <tr>
                    <td colSpan="5" className="empty-message">
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
                        <button
                          onClick={() => setUsuarioSeleccionadoTimeline(usuario.id)}
                          className="action-btn timeline"
                          title="Ver timeline"
                        >
                          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <line x1="12" y1="5" x2="12" y2="19" />
                            <line x1="5" y1="12" x2="19" y2="12" />
                          </svg>
                        </button>
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
          {totalPaginas > 1 && (
            <div className="pagination">
              <button onClick={() => irPagina(0)} disabled={pagina === 0} title="Primera página">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="11 17 6 12 11 7" />
                  <polyline points="18 17 13 12 18 7" />
                </svg>
              </button>
              <button onClick={() => irPagina(pagina - 1)} disabled={pagina === 0} title="Anterior">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="15 18 9 12 15 6" />
                </svg>
              </button>
              <span className="pagina-info">Página {pagina + 1} de {totalPaginas}</span>
              <button onClick={() => irPagina(pagina + 1)} disabled={pagina >= totalPaginas - 1} title="Siguiente">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="9 18 15 12 9 6" />
                </svg>
              </button>
              <button onClick={() => irPagina(totalPaginas - 1)} disabled={pagina >= totalPaginas - 1} title="Última página">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="13 17 18 12 13 7" />
                  <polyline points="6 17 11 12 6 7" />
                </svg>
              </button>
            </div>
          )}
          </>
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

      {usuarioSeleccionadoTimeline && (
        <TimelinePanel
          usuarioId={usuarioSeleccionadoTimeline}
          onCerrar={() => setUsuarioSeleccionadoTimeline(null)}
        />
      )}
    </div>
  );
};

export default GestionUsuarios;
