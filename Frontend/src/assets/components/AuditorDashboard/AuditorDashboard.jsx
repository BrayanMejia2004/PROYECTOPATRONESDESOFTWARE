import { useState, useEffect, useRef } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { obtenerDashboard } from '../../Services/dashboard/auditorDashboardService';
import { DashboardOriginator, dashboardCaretaker } from './DashboardMemento';
import './AuditorDashboard.css';

const originator = new DashboardOriginator();

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="auditor-tooltip">
        <p className="auditor-tooltip-label">{label}</p>
        {payload.map((entry, idx) => (
          <p key={idx} style={{ color: entry.color }}>
            {entry.name}: <strong>{entry.value}</strong>
          </p>
        ))}
      </div>
    );
  }
  return null;
};

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  const date = new Date(fecha);
  return date.toLocaleString('es-ES');
};

const AuditorDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [ultimaAct, setUltimaAct] = useState(null);
  const [refreshing, setRefreshing] = useState(false);
  const mountedRef = useRef(true);

  const cargarDashboard = async (silencioso = false) => {
    if (!silencioso) setLoading(true);
    else setRefreshing(true);
    setError(null);
    const result = await obtenerDashboard();
    if (!mountedRef.current) return;
    if (result.success) {
      setData(result.data);
      const ahora = Date.now();
      setUltimaAct(ahora);
      dashboardCaretaker.guardar(originator.guardar(result.data));
    } else {
      if (!silencioso) setError(result.message || 'Error al cargar dashboard');
    }
    if (!silencioso) setLoading(false);
    else setRefreshing(false);
  };

  useEffect(() => {
    mountedRef.current = true;
    const memento = dashboardCaretaker.recuperar();
    if (memento) {
      const data = originator.restaurar(memento);
      if (data) {
        setData(data);
        setUltimaAct(memento.getTimestamp());
        if (dashboardCaretaker.hayMementoValido(30)) {
          setLoading(false);
        } else {
          setLoading(false);
          cargarDashboard(true);
        }
        return;
      }
    }
    cargarDashboard();
    return () => { mountedRef.current = false; };
  }, []);

  if (loading) {
    return (
      <div className="dashboard">
        <main className="dashboard-main">
          <div className="loading-container">
            <div className="spinner"></div>
            <p>Cargando dashboard...</p>
          </div>
        </main>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard">
        <main className="dashboard-main">
          <div className="alert alert-error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="15" y1="9" x2="9" y2="15" />
              <line x1="9" y1="9" x2="15" y2="15" />
            </svg>
            {error}
          </div>
          <div className="alert-actions">
            <button className="btn-retry" onClick={() => cargarDashboard()}>
              Reintentar
            </button>
          </div>
        </main>
      </div>
    );
  }

  const ultimosSeguridad = data?.ultimosEventosSeguridad || [];
  const topIps = data?.topIpsHoy || [];

  const datosBar = (data?.actividadDiaria || []).map((d) => ({
    fecha: d.fecha ? new Date(d.fecha).toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit' }) : '-',
    eventos: d.total,
  }));

  return (
    <div className="dashboard">
      <main className="dashboard-main">
        <div className="auditor-header">
          <div className="auditor-title">
            <h1>Dashboard de Auditoría</h1>
            <p>Resumen de actividad y eventos del sistema</p>
          </div>
          <div className="auditor-header-actions">
            {ultimaAct && (
              <span className="ultima-act">
                Última actualización: {formatearFecha(ultimaAct)}
              </span>
            )}
            <button
              className="btn-refresh"
              onClick={() => cargarDashboard(true)}
              disabled={refreshing}
            >
              <svg className={`refresh-icon ${refreshing ? 'spin' : ''}`} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16">
                <polyline points="23 4 23 10 17 10" />
                <polyline points="1 20 1 14 7 14" />
                <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" />
              </svg>
              {refreshing ? 'Actualizando...' : 'Actualizar'}
            </button>
          </div>
        </div>

        <div className="auditor-cards">
          <div className="auditor-card">
            <div className="card-icon card-icon-gold">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
              </svg>
            </div>
            <div className="card-info">
              <span className="card-value">{data?.eventosHoy || 0}</span>
              <span className="card-label">Hoy</span>
            </div>
          </div>
          <div className="auditor-card">
            <div className="card-icon card-icon-blue">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
              </svg>
            </div>
            <div className="card-info">
              <span className="card-value">{data?.eventoSemana || 0}</span>
              <span className="card-label">Esta Semana</span>
            </div>
          </div>
          <div className="auditor-card">
            <div className="card-icon card-icon-green">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
              </svg>
            </div>
            <div className="card-info">
              <span className="card-value">{data?.eventosMes || 0}</span>
              <span className="card-label">Este Mes</span>
            </div>
          </div>
        </div>

        <div className="auditor-grid">
          <div className="auditor-panel">
            <div className="panel-header">
              <h3>Actividad Diaria (30 días)</h3>
            </div>
            <div className="panel-body">
              <ResponsiveContainer width="100%" height={250}>
                <BarChart data={datosBar}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(231,233,234,0.06)" />
                  <XAxis dataKey="fecha" tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} interval={4} />
                  <YAxis tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} />
                  <Tooltip content={<CustomTooltip />} />
                  <Bar dataKey="eventos" fill="#d4a853" radius={[3, 3, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="auditor-panel">
            <div className="panel-header">
              <h3>Top IPs Hoy</h3>
            </div>
            <div className="panel-body">
              <table className="auditor-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Dirección IP</th>
                    <th>Eventos</th>
                  </tr>
                </thead>
                <tbody>
                  {topIps.length === 0 ? (
                    <tr>
                      <td colSpan="3" className="empty-message">Sin actividad hoy</td>
                    </tr>
                  ) : (
                    topIps.map((item, idx) => (
                      <tr key={item.ip}>
                        <td className="rank-cell">{idx + 1}</td>
                        <td><code className="ip-code">{item.ip}</code></td>
                        <td className="value-cell">{item.total}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div className="auditor-panel">
          <div className="panel-header">
            <h3>Últimos Eventos de Seguridad</h3>
          </div>
          <div className="panel-body">
            <table className="auditor-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Usuario ID</th>
                  <th>Acción</th>
                  <th>Descripción</th>
                  <th>Fecha</th>
                  <th>IP</th>
                </tr>
              </thead>
              <tbody>
                {ultimosSeguridad.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="empty-message">No hay eventos de seguridad</td>
                  </tr>
                ) : (
                  ultimosSeguridad.map((e) => (
                    <tr key={e.id}>
                      <td>{e.id}</td>
                      <td>{e.usuario_id}</td>
                      <td>
                        <span className="accion-badge">{e.accion}</span>
                      </td>
                      <td>{e.descripcion || '-'}</td>
                      <td>{formatearFecha(e.fecha)}</td>
                      <td><code className="ip-code">{e.ip_origen || '-'}</code></td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>
  );
};

export default AuditorDashboard;
