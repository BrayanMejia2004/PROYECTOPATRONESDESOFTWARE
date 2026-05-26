import { useState, useEffect } from 'react';
import { BarChart, Bar, Cell, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { obtenerEstadisticas } from '../../Services/dashboard/estadisticasService';
import { generarMatrizSemanal, generarAnomalias, generarDatosBurbuja } from '../../Utils/datosSimulados';
import PlantillaSeccion from './PlantillaSeccion';
import MapaCircularHeatmap from './MapaCircularHeatmap';
import TimelineAnomalias from './TimelineAnomalias';
import EcosistemaBurbujas from './EcosistemaBurbujas';
import './EstadisticasPanel.css';

const COLORES_GRAFICO = ['#d4a853', '#f5c26b', '#00ba7c', '#f4212e', '#1d9bf0', '#8899a6', '#e0245e', '#794bc4'];
const PERIODOS = [
  { key: 7, label: '7d' },
  { key: 30, label: '30d' },
  { key: 90, label: '90d' },
  { key: 365, label: '1a' },
];

const CustomTooltipBar = ({ active, payload, label }) => {
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
  const [periodo, setPeriodo] = useState(30);

  const cargarEstadisticas = async () => {
    setLoading(true);
    setError(null);
    const result = await obtenerEstadisticas();
    if (result.success) {
      const factor = periodo / 30;
      setEstadisticas({
        ...result.data,
        _matrizSemanal: generarMatrizSemanal(7, Math.round(8 * factor)),
        _anomalias: generarAnomalias(periodo),
        _burbujas: generarDatosBurbuja(periodo),
      });
    } else {
      setError(result.message || 'Error al cargar estadísticas');
    }
    setLoading(false);
  };

  useEffect(() => {
    cargarEstadisticas();
  }, []);

  useEffect(() => {
    if (estadisticas) {
      const factor = periodo / 30;
      setEstadisticas(prev => ({
        ...prev,
        _matrizSemanal: generarMatrizSemanal(7, Math.round(8 * factor)),
        _anomalias: generarAnomalias(periodo),
        _burbujas: generarDatosBurbuja(periodo),
      }));
    }
  }, [periodo]);

  const datosCircular = estadisticas?._matrizSemanal || [];
  const datosAnomalias = estadisticas?._anomalias || null;
  const datosBurbujas = estadisticas?._burbujas || [];

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

  return (
    <div className="estadisticas-panel">
      <div className="estadisticas-panel-header">
        <h2>Panel de Estadísticas</h2>
        <div className="period-pills">
          {PERIODOS.map(p => (
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
      <section className="estadisticas-section">
        <div className="estadisticas-grid">
          <PlantillaSeccion
            titulo="Mapa de Calor Circular 24h × 7d"
            datos={{ datosCircular }}
            extractor={d => d.datosCircular}
            render={data => <MapaCircularHeatmap data={data} />}
          />

          <PlantillaSeccion
            titulo="Actividad por Hora"
            datos={estadisticas}
            extractor={d => d.actividadPorHora || {}}
            transformador={raw => Array.from({ length: 24 }, (_, i) => ({
              hora: `${String(i).padStart(2, '0')}:00`,
              eventos: raw[i] || 0,
            }))}
            render={data => (
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={data}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(231,233,234,0.06)" />
                  <XAxis
                    dataKey="hora"
                    tick={{ fill: '#8899a6', fontSize: 11 }}
                    tickLine={false}
                    interval={2}
                  />
                  <YAxis tick={{ fill: '#8899a6', fontSize: 11 }} tickLine={false} />
                  <Tooltip content={<CustomTooltipBar />} />
                  <Bar dataKey="eventos" radius={[3, 3, 0, 0]}>
                    {data.map((_, idx) => (
                      <Cell key={idx} fill={COLORES_GRAFICO[idx % COLORES_GRAFICO.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            )}
          />

          <PlantillaSeccion
            titulo="Timeline de Anomalías y Predicción"
            badge={datosAnomalias?.totalAnomalias}
            datos={{ datosAnomalias }}
            extractor={d => d.datosAnomalias}
            render={data => <TimelineAnomalias data={data} />}
          />

          <PlantillaSeccion
            titulo="Ecosistema de Roles"
            datos={{ datosBurbujas }}
            extractor={d => d.datosBurbujas}
            render={data => <EcosistemaBurbujas data={data} />}
          />
        </div>
      </section>
    </div>
  );
};

export default EstadisticasPanel;
