import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../Context/AuthContext';
import adminService from '../Services/adminService';
import './GestionRoles.css';

const ROLES_FIJOS = ['ADMIN', 'USER', 'AUDITOR'];

const GestionRoles = () => {
  const navigate = useNavigate();
  const { user, perfil, token, logout, isAdmin } = useAuth();
  const [roles, setRoles] = useState([]);
  const [rolesData, setRolesData] = useState({});
  const [loading, setLoading] = useState(true);
  const [loadingPermisos, setLoadingPermisos] = useState({});
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [selectedRol, setSelectedRol] = useState(null);
  const [permisosDisponibles, setPermisosDisponibles] = useState([]);
  const [loadingGlobal, setLoadingGlobal] = useState(false);
  const [showCrearModal, setShowCrearModal] = useState(false);
  const [nuevoRolNombre, setNuevoRolNombre] = useState('');
  const [nuevoRolDescripcion, setNuevoRolDescripcion] = useState('');
  const [permisosSeleccionados, setPermisosSeleccionados] = useState([]);
  const [permisosSeleccionadosCrear, setPermisosSeleccionadosCrear] = useState([]);
  const [showEditarModal, setShowEditarModal] = useState(false);
  const [rolEnEdicion, setRolEnEdicion] = useState(null);
  const [editarNombre, setEditarNombre] = useState('');
  const [editarDescripcion, setEditarDescripcion] = useState('');
  const [editarPermisos, setEditarPermisos] = useState([]);

  const togglePermisoCrear = (nombrePermiso) => {
    setPermisosSeleccionadosCrear((prev) =>
      prev.includes(nombrePermiso)
        ? prev.filter((p) => p !== nombrePermiso)
        : [...prev, nombrePermiso]
    );
  };

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
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    setLoading(true);
    setLoadingGlobal(true);
    setError(null);

    const rolesResult = await adminService.obtenerRoles(token);
    if (rolesResult.success) {
      setRoles(rolesResult.data || []);
    } else {
      setError('Error al cargar roles');
    }

    const permisosResult = await adminService.obtenerPermisos(token);
    if (permisosResult.success) {
      setPermisosDisponibles(permisosResult.data || []);
    }

    setLoading(false);
    setLoadingGlobal(false);
  };

  const cargarPermisosDeRol = async (nombreRol) => {
    setLoadingPermisos((prev) => ({ ...prev, [nombreRol]: true }));
    const result = await adminService.obtenerPermisosDeRol(nombreRol, token);
    if (result.success) {
      setRolesData((prev) => ({
        ...prev,
        [nombreRol]: result.data || [],
      }));
    }
    setLoadingPermisos((prev) => ({ ...prev, [nombreRol]: false }));
  };

  const handleCrearRol = async (e) => {
    e.preventDefault();
    if (!nuevoRolNombre.trim()) {
      setError('El nombre del rol es obligatorio');
      return;
    }

    const nombreRolCreado = nuevoRolNombre.trim().toUpperCase();
    const result = await adminService.crearRol(
      nombreRolCreado, 
      nuevoRolDescripcion.trim(), 
      permisosSeleccionadosCrear,
      token,
      user?.username
    );
    if (result.success) {
      setSuccessMessage(result.message);
      setShowCrearModal(false);
      setNuevoRolNombre('');
      setNuevoRolDescripcion('');
      setPermisosSeleccionadosCrear([]);
      await cargarDatos();
      setSelectedRol(nombreRolCreado);
      await cargarPermisosDeRol(nombreRolCreado);
      setTimeout(() => setSuccessMessage(null), 3000);
    } else {
      setError(result.message);
      setTimeout(() => setError(null), 3000);
    }
  };

  const handleEliminarRol = async (nombreRol) => {
    if (!window.confirm(`¿Estás seguro de eliminar el rol ${nombreRol}?`)) {
      return;
    }

    const result = await adminService.eliminarRol(nombreRol, token, user?.username);
    if (result.success) {
      setSuccessMessage(result.message);
      setSelectedRol(null);
      await cargarDatos();
      setTimeout(() => setSuccessMessage(null), 3000);
    } else {
      setError(result.message);
      setTimeout(() => setError(null), 3000);
    }
  };

  const handleEditarRol = (rol) => {
    setRolEnEdicion(rol);
    setEditarNombre(rol.nombre);
    setEditarDescripcion(rol.descripcion || '');
    const permisosActuales = rolesData[rol.nombre]?.map(p => p.nombre) || [];
    setEditarPermisos(permisosActuales);
    setShowEditarModal(true);
  };

  const togglePermisoEditar = (nombrePermiso) => {
    setEditarPermisos(prev =>
      prev.includes(nombrePermiso)
        ? prev.filter(p => p !== nombrePermiso)
        : [...prev, nombrePermiso]
    );
  };

  const handleGuardarEdicion = async (e) => {
    e.preventDefault();

    const result = await adminService.actualizarRol(
      rolEnEdicion.nombre,
      editarNombre,
      editarDescripcion,
      token,
      user?.username
    );

    if (result.success) {
      const nombreFinal = editarNombre.toUpperCase();
      await adminService.asignarPermisosARol(nombreFinal, editarPermisos, token, user?.username);

      setSuccessMessage('Rol actualizado exitosamente');
      setShowEditarModal(false);

      await cargarDatos();

      const nombreParaSeleccionar = editarNombre.toUpperCase() !== rolEnEdicion.nombre.toUpperCase()
        ? nombreFinal
        : selectedRol;
      
      if (nombreParaSeleccionar) {
        setSelectedRol(nombreParaSeleccionar);
        await cargarPermisosDeRol(nombreParaSeleccionar);
      }

      setTimeout(() => setSuccessMessage(null), 3000);
    } else {
      setError(result.message);
      setTimeout(() => setError(null), 3000);
    }
  };

  const handleSelectRol = (rol) => {
    setSelectedRol(rol.nombre);
    if (!rolesData[rol.nombre]) {
      cargarPermisosDeRol(rol.nombre);
    }
    setPermisosSeleccionados([]);
  };

  const togglePermiso = (nombrePermiso) => {
    setPermisosSeleccionados((prev) =>
      prev.includes(nombrePermiso)
        ? prev.filter((p) => p !== nombrePermiso)
        : [...prev, nombrePermiso]
    );
  };

  const handleAsignarPermisos = async () => {
    if (!selectedRol || permisosSeleccionados.length === 0) {
      setError('Selecciona al menos un permiso');
      setTimeout(() => setError(null), 3000);
      return;
    }

    const result = await adminService.asignarPermisosARol(selectedRol, permisosSeleccionados, token);
    if (result.success) {
      setSuccessMessage(result.message);
      await cargarPermisosDeRol(selectedRol);
      setPermisosSeleccionados([]);
      setTimeout(() => setSuccessMessage(null), 3000);
    } else {
      setError(result.message);
      setTimeout(() => setError(null), 3000);
    }
  };

  const esRolFijo = (nombreRol) => ROLES_FIJOS.includes(nombreRol);

  const getPermisosDescription = (permisos) => {
    if (!permisos || permisos.length === 0) {
      return 'Sin permisos asignados';
    }
    const nombres = permisos.map(p => p.nombre);
    if (nombres.includes('*')) {
      return 'Todos los permisos (administrador)';
    }
    return nombres.join(', ');
  };

  const getPermisosCount = (permisos) => {
    if (!permisos || permisos.length === 0) return 0;
    const nombres = permisos.map(p => p.nombre);
    if (nombres.includes('*')) return 'Todos';
    return permisos.length;
  };

  const tienePermiso = (rolesPermisos, nombrePermiso) => {
    if (!rolesPermisos || rolesPermisos.length === 0) return false;
    return rolesPermisos.some(p => p.nombre === nombrePermiso);
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
          <div className="title-row">
            <div>
              <h1>Gestión de Roles</h1>
              <p>Administra los roles del sistema y sus permisos</p>
            </div>
            <button onClick={() => setShowCrearModal(true)} className="btn-crear">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <line x1="12" y1="5" x2="12" y2="19" />
                <line x1="5" y1="12" x2="19" y2="12" />
              </svg>
              Crear Rol
            </button>
          </div>
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

        {loadingGlobal ? (
          <div className="loading-container">
            <div className="spinner"></div>
            <p>Cargando información de roles...</p>
          </div>
        ) : (
          <div className="roles-grid">
            {roles.map((rol) => (
              <div
                key={rol.nombre}
                className={`rol-card rol-${rol.nombre.toLowerCase()} ${selectedRol === rol.nombre ? 'selected' : ''}`}
                onClick={() => handleSelectRol(rol)}
              >
                <div className="rol-header">
                  <div className="rol-icon">
                    {rol.nombre === 'ADMIN' && (
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                      </svg>
                    )}
                    {rol.nombre === 'USER' && (
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                        <circle cx="12" cy="7" r="4" />
                      </svg>
                    )}
                    {rol.nombre === 'AUDITOR' && (
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" />
                        <path d="M14 2v6h6" />
                        <line x1="16" y1="13" x2="8" y2="13" />
                        <line x1="16" y1="17" x2="8" y2="17" />
                      </svg>
                    )}
                    {!ROLES_FIJOS.includes(rol.nombre) && (
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                        <path d="M9 12l2 2 4-4" />
                      </svg>
                    )}
                  </div>
                  <div className="rol-title">
                    <h3>{rol.nombre}</h3>
                    <span className="rol-count">
                      {loadingPermisos[rol.nombre] ? (
                        'Cargando...'
                      ) : (
                        <>
                          {getPermisosCount(rolesData[rol.nombre])} permisos
                        </>
                      )}
                    </span>
                  </div>
                  {!esRolFijo(rol.nombre) && (
                    <div className="rol-actions">
                      <button
                        className="btn-editar-rol"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleEditarRol(rol);
                        }}
                        title="Editar rol"
                      >
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7" />
                          <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z" />
                        </svg>
                      </button>
                      <button
                        className="btn-eliminar-rol"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleEliminarRol(rol.nombre);
                        }}
                        title="Eliminar rol"
                      >
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <polyline points="3 6 5 6 21 6" />
                          <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2" />
                        </svg>
                      </button>
                    </div>
                  )}
                </div>
                <p className="rol-descripcion">{rol.descripcion}</p>
                <div className="rol-permisos">
                  {loadingPermisos[rol.nombre] ? (
                    <span className="loading-permisos">Cargando permisos...</span>
                  ) : (
                    <span className="permisos-preview">
                      {getPermisosDescription(rolesData[rol.nombre])}
                    </span>
                  )}
                </div>
                {esRolFijo(rol.nombre) && (
                  <span className="rol-fijo-badge">Protegido</span>
                )}
              </div>
            ))}
          </div>
        )}

        {selectedRol && permisosDisponibles.length > 0 && (
          <div className="permisos-detail">
            <div className="permisos-detail-header">
              <h3>Permisos del Rol: {selectedRol}</h3>
            </div>
            <div className="permisos-list">
              {permisosDisponibles.map((permiso) => (
                <div key={permiso.id || permiso.nombre} className="permiso-item">
                  <span className="permiso-nombre">{permiso.nombre}</span>
                  <span className={`permiso-badge ${tienePermiso(rolesData[selectedRol], permiso.nombre) ? 'active' : ''}`}>
                    {tienePermiso(rolesData[selectedRol], permiso.nombre) ? 'Asignado' : 'No asignado'}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>

      {showCrearModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>Crear Nuevo Rol</h2>
              <button className="modal-close" onClick={() => setShowCrearModal(false)}>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <line x1="18" y1="6" x2="6" y2="18" />
                  <line x1="6" y1="6" x2="18" y2="18" />
                </svg>
              </button>
            </div>
            <form onSubmit={handleCrearRol}>
              <div className="form-group">
                <label>Nombre del Rol</label>
                <input
                  type="text"
                  value={nuevoRolNombre}
                  onChange={(e) => setNuevoRolNombre(e.target.value.toUpperCase())}
                  placeholder="Ej: JEFE_RRHH"
                  required
                />
              </div>
              <div className="form-group">
                <label>Descripción</label>
                <textarea
                  value={nuevoRolDescripcion}
                  onChange={(e) => setNuevoRolDescripcion(e.target.value)}
                  placeholder="Descripción del rol"
                  rows="3"
                />
              </div>
              {permisosDisponibles.length > 0 && (
                <div className="form-group">
                  <label>Permisos iniciales</label>
                  <div className="permisos-checkbox-list">
                    {permisosDisponibles.map((permiso) => (
                      <label key={permiso.id || permiso.nombre} className="permiso-checkbox-item">
                        <input
                          type="checkbox"
                          checked={permisosSeleccionadosCrear.includes(permiso.nombre)}
                          onChange={() => togglePermisoCrear(permiso.nombre)}
                        />
                        <span className="permiso-checkbox-label">
                          <strong>{permiso.nombre}</strong>
                          {permiso.descripcion && <small>{permiso.descripcion}</small>}
                        </span>
                      </label>
                    ))}
                  </div>
                </div>
              )}
              <div className="modal-actions">
                <button type="button" className="btn-cancelar" onClick={() => setShowCrearModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn-crear-submit">
                  Crear Rol
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showEditarModal && (
        <div className="modal-overlay">
          <div className="modal modal-editar">
            <div className="modal-header">
              <h2>Editar Rol</h2>
              <button className="modal-close" onClick={() => setShowEditarModal(false)}>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <line x1="18" y1="6" x2="6" y2="18" />
                  <line x1="6" y1="6" x2="18" y2="18" />
                </svg>
              </button>
            </div>
            <form onSubmit={handleGuardarEdicion}>
              <div className="form-group">
                <label>Nombre del Rol</label>
                <input
                  type="text"
                  value={editarNombre}
                  onChange={(e) => setEditarNombre(e.target.value.toUpperCase())}
                  placeholder="Nombre del rol"
                  required
                />
              </div>
              <div className="form-group">
                <label>Descripción</label>
                <textarea
                  value={editarDescripcion}
                  onChange={(e) => setEditarDescripcion(e.target.value)}
                  placeholder="Descripción del rol"
                  rows="3"
                />
              </div>
              {permisosDisponibles.length > 0 && (
                <div className="form-group">
                  <label>Permisos</label>
                  <div className="permisos-checkbox-list">
                    {permisosDisponibles.map((permiso) => (
                      <label key={permiso.id || permiso.nombre} className="permiso-checkbox-item">
                        <input
                          type="checkbox"
                          checked={editarPermisos.includes(permiso.nombre)}
                          onChange={() => togglePermisoEditar(permiso.nombre)}
                        />
                        <span className="permiso-checkbox-label">
                          <strong>{permiso.nombre}</strong>
                          {permiso.descripcion && <small>{permiso.descripcion}</small>}
                        </span>
                      </label>
                    ))}
                  </div>
                </div>
              )}
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

export default GestionRoles;
