import { useState, useEffect } from 'react';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { obtenerEstadisticas } from '../Services/estadisticasService';
import './EstadisticasPanel.css';

const COLORS_TIPO = { BASICA: '#d4a853', COMPLETA: '#00ba7c', SEGURIDAD: '#f4212e' };
const COLORES_GRAFICO = ['#d4a853', '#f5c26b', '#00ba7c', '#f4212e', '#1d9bf0', '#8899a6', '#e0245e', '#794bc4'];

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="estadisticas-tooltip">
        <p className="estadisticas-tooltip-label">{label}</p>
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

const EstadisticasPanel = () => {
  const [estadisticas, setEstadisticas] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    cargarEstadisticas();
  }, []);

  const cargarEstadisticas = async () => {
    setLoading(true);
    setError(null);
    const result = await obtenerEstadisticas();
    if (result.success) {
      setEstadisticas(result.data);
    } else {
      setError(result.message || 'Error al cargar estadísticas');
    }
    setLoading(false);
  };

  if (loading) {
    return (
      <div className="estadisticas-panel">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Cargando estadísticas...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="estadisticas-panel">
        <div className="alert alert-error">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="15" y1="9" x2="9" y2="15" />
            <line x1="9" y1="9" x2="15" y2="15" />
          </svg>
          {error}
        </div>
      </div>
    );
  }

  const eventosPorTipo = estadisticas?.eventosPorTipo || {};
  const actividadPorHora = estadisticas?.actividadPorHora || {};
  const top5UsuariosActivos = estadisticas?.top5UsuariosActivos || [];
  const usuariosSinActividad = estadisticas?.usuariosSinActividad || [];

  const datosPie = Object.entries(eventosPorTipo).map(([name, value]) => ({ name, value }));
  const datosBar = Array.from({ length: 24 }, (_, i) => ({
    hora: `${String(i).padStart(2, '0')}:00`,
    eventos: actividadPorHora[i] || 0,
  }));

  return (
    <div className="estadisticas-panel">
      <section className="estadisticas-section">
        <div className="estadisticas-grid">
          <div className="estadisticas-card">
            <div className="card-header">
              <h3>Eventos por Tipo</h3>
            </div>
            <div className="card-body">
              <ResponsiveContainer width="100%" height={260}>
                <PieChart>
                  <Pie
                    data={datosPie}
                    cx="50%"
                    cy="50%"
                    innerRadius={55}
                    outerRadius={100}
                    paddingAngle={4}
                    dataKey="value"
                    label={({ name, value }) => `${name}: ${value}`}
                  >
                    {datosPie.map((entry) => (
                      <Cell key={entry.name} fill={COLORS_TIPO[entry.name] || '#8899a6'} />
                    ))}
                  </Pie>
                  <Tooltip content={<CustomTooltip />} />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="estadisticas-card">
            <div className="card-header">
              <h3>Actividad por Hora</h3>
            </div>
            <div className="card-body">
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={datosBar}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(231,233,234,0.06)" />
                  <XAxis
                    dataKey="hora"
                    tick={{ fill: '#8899a6', fontSize: 11 }}
                    tickLine={false}
                    interval={2}
                  />
                  <YAxis tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} />
                  <Tooltip content={<CustomTooltip />} />
                  <Bar dataKey="eventos" radius={[3, 3, 0, 0]}>
                    {datosBar.map((_, idx) => (
                      <Cell key={idx} fill={COLORES_GRAFICO[idx % COLORES_GRAFICO.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="estadisticas-card">
            <div className="card-header">
              <h3>Top 5 Usuarios Más Activos</h3>
            </div>
            <div className="card-body">
              <table className="estadisticas-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Usuario</th>
                    <th>Total Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {top5UsuariosActivos.length === 0 ? (
                    <tr>
                      <td colSpan="3" className="empty-message">Sin datos</td>
                    </tr>
                  ) : (
                    top5UsuariosActivos.map((u, idx) => (
                      <tr key={u.usuarioId}>
                        <td className="rank-cell">{idx + 1}</td>
                        <td>{u.username || `ID: ${u.usuarioId}`}</td>
                        <td className="value-cell">{u.totalAcciones}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>

          <div className="estadisticas-card">
            <div className="card-header">
              <h3>Usuarios Sin Actividad</h3>
              {usuariosSinActividad.length > 0 && (
                <span className="badge-warning">{usuariosSinActividad.length}</span>
              )}
            </div>
            <div className="card-body">
              {usuariosSinActividad.length === 0 ? (
                <p className="empty-text">Todos los usuarios han tenido actividad</p>
              ) : (
                <div className="inactive-list">
                  {usuariosSinActividad.map((u) => (
                    <div key={u.id} className="inactive-item">
                      <div className="inactive-avatar">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <rect x="2" y="3" width="20" height="14" rx="2" />
                          <line x1="8" y1="21" x2="16" y2="21" />
                          <line x1="12" y1="17" x2="12" y2="21" />
                        </svg>
                      </div>
                      <div className="inactive-info">
                        <span className="inactive-username">{u.username}</span>
                        <span className="inactive-rol">{u.roles?.[0] || 'Sin rol'}</span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default EstadisticasPanel;
