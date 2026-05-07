import { useState, useEffect } from 'react';
import { useAuth } from '../Context/AuthContext';
import reportesService from '../Services/reportesService';
import './Auditoria.css';

const Auditoria = () => {
  const { token, isAuditor } = useAuth();
  const [auditorias, setAuditorias] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [loadingReportes, setLoadingReportes] = useState(false);
  const [secciones, setSecciones] = useState({
    encabezado: true,
    resumen: true,
    detalle: true,
    pie: true
  });
  const [filtros, setFiltros] = useState({
    usuarioId: '',
    fechaDesde: '',
    fechaHasta: '',
    tipo: '',
    accion: ''
  });
  const [pagina, setPagina] = useState(0);
  const itemsPorPagina = 10;

  useEffect(() => {
    cargarAuditorias();
  }, []);

  const irPagina = (p) => {
    setPagina(Math.max(0, Math.min(p, totalPaginas - 1)));
  };

  const cargarAuditorias = async () => {
    setLoading(true);
    setError(null);
    setPagina(0);

    try {
      const params = new URLSearchParams();
      if (filtros.usuarioId) params.append('usuarioId', filtros.usuarioId);
      if (filtros.fechaDesde) params.append('fechaDesde', filtros.fechaDesde);
      if (filtros.fechaHasta) params.append('fechaHasta', filtros.fechaHasta);
      if (filtros.tipo) params.append('tipo', filtros.tipo);
      if (filtros.accion) params.append('accion', filtros.accion);

      const response = await fetch(`http://localhost:8080/api/auditoria/lista?${params.toString()}`, {
        headers: { 'Content-Type': 'application/json' }
      });

      if (response.ok) {
        const data = await response.json();
        setAuditorias(data || []);
      } else {
        setError('Error al cargar auditorías');
      }
    } catch (err) {
      setError('Error de conexión: ' + err.message);
    }

    setLoading(false);
  };

  const handleFiltroChange = (e) => {
    const { name, value } = e.target;
    setFiltros(prev => ({ ...prev, [name]: value }));
  };

  const handleSeccionChange = (seccion) => {
    setSecciones(prev => ({ ...prev, [seccion]: !prev[seccion] }));
  };

  const limpiarFiltros = () => {
    setFiltros({
      usuarioId: '',
      fechaDesde: '',
      fechaHasta: '',
      tipo: '',
      accion: ''
    });
    setPagina(0);
  };

  const generarReporte = async (formato) => {
    setLoadingReportes(true);
    setError(null);

    const seccionesParam = Object.entries(secciones)
      .filter(([_, selected]) => selected)
      .map(([nombre]) => nombre.toUpperCase())
      .join(',');

    const result = await reportesService.generarReporte('AUDITORIA', formato, filtros, token, seccionesParam);

    if (result.success) {
      let extension;
      if (formato.toUpperCase().includes('ZIP')) {
        extension = '.zip';
      } else if (formato.toLowerCase().includes('csv')) {
        extension = '.csv';
      } else {
        extension = '.pdf';
      }
      reportesService.descargarBlob(result.data, `reporte_auditoria_${Date.now()}${extension}`);
      setSuccessMessage(`Reporte ${formato} generado exitosamente`);
      setTimeout(() => setSuccessMessage(null), 3000);
    } else {
      setError('Error al generar reporte');
    }

    setLoadingReportes(false);
  };

  const formatearFecha = (fecha) => {
    if (!fecha) return '-';
    const date = new Date(fecha);
    return date.toLocaleString('es-ES');
  };

  const totalPaginas = Math.max(1, Math.ceil(auditorias.length / itemsPorPagina));
  const auditoriasPagina = auditorias.slice(pagina * itemsPorPagina, (pagina + 1) * itemsPorPagina);

  if (!isAuditor()) {
    return null;
  }

  return (
    <div className="auditoria-container">
      <main className="auditoria-main">
        <div className="auditoria-title">
          <h1>Auditoría del Sistema</h1>
          <p>Revisa el historial de acciones y genera reportes</p>
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

        <div className="filtros-section">
          <h3>Filtrar Auditorías</h3>
          <div className="filtros-grid">
            <div className="filtro-item">
              <label>Usuario ID</label>
              <input
                type="number"
                name="usuarioId"
                value={filtros.usuarioId}
                onChange={handleFiltroChange}
                placeholder="ID de usuario"
              />
            </div>
            <div className="filtro-item">
              <label>Fecha Desde</label>
              <input
                type="datetime-local"
                name="fechaDesde"
                value={filtros.fechaDesde}
                onChange={handleFiltroChange}
              />
            </div>
            <div className="filtro-item">
              <label>Fecha Hasta</label>
              <input
                type="datetime-local"
                name="fechaHasta"
                value={filtros.fechaHasta}
                onChange={handleFiltroChange}
              />
            </div>
            <div className="filtro-item">
              <label>Tipo</label>
              <select name="tipo" value={filtros.tipo} onChange={handleFiltroChange}>
                <option value="">Todos</option>
                <option value="BASICA">Básica</option>
                <option value="COMPLETA">Completa</option>
                <option value="SEGURIDAD">Seguridad</option>
              </select>
            </div>
            <div className="filtro-item">
              <label>Acción</label>
              <input
                type="text"
                name="accion"
                value={filtros.accion}
                onChange={handleFiltroChange}
                placeholder="Ej: LOGIN, CREAR_USUARIO"
              />
            </div>
          </div>
          <div className="filtros-actions">
            <button onClick={cargarAuditorias} className="btn-aplicar">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
              </svg>
              Aplicar Filtros
            </button>
            <button onClick={limpiarFiltros} className="btn-limpiar">
              Limpiar
            </button>
          </div>
        </div>

        <div className="reportes-section">
          <h3>Generar Reportes</h3>
          <div className="secciones-checklist">
            <span className="secciones-label">Secciones:</span>
            <label className="seccion-checkbox">
              <input
                type="checkbox"
                checked={secciones.encabezado}
                onChange={() => handleSeccionChange('encabezado')}
              />
              <span>Encabezado</span>
            </label>
            <label className="seccion-checkbox">
              <input
                type="checkbox"
                checked={secciones.resumen}
                onChange={() => handleSeccionChange('resumen')}
              />
              <span>Resumen</span>
            </label>
            <label className="seccion-checkbox">
              <input
                type="checkbox"
                checked={secciones.detalle}
                onChange={() => handleSeccionChange('detalle')}
              />
              <span>Detalle</span>
            </label>
            <label className="seccion-checkbox">
              <input
                type="checkbox"
                checked={secciones.pie}
                onChange={() => handleSeccionChange('pie')}
              />
              <span>Pie</span>
            </label>
          </div>
          <div className="reportes-buttons">
            <select
              id="formato-select"
              className="formato-select"
              defaultValue=""
            >
              <option value="" disabled>Seleccionar formato</option>
              <option value="PDF">PDF</option>
              <option value="CSV">CSV</option>
              <option value="PDFZIP">PDF + Comprimir (ZIP)</option>
              <option value="CSVZIP">CSV + Comprimir (ZIP)</option>
            </select>
            <button
              onClick={() => {
                const select = document.getElementById('formato-select');
                const formato = select.value;
                if (formato) {
                  generarReporte(formato);
                }
              }}
              className="btn-reporte btn-generar"
              disabled={loadingReportes}
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" />
                <polyline points="7 10 12 15 17 10" />
                <line x1="12" y1="15" x2="12" y2="3" />
              </svg>
              {loadingReportes ? 'Generando...' : 'Descargar'}
            </button>
          </div>
        </div>

        {loading ? (
          <div className="loading-container">
            <div className="spinner"></div>
            <p>Cargando auditorías...</p>
          </div>
        ) : (
          <div className="auditorias-table-container">
            <table className="auditorias-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Usuario ID</th>
                  <th>Acción</th>
                  <th>Descripción</th>
                  <th>Fecha</th>
                  <th>IP</th>
                  <th>Tipo</th>
                </tr>
              </thead>
              <tbody>
                {auditorias.length === 0 ? (
                  <tr>
                    <td colSpan="7" className="empty-message">
                      No hay registros de auditoría
                    </td>
                  </tr>
                ) : (
                  auditoriasPagina.map((aud) => (
                    <tr key={aud.id}>
                      <td>{aud.id}</td>
                      <td>{aud.usuario_id}</td>
                      <td>
                        <span className={`accion-badge accion-${(aud.accion || '').toLowerCase()}`}>
                          {aud.accion}
                        </span>
                      </td>
                      <td>{aud.descripcion || '-'}</td>
                      <td>{formatearFecha(aud.fecha)}</td>
                      <td>
                        {aud.tipo === 'BASICA' ? '-' : (aud.ip_origen || '-')}
                      </td>
                      <td>
                        <span className={`tipo-badge tipo-${(aud.tipo || '').toLowerCase()}`}>
                          {aud.tipo}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
            <div className="table-footer">
              <span>
                Mostrando {auditorias.length === 0 ? 0 : pagina * itemsPorPagina + 1}-
                {Math.min((pagina + 1) * itemsPorPagina, auditorias.length)} de {auditorias.length} registros
              </span>
            </div>
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
          </div>
        )}
      </main>
    </div>
  );
};

export default Auditoria;
