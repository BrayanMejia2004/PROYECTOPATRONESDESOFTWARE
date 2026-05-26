import { useState, useEffect, useRef, useCallback, useMemo } from 'react';
import { obtenerTimeline, obtenerResumenUsuario, obtenerActividadCalendario } from '../../Services/timeline/timelineService';
import { CronologicoIterador, InversoIterador, FiltroIterador } from './TimelineIterador';
import { generarDistribucionHoras } from '../../Utils/datosSimulados';
import './TimelinePanel.css';

const TIPOS_EVENTO = [
  { key: null, label: 'Todas', color: '#8899a6' },
  { key: 'BASICA', label: 'Básica', color: '#3b82f6' },
  { key: 'COMPLETA', label: 'Completa', color: '#10b981' },
  { key: 'SEGURIDAD', label: 'Seguridad', color: '#ef4444' },
];

const parseLocalDate = (str) => {
  const [y, m, d] = str.split('-').map(Number);
  return new Date(y, m - 1, d);
};

const agruparPorSemana = (dias) => {
  const grupos = [];
  let semana = [];
  const primerDia = parseLocalDate(dias[0].fecha).getDay();
  const idxLun = primerDia === 0 ? 6 : primerDia - 1;
  for (let i = 0; i < idxLun; i++) semana.push(null);
  dias.forEach((d) => {
    semana.push(d);
    if (semana.length === 7) {
      grupos.push(semana);
      semana = [];
    }
  });
  if (semana.length > 0) {
    while (semana.length < 7) semana.push(null);
    grupos.push(semana);
  }
  return grupos;
};

