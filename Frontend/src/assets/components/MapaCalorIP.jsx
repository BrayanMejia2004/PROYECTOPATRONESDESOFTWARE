import { useState, useEffect, useMemo } from 'react';
import ipEstadisticasService from '../Services/ipEstadisticasService';
import MapaCalorGrafico from './MapaCalorGrafico';
import './MapaCalorIP.css';

const ITEMS_POR_PAGINA = 10;

const getColorPorNivel = (nivel) => {
  if (!nivel || nivel <= 0) return '#1a2332';
  if (nivel <= 3) return 'rgba(234, 179, 8, 0.2)';
  if (nivel <= 6) return 'rgba(249, 115, 22, 0.25)';
  if (nivel <= 9) return 'rgba(239, 68, 68, 0.3)';
  return 'rgba(185, 28, 28, 0.45)';
};

const getColorBarraPorNivel = (nivel) => {
  if (!nivel || nivel <= 0) return '#1a2332';
  if (nivel <= 3) return '#eab308';
  if (nivel <= 6) return '#f97316';
  if (nivel <= 9) return '#ef4444';
  return '#b91c1c';
};

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  const date = new Date(fecha);
  return date.toLocaleString('es-ES');
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

  useEffect(() => {
    cargarDatos();
  }, []);

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

  const handleFiltrar = () => {
    setPagina(0);
    cargarDatos(filtroDesde || undefined, filtroHasta || undefined);
  };

  const limpiarFiltros = () => {
    setFiltroDesde('');
    setFiltroHasta('');
    setPagina(0);
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

  const totalPaginas = useMemo(() => Math.max(1, Math.ceil(ips.length / ITEMS_POR_PAGINA)), [ips.length]);
  const inicio = pagina * ITEMS_POR_PAGINA;
  const ipsPagina = useMemo(() => ips.slice(inicio, inicio + ITEMS_POR_PAGINA), [ips, inicio]);
  const totalSospechosas = useMemo(() => ips.filter((i) => i.esSospechosa).length, [ips]);

  const metricas = useMemo(() => ({
    totalEventos: ips.reduce((s, i) => s + (i.totalEventos || 0), 0),
    ipsAltaActividad: ips.filter((i) => i.nivelIntensidad >= 7).length,
    ipsCriticas: ips.filter((i) => i.nivelIntensidad === 10).length,
    totalUsuarios: ips.reduce((s, i) => s + (i.totalUsuariosDistintos || 0), 0),
    promedioEventos: ips.length > 0
      ? Math.round((ips.reduce((s, i) => s + (i.totalEventos || 0), 0) / ips.length) * 10) / 10
      : 0,
  }), [ips]);

  useEffect(() => {
    if (onMetricasUpdate) {
      onMetricasUpdate(metricas);
    }
  }, [metricas, onMetricasUpdate]);

  const irPagina = (n) => {
    if (n >= 0 && n < totalPaginas) {
      setPagina(n);
      setIpExpandida(null);
    }
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
            {totalSospechosas} sospechosa{totalSospechosas !== 1 ? 's' : ''}
          </span>
        </div>
        <div className="mapa-calor-vistas">
          <button
            className={`vista-btn ${vista === 'tabla' ? 'vista-activa' : ''}`}
            onClick={() => setVista('tabla')}
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="3" width="18" height="18" rx="2" />
              <line x1="3" y1="9" x2="21" y2="9" />
              <line x1="9" y1="3" x2="9" y2="21" />
            </svg>
            Tabla
          </button>
          <button
            className={`vista-btn ${vista === 'grafico' ? 'vista-activa' : ''}`}
            onClick={() => setVista('grafico')}
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="18" y1="20" x2="18" y2="10" />
              <line x1="12" y1="20" x2="12" y2="4" />
              <line x1="6" y1="20" x2="6" y2="14" />
            </svg>
            Gráfico
          </button>
        </div>
        <div className="mapa-calor-filtros">
          <div className="filtro-fecha">
            <label>Desde</label>
            <input
              type="date"
              value={filtroDesde}
              onChange={(e) => setFiltroDesde(e.target.value)}
            />
          </div>
          <div className="filtro-fecha">
            <label>Hasta</label>
            <input
              type="date"
              value={filtroHasta}
              onChange={(e) => setFiltroHasta(e.target.value)}
            />
          </div>
          <button onClick={handleFiltrar} className="btn-filtrar">
            Filtrar
          </button>
          <button onClick={limpiarFiltros} className="btn-limpiar">
            Limpiar
          </button>
        </div>
      </div>

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
          {vista === 'tabla' ? (
            <div className="mapa-calor-table-wrapper">
              <table className="mapa-calor-table">
                <thead>
                  <tr>
                    <th>IP</th>
                    <th>Eventos</th>
                    <th>Usuarios Distintos</th>
                    <th>Primera Vez</th>
                    <th>Última Vez</th>
                    <th>Nivel de Actividad</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {ipsPagina.map((ip) => (
                  <tr
                    key={ip.ipOrigen}
                    className={`mapa-fila ${ipExpandida === ip.ipOrigen ? 'expandida' : ''}`}
                    style={{ backgroundColor: getColorPorNivel(ip.nivelIntensidad) }}
                    onClick={() => toggleDetalle(ip.ipOrigen)}
                  >
                    <td className="ip-cell">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="ip-icon">
                        <rect x="2" y="2" width="20" height="20" rx="3" />
                        <line x1="2" y1="10" x2="22" y2="10" />
                        <line x1="10" y1="2" x2="10" y2="22" />
                      </svg>
                      {ip.ipOrigen}
                    </td>
                    <td className="num-cell">{ip.totalEventos}</td>
                    <td className="num-cell">{ip.totalUsuariosDistintos}</td>
                    <td>{formatearFecha(ip.primeraVez)}</td>
                    <td>{formatearFecha(ip.ultimaVez)}</td>
                    <td>
                      <div className="intensity-bar-container">
                        <div
                          className="intensity-bar"
                          style={{
                            width: `${ip.nivelIntensidad * 10}%`,
                            backgroundColor: getColorBarraPorNivel(ip.nivelIntensidad),
                          }}
                        ></div>
                        <span className="intensity-label">{ip.nivelIntensidad}/10</span>
                      </div>
                    </td>
                    <td>
                      {ip.esSospechosa ? (
                        <span className="badge-sospechosa">⚠ Sospechosa</span>
                      ) : (
                        <span className="badge-normal">✓ Normal</span>
                      )}
                    </td>
                  </tr>
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
                          <td>{formatearFecha(evt.fecha)}</td>
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
