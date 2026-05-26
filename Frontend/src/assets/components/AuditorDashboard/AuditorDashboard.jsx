import { useState, useEffect, useRef, useMemo, useCallback } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { obtenerDashboard } from '../../Services/dashboard/auditorDashboardService';
import { DashboardOriginator, dashboardCaretaker } from './DashboardMemento';
import { generarTendenciaDiaria, generarActividadMensualPorAnio } from '../../Utils/datosSimulados';
import './AuditorDashboard.css';

const originator = new DashboardOriginator();

const PERIODOS = [
  { key: 7, label: '7d' },
  { key: 30, label: '30d' },
  { key: 90, label: '90d' },
  { key: 365, label: '1a' },
];

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

const VariacionBadge = ({ valor }) => {
  if (valor === 0) return <span className="variacion-badge variacion-flat">→ 0%</span>;
  if (valor > 0) return <span className="variacion-badge variacion-up">↑ {valor}%</span>;
  return <span className="variacion-badge variacion-down">↓ {Math.abs(valor)}%</span>;
};

const AuditorDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [ultimaAct, setUltimaAct] = useState(null);
  const [refreshing, setRefreshing] = useState(false);
  const [periodo, setPeriodo] = useState(365);
  const [anioComparativa, setAnioComparativa] = useState(new Date().getFullYear());
  const mountedRef = useRef(true);

  const tendenciaPeriodo = useMemo(() => generarTendenciaDiaria(periodo, 25, 10), [periodo]);

  const tendenciaCorta = useMemo(() => generarTendenciaDiaria(7, 15, 6), []);

  const datosMensuales = useMemo(() => {
    const r = generarActividadMensualPorAnio(anioComparativa, 250);
    return r;
  }, [anioComparativa]);

  const variacionHoy = useMemo(() => {
    const t = tendenciaCorta;
    if (t.length < 2) return 0;
    const hoy = t[t.length - 1]?.valor || 0;
    const ayer = t[t.length - 2]?.valor || 0;
    if (ayer === 0) return hoy > 0 ? 100 : 0;
    return Math.round(((hoy - ayer) / ayer) * 100);
  }, [tendenciaCorta]);

  const variacionSemana = useMemo(() => {
    const t = tendenciaCorta;
    if (t.length < 7) return 0;
    const suma7 = t.slice(-7).reduce((s, d) => s + d.valor, 0);
    const suma7Ant = t.slice(-14, -7).reduce((s, d) => s + d.valor, 0);
    if (suma7Ant === 0) return suma7 > 0 ? 100 : 0;
    return Math.round(((suma7 - suma7Ant) / suma7Ant) * 100);
  }, [tendenciaCorta]);

  const variacionMes = useMemo(() => {
    const actual = datosMensuales.totalActual;
    const anterior = datosMensuales.totalAnterior;
    if (anterior === 0) return actual > 0 ? 100 : 0;
    return Math.round(((actual - anterior) / anterior) * 100);
  }, [datosMensuales]);

  const cargarDashboard = useCallback(async (silencioso = false) => {
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
  }, []);

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
  }, [cargarDashboard]);

  const chartData = tendenciaPeriodo.map((d) => ({
    fecha: new Date(d.fecha).toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit' }),
    eventos: d.valor,
  }));

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
            <div className="card-variacion"><VariacionBadge valor={variacionHoy} /></div>
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
            <div className="card-variacion"><VariacionBadge valor={variacionSemana} /></div>
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
            <div className="card-variacion"><VariacionBadge valor={variacionMes} /></div>
          </div>
        </div>

        <div className="auditor-panel">
          <div className="panel-header">
            <h3>Actividad — Últimos {periodo} días</h3>
            <div className="period-pills">
              {PERIODOS.map((p) => (
                <button
                  key={p.key}
                  className={`period-pill ${periodo === p.key ? 'period-pill-active' : ''}`}
                  onClick={() => setPeriodo(p.key)}
                >
                  {p.label}
                </button>
              ))}
            </div>
          </div>
          <div className="panel-body">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(231,233,234,0.06)" />
                <XAxis dataKey="fecha" tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} interval={Math.max(0, Math.floor(chartData.length / 10) - 1)} />
                <YAxis tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} />
                <Tooltip content={<CustomTooltip />} />
                <Bar dataKey="eventos" fill="#d4a853" radius={[3, 3, 0, 0]} name="Eventos" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="auditor-panel">
          <div className="panel-header">
            <h3>Comparativa Mensual</h3>
            <div className="year-nav">
              <button className="year-nav-btn" onClick={() => setAnioComparativa((a) => a - 1)} disabled={anioComparativa <= 2020}>◀</button>
              <span className="year-nav-label">{anioComparativa}</span>
              <button className="year-nav-btn" onClick={() => setAnioComparativa((a) => a + 1)} disabled={anioComparativa >= 2027}>▶</button>
            </div>
          </div>
          <div className="panel-body">
            <div className="year-totals">
              <span className="year-total-item year-total-prev">{anioComparativa - 1}: {datosMensuales.totalAnterior.toLocaleString()} eventos</span>
              <span className="year-total-item year-total-curr">{anioComparativa}: {datosMensuales.totalActual.toLocaleString()} eventos</span>
            </div>
            <ResponsiveContainer width="100%" height={220}>
              <BarChart data={datosMensuales.meses}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(231,233,234,0.06)" />
                <XAxis dataKey="mes" tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} />
                <YAxis tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} />
                <Tooltip content={<CustomTooltip />} />
                <Bar dataKey="anterior" fill="rgba(29,155,240,0.35)" radius={[3, 3, 0, 0]} name={`${anioComparativa - 1}`} />
                <Bar dataKey="actual" fill="#d4a853" radius={[3, 3, 0, 0]} name={`${anioComparativa}`} />
              </BarChart>
            </ResponsiveContainer>
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
