import { useState, useEffect, useMemo, useCallback, useRef } from 'react';
import {
  AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer, Brush, CartesianGrid
} from 'recharts';
import ipEstadisticasService from '../../Services/mapaCalor/ipEstadisticasService';
import { obtenerDashboard } from '../../Services/dashboard/auditorDashboardService';
import MapaCalorGrafico from './MapaCalorGrafico';
import {
  FilaVisitante, MetricasVisitante, TemporalFlowVisitante,
  TimeFrameVisitante, aceptar, COLORES_BARRA
} from './VisitanteIP';
import './MapaCalorIP.css';

const ITEMS_POR_PAGINA = 10;
const ANIM_WINDOW_HALF = 3;

const formatFecha = (fechaStr) => {
  if (!fechaStr) return '-';
  const d = new Date(fechaStr);
  return d.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });
};

const customAreaTooltip = ({ active, payload, label }) => {
  if (!active || !payload || !payload.length) return null;
  return (
    <div className="tl-tooltip">
      <span className="tl-tooltip-fecha">{label}</span>
      <span className="tl-tooltip-valor">{payload[0].value} evento{payload[0].value !== 1 ? 's' : ''}</span>
    </div>
  );
};

const MapaCalorIP = ({ onMetricasUpdate }) => {
  const [ips, setIps] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filtroDesde, setFiltroDesde] = useState('');
  const [filtroHasta, setFiltroHasta] = useState('');
  const [ipExpandida, setIpExpandida] = useState(null);
  const [detalleIp, setDetalleIp] = useState({});
  const [loadingDetalle, setLoadingDetalle] = useState({});
  const [pagina, setPagina] = useState(0);
  const [vista, setVista] = useState('tabla');

  const [actividadDiaria, setActividadDiaria] = useState([]);
  const [loadingTimeline, setLoadingTimeline] = useState(true);
  const [brushRange, setBrushRange] = useState({ startIdx: 0, endIdx: 0 });

  const [isPlaying, setIsPlaying] = useState(false);
  const [playSpeed, setPlaySpeed] = useState(5);
  const [timePosition, setTimePosition] = useState(0);

  const brushTimer = useRef(null);
  const playTimer = useRef(null);

  const temporalFlow = useMemo(() => new TemporalFlowVisitante(), []);
  const activityBuckets = useMemo(
    () => temporalFlow.transformar(actividadDiaria),
    [actividadDiaria, temporalFlow]
  );
  const timeRange = useMemo(() => temporalFlow.obtenerRango(activityBuckets), [activityBuckets, temporalFlow]);

  useEffect(() => {
    cargarDatos();
    cargarTimeline();
  }, []);

  useEffect(() => {
    if (activityBuckets.length > 0) {
      const endIdx = activityBuckets.length - 1;
      setBrushRange({ startIdx: 0, endIdx });
      setTimePosition(Math.floor(endIdx / 2));
    }
  }, [activityBuckets.length]);

  useEffect(() => {
    if (!isPlaying || !activityBuckets.length) {
      if (playTimer.current) clearInterval(playTimer.current);
      return;
    }
    const interval = Math.max(50, 300 / playSpeed);
    playTimer.current = setInterval(() => {
      setTimePosition((prev) => {
        const next = prev + 1;
        if (next >= activityBuckets.length) {
          setIsPlaying(false);
          return Math.floor(activityBuckets.length / 2);
        }
        return next;
      });
    }, interval);
    return () => {
      if (playTimer.current) clearInterval(playTimer.current);
    };
  }, [isPlaying, playSpeed, activityBuckets.length]);

  const cargarTimeline = async () => {
    setLoadingTimeline(true);
    const result = await obtenerDashboard();
    if (result.success && result.data?.actividadDiaria) {
      setActividadDiaria(result.data.actividadDiaria);
    }
    setLoadingTimeline(false);
  };

  const cargarDatos = async (desde, hasta) => {
    setLoading(true);
    setError(null);
    const result = await ipEstadisticasService.obtenerMapaCalor(desde || null, hasta || null);
    if (result.success) {
      setIps(result.data || []);
    } else {
      setError(result.message);
    }
    setLoading(false);
  };

  const currentWindow = useMemo(() => {
    if (!activityBuckets.length) return null;
    const startIdx = Math.max(0, timePosition - ANIM_WINDOW_HALF);
    const endIdx = Math.min(activityBuckets.length - 1, timePosition + ANIM_WINDOW_HALF);
    return {
      inicio: activityBuckets[startIdx].fecha,
      fin: activityBuckets[endIdx].fecha,
      startIdx,
      endIdx,
      centro: activityBuckets[timePosition]?.fecha,
    };
  }, [timePosition, activityBuckets]);

  const handleBrushChange = useCallback(({ startIndex, endIndex }) => {
    if (isPlaying) setIsPlaying(false);
    setBrushRange({ startIdx: startIndex, endIdx: endIndex });
    if (brushTimer.current) clearTimeout(brushTimer.current);
    brushTimer.current = setTimeout(() => {
      const desde = activityBuckets[startIndex]?.fecha;
      const hasta = activityBuckets[endIndex]?.fecha;
      cargarDatos(desde, hasta);
    }, 500);
  }, [activityBuckets, isPlaying]);

  const handleFiltrar = () => {
    setPagina(0);
    if (isPlaying) setIsPlaying(false);
    cargarDatos(filtroDesde || undefined, filtroHasta || undefined);
  };

  const limpiarFiltros = () => {
    setFiltroDesde('');
    setFiltroHasta('');
    setPagina(0);
    if (isPlaying) setIsPlaying(false);
    if (activityBuckets.length > 0) {
      setBrushRange({ startIdx: 0, endIdx: activityBuckets.length - 1 });
    }
    cargarDatos();
  };

  const irATodo = () => {
    if (isPlaying) setIsPlaying(false);
    setBrushRange({ startIdx: 0, endIdx: activityBuckets.length - 1 });
    cargarDatos();
  };

  const toggleDetalle = async (ip) => {
    if (ipExpandida === ip) {
      setIpExpandida(null);
      return;
    }
    setIpExpandida(ip);
    if (!detalleIp[ip]) {
      setLoadingDetalle((prev) => ({ ...prev, [ip]: true }));
      const result = await ipEstadisticasService.obtenerDetalleIp(ip);
      if (result.success) {
        setDetalleIp((prev) => ({ ...prev, [ip]: result.data || [] }));
      }
      setLoadingDetalle((prev) => ({ ...prev, [ip]: false }));
    }
  };

  const globalRange = useMemo(() => {
    if (!activityBuckets.length) return null;
    const inicio = new Date(activityBuckets[0].fecha);
    const fin = new Date(activityBuckets[activityBuckets.length - 1].fecha);
    return { inicio, fin, totalMs: fin - inicio || 1 };
  }, [activityBuckets]);

  const calcStripStyle = (ip) => {
    if (!globalRange) return { left: '0%', width: '0%' };
    const ipStart = new Date(ip.primeraVez);
    const ipEnd = new Date(ip.ultimaVez);
    const left = Math.max(0, ((ipStart - globalRange.inicio) / globalRange.totalMs) * 100);
    const right = Math.min(100, ((ipEnd - globalRange.inicio) / globalRange.totalMs) * 100);
    return { left: `${left}%`, width: `${Math.max(1, right - left)}%` };
  };

  const cursorPct = globalRange && currentWindow?.centro
    ? ((new Date(currentWindow.centro) - globalRange.inicio) / globalRange.totalMs) * 100
    : null;

  const handlePlayPause = () => {
    if (vista !== 'timelapse') setVista('timelapse');
    setIsPlaying((p) => !p);
  };

  const handleSpeedChange = (speed) => {
    setPlaySpeed(speed);
  };

  const handleSliderChange = (e) => {
    const val = parseInt(e.target.value, 10);
    setTimePosition(val);
    if (isPlaying) setIsPlaying(false);
  };

  const totalPaginas = useMemo(() => Math.max(1, Math.ceil(ips.length / ITEMS_POR_PAGINA)), [ips.length]);
  const inicio = pagina * ITEMS_POR_PAGINA;
  const ipsPagina = useMemo(() => ips.slice(inicio, inicio + ITEMS_POR_PAGINA), [ips, inicio]);
  const filasVisitadas = useMemo(() => aceptar(ipsPagina, new FilaVisitante()), [ipsPagina]);

  const filasAnimadas = useMemo(() => {
    if (vista !== 'timelapse' || !currentWindow || !ipsPagina.length) return null;
    const visitante = new TimeFrameVisitante(currentWindow.inicio, currentWindow.fin);
    return ipsPagina.map((ip) => visitante.visitar(ip));
  }, [vista, currentWindow, ipsPagina]);

  const metricas = useMemo(() => new MetricasVisitante().calcular(ips), [ips]);

  useEffect(() => {
    if (onMetricasUpdate) onMetricasUpdate(metricas);
  }, [metricas, onMetricasUpdate]);

  const irPagina = (n) => {
    if (n >= 0 && n < totalPaginas) {
      setPagina(n);
      setIpExpandida(null);
    }
  };

  const renderTimelineChart = () => {
    if (loadingTimeline) {
      return (
        <div className="tl-chart-container tl-loading">
          <div className="spinner-sm" />
          <span>Cargando línea de tiempo...</span>
        </div>
      );
    }
    if (!activityBuckets.length) return null;

    return (
      <div className="tl-chart-container">
        <ResponsiveContainer width="100%" height={140}>
          <AreaChart data={activityBuckets} margin={{ top: 5, right: 10, left: 0, bottom: 0 }}>
            <defs>
              <linearGradient id="tlGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#d4a853" stopOpacity={0.45} />
                <stop offset="100%" stopColor="#d4a853" stopOpacity={0.05} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(231,233,234,0.04)" vertical={false} />
            <XAxis
              dataKey="fecha"
              tick={{ fill: '#8899a6', fontSize: 10 }}
              tickFormatter={(v) => {
                const d = new Date(v);
                return `${d.getDate()}/${d.getMonth() + 1}`;
              }}
              axisLine={false}
              tickLine={false}
              minTickGap={40}
            />
            <YAxis hide />
            <Tooltip content={customAreaTooltip} cursor={{ stroke: '#d4a853', strokeDasharray: '3 3' }} />
            <Area
              type="monotone"
              dataKey="total"
              stroke="#d4a853"
              strokeWidth={2}
              fill="url(#tlGradient)"
              dot={false}
              activeDot={{ r: 4, fill: '#d4a853', stroke: '#0f1419', strokeWidth: 2 }}
            />
            {vista !== 'timelapse' && activityBuckets.length > 1 && (
              <Brush
                data={activityBuckets}
                dataKey="fecha"
                height={24}
                stroke="#d4a853"
                fill="#1a2332"
                travellerWidth={8}
                startIndex={brushRange.startIdx}
                endIndex={brushRange.endIdx}
                onChange={handleBrushChange}
                fillOpacity={0.15}
                strokeWidth={1}
              />
            )}
          </AreaChart>
        </ResponsiveContainer>
        {vista !== 'timelapse' && (
          <button className="tl-btn-todo" onClick={irATodo}>Todo</button>
        )}
      </div>
    );
  };

  const renderTimePlayer = () => {
    if (!activityBuckets.length) return null;
    const maxPos = activityBuckets.length - 1;
    return (
      <div className="tl-player">
        <div className="tl-player-controls">
          <button className={`tl-btn-play ${isPlaying ? 'is-playing' : ''}`} onClick={handlePlayPause} title={isPlaying ? 'Pausar' : 'Reproducir'}>
            {isPlaying ? (
              <svg viewBox="0 0 24 24" fill="currentColor"><rect x="6" y="4" width="4" height="16" rx="1" /><rect x="14" y="4" width="4" height="16" rx="1" /></svg>
            ) : (
              <svg viewBox="0 0 24 24" fill="currentColor"><polygon points="6,4 20,12 6,20" /></svg>
            )}
          </button>
          <div className="tl-speed-group">
            {[1, 5, 10, 50].map((s) => (
              <button
                key={s}
                className={`tl-speed-btn ${playSpeed === s ? 'speed-active' : ''}`}
                onClick={() => handleSpeedChange(s)}
              >{s}×</button>
            ))}
          </div>
        </div>
        <div className="tl-slider-wrap">
          <input
            type="range"
            className="tl-slider"
            min={0}
            max={maxPos}
            value={timePosition}
            onChange={handleSliderChange}
          />
          <div className="tl-slider-labels">
            <span>{currentWindow ? formatFecha(currentWindow.inicio) : '-'}</span>
            <span className="tl-slider-center">{currentWindow ? formatFecha(currentWindow.centro) : '-'}</span>
            <span>{currentWindow ? formatFecha(currentWindow.fin) : '-'}</span>
          </div>
        </div>
      </div>
    );
  };

  const renderRow = (fila, idx) => {
    const isAnimating = vista === 'timelapse' && filasAnimadas;
    const anim = isAnimating ? filasAnimadas[idx] : null;
    const rowStyle = {
      backgroundColor: isAnimating ? (anim._colorFondo) : fila._colorFondo,
      opacity: isAnimating ? anim._opacidad : 1,
    };

    return (
      <tr
        key={fila.ipOrigen}
        className={`mapa-fila ${ipExpandida === fila.ipOrigen ? 'expandida' : ''} ${isAnimating && !anim._activoEnVentana ? 'fila-inactiva' : 'fila-activa'}`}
        style={rowStyle}
        onClick={() => toggleDetalle(fila.ipOrigen)}
      >
        <td className="ip-cell">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="ip-icon">
            <rect x="2" y="2" width="20" height="20" rx="3" />
            <line x1="2" y1="10" x2="22" y2="10" />
            <line x1="10" y1="2" x2="10" y2="22" />
          </svg>
          {fila.ipOrigen}
        </td>
        <td className="num-cell">{fila.totalEventos}</td>
        <td className="num-cell">{fila.totalUsuariosDistintos}</td>
        <td>{fila._primeraVezStr}</td>
        <td>{fila._ultimaVezStr}</td>
        <td>
          {isAnimating ? (
            <div className="pulse-wrap">
              <div
                className="pulse-dot"
                style={{
                  backgroundColor: anim._pulsoColor,
                  '--pulse-scale': anim._pulsoEscala,
                  '--pulse-color': anim._pulsoColor,
                }}
              />
              <span className="pulse-label">{anim._intensidadVentana}/10</span>
            </div>
          ) : (
            <div className="intensity-bar-container">
              <div className="intensity-bar" style={{ width: fila._anchoBarra, backgroundColor: fila._colorBarra }} />
              <span className="intensity-label">{fila.nivelIntensidad}/10</span>
            </div>
          )}
        </td>
        <td>
          <span className={fila._estadoClase}>{fila._estadoLabel}</span>
        </td>
        {isAnimating && (
          <td className="heatstrip-cell">
            <div className="heatstrip">
              <div className="heatstrip-track">
                <div className="heatstrip-bar" style={calcStripStyle(fila)} />
                {cursorPct !== null && (
                  <div className="heatstrip-cursor" style={{ left: `${cursorPct}%` }} />
                )}
              </div>
            </div>
          </td>
        )}
      </tr>
    );
  };

  return (
    <div className="mapa-calor">
      <div className="mapa-calor-header">
        <div className="mapa-calor-contadores">
          <span className="contador-total">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="2" y="2" width="20" height="20" rx="3" />
              <line x1="2" y1="10" x2="22" y2="10" />
              <line x1="10" y1="2" x2="10" y2="22" />
            </svg>
            {ips.length} IP{ips.length !== 1 ? 's' : ''}
          </span>
          <span className="contador-sospechosas">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
              <line x1="12" y1="9" x2="12" y2="13" />
              <line x1="12" y1="17" x2="12.01" y2="17" />
            </svg>
            {metricas.totalSospechosas} sospechosa{metricas.totalSospechosas !== 1 ? 's' : ''}
          </span>
        </div>
        <div className="mapa-calor-vistas">
          <button className={`vista-btn ${vista === 'tabla' ? 'vista-activa' : ''}`} onClick={() => setVista('tabla')}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="3" width="18" height="18" rx="2" />
              <line x1="3" y1="9" x2="21" y2="9" />
              <line x1="9" y1="3" x2="9" y2="21" />
            </svg>
            Tabla
          </button>
          <button className={`vista-btn ${vista === 'grafico' ? 'vista-activa' : ''}`} onClick={() => setVista('grafico')}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="20" x2="18" y2="10" />
              <line x1="12" y1="20" x2="12" y2="4" />
              <line x1="6" y1="20" x2="6" y2="14" />
            </svg>
            Gráfico
          </button>
          <button className={`vista-btn ${vista === 'timelapse' ? 'vista-activa' : ''}`} onClick={() => { setVista('timelapse'); }}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
            TimeLapse
          </button>
        </div>
        <div className="mapa-calor-filtros">
          <div className="filtro-fecha">
            <label>Desde</label>
            <input type="date" value={filtroDesde} onChange={(e) => setFiltroDesde(e.target.value)} />
          </div>
          <div className="filtro-fecha">
            <label>Hasta</label>
            <input type="date" value={filtroHasta} onChange={(e) => setFiltroHasta(e.target.value)} />
          </div>
          <button onClick={handleFiltrar} className="btn-filtrar">Filtrar</button>
          <button onClick={limpiarFiltros} className="btn-limpiar">Limpiar</button>
        </div>
      </div>

      {renderTimelineChart()}

      {vista === 'timelapse' && renderTimePlayer()}

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Cargando mapa de calor...</p>
        </div>
      ) : ips.length === 0 ? (
        <div className="empty-message">No hay datos de IP disponibles</div>
      ) : (
        <>
          {vista === 'tabla' || vista === 'timelapse' ? (
            <div className="mapa-calor-table-wrapper">
              <table className="mapa-calor-table">
                <thead>
                  <tr>
                    <th>IP</th>
                    <th>Eventos</th>
                    <th>Usuarios Distintos</th>
                    <th>Primera Vez</th>
                    <th>Última Vez</th>
                    <th>{vista === 'timelapse' ? 'Actividad' : 'Nivel de Actividad'}</th>
                    <th>Estado</th>
                    {vista === 'timelapse' && <th>Linea de Tiempo</th>}
                  </tr>
                </thead>
                <tbody>
                  {(vista === 'timelapse' && filasAnimadas ? filasAnimadas : filasVisitadas).map((fila, idx) => renderRow(
                    vista === 'timelapse' ? filasVisitadas[idx] : fila,
                    idx
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <MapaCalorGrafico ips={ipsPagina} />
          )}

          {totalPaginas > 1 && (
            <div className="pagination">
              <button onClick={() => irPagina(0)} disabled={pagina === 0} title="Primera">
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
              <button onClick={() => irPagina(totalPaginas - 1)} disabled={pagina >= totalPaginas - 1} title="Última">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="13 17 18 12 13 7" />
                  <polyline points="6 17 11 12 6 7" />
                </svg>
              </button>
            </div>
          )}

          {ipExpandida && (
            <div className="detalle-acordeon">
              <div className="detalle-header">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <rect x="2" y="2" width="20" height="20" rx="3" />
                  <line x1="2" y1="10" x2="22" y2="10" />
                  <line x1="10" y1="2" x2="10" y2="22" />
                </svg>
                Últimos eventos de: <strong>{ipExpandida}</strong>
              </div>
              {loadingDetalle[ipExpandida] ? (
                <div className="loading-container">
                  <div className="spinner"></div>
                </div>
              ) : (
                <table className="detalle-table">
                  <thead>
                    <tr>
                      <th>Usuario ID</th>
                      <th>Acción</th>
                      <th>Descripción</th>
                      <th>Fecha</th>
                      <th>Tipo</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(detalleIp[ipExpandida] || []).length === 0 ? (
                      <tr>
                        <td colSpan="5" className="empty-message">Sin eventos registrados</td>
                      </tr>
                    ) : (
                      (detalleIp[ipExpandida] || []).map((evt, idx) => (
                        <tr key={idx}>
                          <td>{evt.usuarioId}</td>
                          <td>
                            <span className={`accion-badge accion-${(evt.accion || '').toLowerCase()}`}>
                              {evt.accion}
                            </span>
                          </td>
                          <td>{evt.descripcion || '-'}</td>
                          <td>{new Date(evt.fecha).toLocaleString('es-ES')}</td>
                          <td>
                            <span className={`tipo-badge tipo-${(evt.tipo || '').toLowerCase()}`}>
                              {evt.tipo}
                            </span>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default MapaCalorIP;