const TimelinePanel = ({ usuarioId, onCerrar }) => {
  const [resumen, setResumen] = useState(null);
  const [eventos, setEventos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [orden, setOrden] = useState('asc');
  const [filtroTipo, setFiltroTipo] = useState(null);
  const [filtroFecha, setFiltroFecha] = useState(null);
  const [filtroHora, setFiltroHora] = useState(null);
  const [analisisAbierto, setAnalisisAbierto] = useState(true);
  const [calendarioBackend, setCalendarioBackend] = useState(null);
  const [loadingCalendario, setLoadingCalendario] = useState(false);
  const iteradorRef = useRef(null);

  const distribucionHoras = useMemo(() => generarDistribucionHoras(), []);

  useEffect(() => {
    const cargarCalendario = async () => {
      setLoadingCalendario(true);
      const result = await obtenerActividadCalendario(usuarioId, 28);
      if (result.success && result.data?.length > 0) {
        setCalendarioBackend(result.data.map(d => ({
          fecha: d.fecha,
          valor: d.total,
        })));
      } else {
        setCalendarioBackend(null);
      }
      setLoadingCalendario(false);
    };
    cargarCalendario();
  }, [usuarioId]);

  const construirIterador = useCallback(() => {
    let base = orden === 'asc'
      ? new CronologicoIterador(usuarioId, obtenerTimeline)
      : new InversoIterador(usuarioId, obtenerTimeline);

    if (filtroTipo) {
      base = new FiltroIterador(usuarioId, () => Promise.resolve({ success: true, data: base.eventos }), (evt) => evt.tipo === filtroTipo);
      base._eventos = iteradorRef.current?._eventos || [];
      base._limite = iteradorRef.current?._limite || 50;
      return base;
    }

    return base;
  }, [usuarioId, orden, filtroTipo]);

  useEffect(() => {
    const it = construirIterador();
    iteradorRef.current = it;
    cargarDatos(it);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [construirIterador]);

  const cargarDatos = async (it) => {
    setLoading(true);
    setError(null);

    const [resumenResult, timelineResult] = await Promise.all([
      obtenerResumenUsuario(usuarioId),
      it.cargar()
    ]);

    if (resumenResult.success) {
      setResumen(resumenResult.data);
    }

    if (timelineResult.success) {
      setEventos(it.eventos);
    } else {
      setError(timelineResult.message || 'Error al cargar timeline');
    }

    setLoading(false);
  };

  const eventosFiltrados = useMemo(() => {
    let items = eventos;
    if (filtroFecha) {
      items = items.filter((e) => {
        if (!e.fecha) return false;
        return e.fecha.split('T')[0] === filtroFecha;
      });
    }
    if (filtroHora) {
      items = items.filter((e) => {
        if (!e.fecha) return false;
        const h = new Date(e.fecha).getHours();
        const [hMin, hMax] = filtroHora.split('-').map(Number);
        return h >= hMin && h <= hMax;
      });
    }
    return items;
  }, [eventos, filtroFecha, filtroHora]);

  const cargarMas = async () => {
    const it = iteradorRef.current;
    await it.cargarMas();
    setEventos(it.eventos);
  };

  const limpiarFiltros = () => {
    setFiltroTipo(null);
    setFiltroFecha(null);
    setFiltroHora(null);
  };

  const hayFiltrosActivos = filtroTipo || filtroFecha || filtroHora;

  const calendario = useMemo(() => {
    const result = [];
    const mapBackend = {};
    if (calendarioBackend && calendarioBackend.length > 0) {
      calendarioBackend.forEach(d => { mapBackend[d.fecha] = d.valor; });
    }
    const maxVal = Math.max(...Object.values(mapBackend), 1);

    for (let i = 27; i >= 0; i--) {
      const dt = new Date();
      dt.setDate(dt.getDate() - i);
      const fechaLocal = `${dt.getFullYear()}-${String(dt.getMonth() + 1).padStart(2, '0')}-${String(dt.getDate()).padStart(2, '0')}`;
      const valor = mapBackend[fechaLocal] || 0;
      result.push({
        fecha: fechaLocal,
        diaSem: dt.toLocaleDateString('es-ES', { weekday: 'short' }),
        diaNum: dt.getDate(),
        valor,
        nivel: valor === 0 ? 0 : valor <= Math.round(maxVal * 0.25) ? 1 : valor <= Math.round(maxVal * 0.5) ? 2 : valor <= Math.round(maxVal * 0.75) ? 3 : 4,
      });
    }
    return result;
  }, [calendarioBackend]);

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

  const CAL_NIVEL_COLORES = ['#1a2332', '#153044', '#1a5276', '#d4a853', '#f5a623'];
  const CAL_NIVEL_TEXT = ['#5e6f7d', '#5e6f7d', '#8899a6', '#0f1419', '#0f1419'];

  const diasSemanaHead = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];

  const semanas = useMemo(() => agruparPorSemana(calendario), [calendario]);

  const maxCalor = Math.max(...calendario.map((d) => d.valor), 1);

  return (
    <div className="timeline-overlay" onClick={onCerrar}>
      <div className="timeline-panel" onClick={(e) => e.stopPropagation()}>
        <div className="timeline-header">
          <h2>Timeline de Usuario</h2>
          <span className="timeline-id-badge">ID: {usuarioId}</span>
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

            <div className={`analisis-section ${analisisAbierto ? 'abierto' : ''}`}>
              <div className="analisis-header" onClick={() => setAnalisisAbierto(!analisisAbierto)}>
                <span className="analisis-titulo">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" />
                    <line x1="16" y1="2" x2="16" y2="6" />
                    <line x1="8" y1="2" x2="8" y2="6" />
                    <line x1="3" y1="10" x2="21" y2="10" />
                  </svg>
                  Análisis Temporal
                </span>
                <span className="analisis-toggle">{analisisAbierto ? '▲' : '▼'}</span>
              </div>

              {analisisAbierto && (
                <div className="analisis-body">
                  <div className="analisis-subsection">
                    <div className="analisis-subsection-header">
                      <span className="analisis-subtitulo">Calendario de Actividad {loadingCalendario ? <span className="cal-loading-dot" /> : ''}</span>
                      {!loadingCalendario && calendarioBackend && <span className="cal-datos-reales">BD</span>}
                      {!loadingCalendario && !calendarioBackend && <span className="cal-datos-simulados">SIM</span>}
                    </div>
                    <div className="calendar-grid">
                      <div className="calendar-headers">
                        {diasSemanaHead.map((d) => (
                          <span key={d} className="cal-header">{d}</span>
                        ))}
                      </div>
                      <div className="calendar-body">
                        {semanas.map((semana, si) => (
                          <div key={si} className="cal-semana">
                            {semana.map((dia, di) => {
                              if (!dia) return <div key={`e-${di}`} className="cal-dia cal-dia-vacio" />;
                              const activo = filtroFecha === dia.fecha;
                              return (
                                <div
                                  key={dia.fecha}
                                  className={`cal-dia ${activo ? 'cal-dia-activo' : ''} ${dia.valor === 0 ? 'cal-dia-sin' : ''}`}
                                  style={{
                                    backgroundColor: CAL_NIVEL_COLORES[dia.nivel],
                                    color: CAL_NIVEL_TEXT[dia.nivel],
                                  }}
                                  onClick={() => setFiltroFecha(filtroFecha === dia.fecha ? null : dia.fecha)}
                                  title={`${dia.fecha}: ${dia.valor} eventos`}
                                >
                                  <span className="cal-dia-num">{dia.diaNum}</span>
                                  {dia.valor > 0 && <span className="cal-dia-bar" style={{ width: `${Math.min(100, (dia.valor / maxCalor) * 100)}%` }} />}
                                </div>
                              );
                            })}
                          </div>
                        ))}
                      </div>
                      <div className="cal-legend">
                        <span className="cal-legend-item"><span className="cal-legend-swatch" style={{ background: CAL_NIVEL_COLORES[0] }} />Sin</span>
                        <span className="cal-legend-item"><span className="cal-legend-swatch" style={{ background: CAL_NIVEL_COLORES[1] }} />Baja</span>
                        <span className="cal-legend-item"><span className="cal-legend-swatch" style={{ background: CAL_NIVEL_COLORES[2] }} />Media</span>
                        <span className="cal-legend-item"><span className="cal-legend-swatch" style={{ background: CAL_NIVEL_COLORES[3] }} />Alta</span>
                        <span className="cal-legend-item"><span className="cal-legend-swatch" style={{ background: CAL_NIVEL_COLORES[4] }} />Máxima</span>
                      </div>
                    </div>
                  </div>

                  <div className="analisis-subsection">
                    <span className="analisis-subtitulo">Actividad por Horas</span>
                    <div className="horas-bars">
                      {distribucionHoras.map((r) => {
                        const activo = filtroHora === `${r.hInicio}-${r.hFin}`;
                        const maxHora = Math.max(...distribucionHoras.map((x) => x.eventos), 1);
                        const altura = (r.eventos / maxHora) * 100;
                        return (
                          <div
                            key={r.label}
                            className={`hora-bar-item ${activo ? 'hora-bar-activo' : ''}`}
                            onClick={() => setFiltroHora(filtroHora === `${r.hInicio}-${r.hFin}` ? null : `${r.hInicio}-${r.hFin}`)}
                            title={`${r.label}h: ${r.eventos} eventos`}
                          >
                            <span className="hora-bar-valor">{r.eventos}</span>
                            <div className="hora-bar-track">
                              <div className="hora-bar-fill" style={{ height: `${altura}%` }} />
                            </div>
                            <span className="hora-bar-label">{r.label}</span>
                          </div>
                        );
                      })}
                    </div>
                  </div>

                  <div className="analisis-subsection">
                    <span className="analisis-subtitulo">Filtrar por Tipo</span>
                    <div className="tipo-pills">
                      {TIPOS_EVENTO.map((t) => (
                        <button
                          key={t.key || 'todas'}
                          className={`tipo-pill ${filtroTipo === t.key ? 'tipo-pill-activo' : ''}`}
                          style={filtroTipo === t.key ? { backgroundColor: t.color + '22', borderColor: t.color, color: t.color } : {}}
                          onClick={() => setFiltroTipo(filtroTipo === t.key ? null : t.key)}
                        >
                          {t.label}
                        </button>
                      ))}
                    </div>
                  </div>
                </div>
              )}
            </div>

            {hayFiltrosActivos && (
              <div className="filtros-activos">
                {filtroFecha && (
                  <span className="filtro-chip" onClick={() => setFiltroFecha(null)}>
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <rect x="3" y="4" width="18" height="18" rx="2" />
                      <line x1="16" y1="2" x2="16" y2="6" />
                      <line x1="8" y1="2" x2="8" y2="6" />
                      <line x1="3" y1="10" x2="21" y2="10" />
                    </svg>
                    {parseLocalDate(filtroFecha).toLocaleDateString('es-ES', { day: 'numeric', month: 'short' })}
                    <span className="filtro-chip-x">✕</span>
                  </span>
                )}
                {filtroHora && (
                  <span className="filtro-chip" onClick={() => setFiltroHora(null)}>
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <circle cx="12" cy="12" r="10" />
                      <polyline points="12 6 12 12 16 14" />
                    </svg>
                    {filtroHora}h
                    <span className="filtro-chip-x">✕</span>
                  </span>
                )}
                {filtroTipo && (
                  <span className="filtro-chip" onClick={() => setFiltroTipo(null)}>
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
                      <polyline points="22 4 12 14.01 9 11.01" />
                    </svg>
                    {filtroTipo}
                    <span className="filtro-chip-x">✕</span>
                  </span>
                )}
                <button className="filtro-limpiar" onClick={limpiarFiltros}>Limpiar</button>
              </div>
            )}

            <div className="timeline-controls">
              <button
                className={`btn-orden ${orden === 'asc' ? 'active' : ''}`}
                onClick={() => setOrden(o => o === 'asc' ? 'desc' : 'asc')}
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <line x1="12" y1="5" x2="12" y2="19" />
                  <polyline points="19 12 12 19 5 12" />
                </svg>
                {orden === 'asc' ? 'Más antiguos' : 'Más recientes'}
              </button>
              <span className="eventos-count">{eventosFiltrados.length} eventos</span>
            </div>

            <div className="timeline-list">
              {eventosFiltrados.length === 0 ? (
                <p className="no-events">No hay eventos{hayFiltrosActivos ? ' con los filtros actuales' : ' registrados'}</p>
              ) : (
                eventosFiltrados.map((evento, index) => (
                  <div key={evento.id} className="timeline-item">
                    <div className="timeline-marker" style={{ backgroundColor: getColorPorTipo(evento.tipo) }}>
                      <span className="material-symbols-outlined timeline-icon">{getIconoPorTipo(evento.tipo)}</span>
                    </div>
                    {index < eventosFiltrados.length - 1 && <div className="timeline-line"></div>}
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

            {iteradorRef.current?.hayMas() && !hayFiltrosActivos && (
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
