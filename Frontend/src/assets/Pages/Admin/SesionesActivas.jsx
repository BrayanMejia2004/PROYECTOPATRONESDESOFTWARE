import { useState, useEffect, useCallback, useMemo } from 'react';
import { obtenerSesionesActivas, revocarSesion, obtenerMetricas } from '../../Services/sesiones/sesionesService';
import { TiempoGard, AvatarGard, IpGard, RiesgoGard } from '../../Components/Gards/SesionGard';
import { AreaChart, Area, BarChart, Bar, XAxis, YAxis, ResponsiveContainer, Tooltip, CartesianGrid } from 'recharts';
import { generarTendenciaDiaria, generarDistribucionDuracion, generarSesionesPorHora } from '../../Utils/datosSimulados';
import './SesionesActivas.css';

const SesionesActivas = () => {
  const [sesiones, setSesiones] = useState([]);
  const [metricas, setMetricas] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [contador, setContador] = useState(15);
  const [confirmModal, setConfirmModal] = useState({ show: false, id: null, username: '' });

  const tendencia = useMemo(() => generarTendenciaDiaria(7, 8, 4), []);
  const distribucion = useMemo(() => generarDistribucionDuracion(), []);
  const sesionesPorHora = useMemo(() => generarSesionesPorHora(), []);

  const cadena = useMemo(() => {
    const tiempo = new TiempoGard();
    tiempo.setNext(new AvatarGard()).setNext(new IpGard()).setNext(new RiesgoGard());
    return tiempo;
  }, []);

  const cargarDatos = useCallback(async () => {
    const [sesionesRes, metricasRes] = await Promise.all([
      obtenerSesionesActivas(),
      obtenerMetricas(),
    ]);
    if (sesionesRes.success) {
      setSesiones(sesionesRes.data.map(s => cadena.procesar(s)));
      setError(null);
    } else {
      setError(sesionesRes.message || 'Error al cargar sesiones');
    }
    if (metricasRes.success) {
      setMetricas(metricasRes.data);
    }
    setLoading(false);
  }, [cadena]);

  useEffect(() => {
    cargarDatos();
    const interval = setInterval(() => {
      cargarDatos();
      setContador(15);
    }, 15000);
    const tick = setInterval(() => {
      setContador((c) => Math.max(0, c - 1));
    }, 1000);
    return () => {
      clearInterval(interval);
      clearInterval(tick);
    };
  }, [cargarDatos]);

  const handleRevocar = async () => {
    const { id } = confirmModal;
    setConfirmModal({ show: false, id: null, username: '' });
    const result = await revocarSesion(id);
    if (result.success) {
      cargarDatos();
    } else {
      alert(result.message || 'Error al revocar sesión');
    }
  };

  if (loading) {
    return (
      <div className="dashboard">
        <main className="dashboard-main">
          <div className="loading-container">
            <div className="spinner"></div>
            <p>Cargando sesiones activas...</p>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <main className="dashboard-main">
        <div className="sesiones-header">
          <div>
            <h1 className="sesiones-title">Sesiones Activas</h1>
            <p className="sesiones-subtitle">
              {sesiones.length} sesión{sesiones.length !== 1 ? 'es' : ''} activa{sesiones.length !== 1 ? 's' : ''} en el sistema
            </p>
          </div>
          <div className="sesiones-refresh">
            <span className="refresh-indicator" />
            <span className="refresh-text">Actualizando en {contador}s</span>
            <button className="refresh-btn" onClick={() => { cargarDatos(); setContador(15); }}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="23 4 23 10 17 10" />
                <path d="M20.49 15a9 9 0 11-2.12-9.36L23 10" />
              </svg>
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

        {metricas && (
          <div className="metricas-grid">
            <div className="metrica-card">
              <div className="metrica-icon metrica-icon-red">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10" />
                  <line x1="15" y1="9" x2="9" y2="15" />
                  <line x1="9" y1="9" x2="15" y2="15" />
                </svg>
              </div>
              <div className="metrica-info">
                <span className="metrica-value">{metricas.revocacionesHoy}</span>
                <span className="metrica-label">Revoc. Hoy</span>
              </div>
              <div className="metrica-sparkline">
                <ResponsiveContainer width="100%" height={40}>
                  <AreaChart data={tendencia}>
                    <defs>
                      <linearGradient id="sparkRed" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#f4212e" stopOpacity={0.3} />
                        <stop offset="100%" stopColor="#f4212e" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <Area type="monotone" dataKey="valor" stroke="#f4212e" fill="url(#sparkRed)" strokeWidth={1.5} dot={false} />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>
            <div className="metrica-card">
              <div className="metrica-icon metrica-icon-orange">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
                </svg>
              </div>
              <div className="metrica-info">
                <span className="metrica-value">{metricas.revocacionesSemana}</span>
                <span className="metrica-label">Revoc. Semana</span>
              </div>
              <div className="metrica-sparkline">
                <ResponsiveContainer width="100%" height={40}>
                  <AreaChart data={tendencia}>
                    <defs>
                      <linearGradient id="sparkOrange" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#ff9500" stopOpacity={0.3} />
                        <stop offset="100%" stopColor="#ff9500" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <Area type="monotone" dataKey="valor" stroke="#ff9500" fill="url(#sparkOrange)" strokeWidth={1.5} dot={false} />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>
            <div className="metrica-card">
              <div className="metrica-icon metrica-icon-purple">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                </svg>
              </div>
              <div className="metrica-info">
                <span className="metrica-value">{metricas.revocacionesTotales}</span>
                <span className="metrica-label">Revoc. Totales</span>
              </div>
              <div className="metrica-sparkline">
                <ResponsiveContainer width="100%" height={40}>
                  <AreaChart data={tendencia}>
                    <defs>
                      <linearGradient id="sparkPurple" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#af52de" stopOpacity={0.3} />
                        <stop offset="100%" stopColor="#af52de" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <Area type="monotone" dataKey="valor" stroke="#af52de" fill="url(#sparkPurple)" strokeWidth={1.5} dot={false} />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>
            <div className="metrica-card">
              <div className="metrica-icon metrica-icon-blue">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M16 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                  <circle cx="8.5" cy="7" r="4" />
                  <polyline points="17 11 19 13 23 9" />
                </svg>
              </div>
              <div className="metrica-info">
                <span className="metrica-value">{metricas.sesionesHoy}</span>
                <span className="metrica-label">Sesiones Hoy</span>
              </div>
              <div className="metrica-sparkline">
                <ResponsiveContainer width="100%" height={40}>
                  <AreaChart data={tendencia}>
                    <defs>
                      <linearGradient id="sparkBlue" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#1d9bf0" stopOpacity={0.3} />
                        <stop offset="100%" stopColor="#1d9bf0" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <Area type="monotone" dataKey="valor" stroke="#1d9bf0" fill="url(#sparkBlue)" strokeWidth={1.5} dot={false} />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        )}

        <div className="sesiones-charts">
          <div className="sesion-chart-panel">
            <div className="chart-panel-header">
              <h3>Distribución de Duración</h3>
            </div>
            <div className="chart-panel-body">
              <ResponsiveContainer width="100%" height={180}>
                <BarChart data={distribucion} layout="vertical" margin={{ top: 0, right: 20, left: 0, bottom: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(231,233,234,0.06)" horizontal={false} />
                  <XAxis type="number" tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} />
                  <YAxis type="category" dataKey="rango" tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} width={90} />
                  <Tooltip contentStyle={{ background: '#1a2332', border: '1px solid rgba(231,233,234,0.12)', borderRadius: 8, fontSize: 12 }}
                    formatter={(value) => [`${value} sesiones`, 'Cantidad']} />
                  <Bar dataKey="cantidad" fill="#d4a853" radius={[0, 4, 4, 0]} barSize={18} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
          <div className="sesion-chart-panel">
            <div className="chart-panel-header">
              <h3>Sesiones por Hora</h3>
            </div>
            <div className="chart-panel-body">
              <ResponsiveContainer width="100%" height={180}>
                <AreaChart data={sesionesPorHora} margin={{ top: 5, right: 5, left: -20, bottom: 0 }}>
                  <defs>
                    <linearGradient id="horasGrad" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%" stopColor="#d4a853" stopOpacity={0.3} />
                      <stop offset="100%" stopColor="#d4a853" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(231,233,234,0.06)" />
                  <XAxis dataKey="hora" tick={{ fill: '#8899a6', fontSize: 10 }} tickLine={false} interval={3} />
                  <YAxis tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} />
                  <Tooltip contentStyle={{ background: '#1a2332', border: '1px solid rgba(231,233,234,0.12)', borderRadius: 8, fontSize: 12 }}
                    formatter={(value) => [`${value} sesiones`, 'Activas']} />
                  <Area type="monotone" dataKey="sesiones" stroke="#d4a853" fill="url(#horasGrad)" strokeWidth={2} dot={false} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        <div className="sesiones-panel">
          <table className="sesiones-table">
            <thead>
              <tr>
                <th>Usuario</th>
                <th>IP de Origen</th>
                <th>Inició</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {sesiones.length === 0 ? (
                <tr>
                  <td colSpan="4" className="empty-message">No hay sesiones activas</td>
                </tr>
              ) : (
                  sesiones.map((s) => (
                  <tr key={s.id}>
                    <td>
                      <div className="sesion-user">
                        <div className="sesion-avatar" style={{ backgroundColor: s.colorRiesgo + '20', color: s.colorRiesgo }}>
                          {s.avatarLetra}
                        </div>
                        <span>{s.username}</span>
                      </div>
                    </td>
                    <td><code className="ip-code">{s.ipDisplay}</code></td>
                    <td>
                      <span style={{ color: s.colorRiesgo }}>{s.tiempoFormateado}</span>
                    </td>
                    <td>
                      <button
                        className="btn-revocar"
                        onClick={() => setConfirmModal({ show: true, id: s.id, username: s.username })}
                      >
                        Revocar
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {confirmModal.show && (
          <div className="modal-overlay" onClick={() => setConfirmModal({ show: false, id: null, username: '' })}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
              <div className="modal-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10" />
                  <line x1="12" y1="8" x2="12" y2="12" />
                  <line x1="12" y1="16" x2="12.01" y2="16" />
                </svg>
              </div>
              <h3 className="modal-title">Revocar sesión</h3>
              <p className="modal-text">
                Se cerrará la sesión de <strong>"{confirmModal.username}"</strong>. El usuario deberá iniciar sesión nuevamente.
              </p>
              <div className="modal-actions">
                <button className="modal-btn-cancel" onClick={() => setConfirmModal({ show: false, id: null, username: '' })}>
                  Cancelar
                </button>
                <button className="modal-btn-confirm" onClick={handleRevocar}>
                  Revocar
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default SesionesActivas;
