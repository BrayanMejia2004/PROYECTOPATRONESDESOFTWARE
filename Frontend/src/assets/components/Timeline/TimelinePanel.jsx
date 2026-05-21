import { useState, useEffect } from 'react';
import { obtenerTimeline, obtenerResumenUsuario } from '../../Services/timeline/timelineService';
import './TimelinePanel.css';

const TimelinePanel = ({ usuarioId, onCerrar }) => {
  const [resumen, setResumen] = useState(null);
  const [timeline, setTimeline] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [limite, setLimite] = useState(50);

  useEffect(() => {
    cargarDatos();
  }, [usuarioId]);

  const cargarDatos = async () => {
    setLoading(true);
    setError(null);

    const [resumenResult, timelineResult] = await Promise.all([
      obtenerResumenUsuario(usuarioId),
      obtenerTimeline(usuarioId, limite)
    ]);

    if (resumenResult.success) {
      setResumen(resumenResult.data);
    }

    if (timelineResult.success) {
      setTimeline(timelineResult.data || []);
    } else {
      setError(timelineResult.message || 'Error al cargar timeline');
    }

    setLoading(false);
  };

  const cargarMas = async () => {
    const nuevoLimite = limite + 50;
    setLimite(nuevoLimite);
    const result = await obtenerTimeline(usuarioId, nuevoLimite);
    if (result.success) {
      setTimeline(result.data || []);
    }
  };

  const formatearFecha = (fechaStr) => {
    if (!fechaStr) return '-';
    return new Date(fechaStr).toLocaleString('es-ES');
  };

  const getColorPorTipo = (tipo) => {
    if (tipo === 'BASICA') return '#3b82f6';
    if (tipo === 'COMPLETA') return '#10b981';
    if (tipo === 'SEGURIDAD') return '#ef4444';
    return '#6b7280';
  };

  const getIconoPorTipo = (tipo) => {
    if (tipo === 'BASICA') return 'person';
    if (tipo === 'COMPLETA') return 'badge';
    if (tipo === 'SEGURIDAD') return 'lock';
    return 'info';
  };

  return (
    <div className="timeline-overlay" onClick={onCerrar}>
      <div className="timeline-panel" onClick={(e) => e.stopPropagation()}>
        <div className="timeline-header">
          <h2>Timeline de Usuario</h2>
          <button className="timeline-close" onClick={onCerrar}>×</button>
        </div>

        {loading ? (
          <div className="timeline-loading">
            <div className="spinner"></div>
            <p>Cargando...</p>
          </div>
        ) : error ? (
          <div className="timeline-error">{error}</div>
        ) : (
          <div className="timeline-content">
            {resumen && (
              <div className="timeline-user-info">
                <div className="user-avatar-lg">
                  {(resumen.nombre?.[0] || resumen.username?.[0] || 'U').toUpperCase()}
                </div>
                <div className="user-details">
                  <h3>{resumen.nombre} {resumen.apellido}</h3>
                  <p>@{resumen.username}</p>
                  <p>{resumen.email}</p>
                  <span className={`estado-badge ${resumen.estado ? 'activo' : 'inactivo'}`}>
                    {resumen.estado ? '● Activo' : '○ Inactivo'}
                  </span>
                </div>
              </div>
            )}

            <div className="timeline-list">
              {timeline.length === 0 ? (
                <p className="no-events">No hay eventos registrados</p>
              ) : (
                timeline.map((evento, index) => (
                  <div key={evento.id} className="timeline-item">
                    <div className="timeline-marker" style={{ backgroundColor: getColorPorTipo(evento.tipo) }}>
                      <span className="material-symbols-outlined timeline-icon">{getIconoPorTipo(evento.tipo)}</span>
                    </div>
                    {index < timeline.length - 1 && <div className="timeline-line"></div>}
                    <div className="timeline-content-item">
                      <div className="timeline-header-item">
                        <strong>{evento.accion}</strong>
                        <span className="timeline-fecha">{formatearFecha(evento.fecha)}</span>
                      </div>
                      <p className="timeline-descripcion">{evento.descripcion}</p>
                      {evento.ipOrigen && (
                        <small className="timeline-ip">IP: {evento.ipOrigen}</small>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>

            {timeline.length >= limite && (
              <button className="btn-cargar-mas" onClick={cargarMas}>
                Ver más
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default TimelinePanel;
