import { useState, useEffect } from 'react';
import { obtenerMiActividad } from '../../Services/miActividadService';
import VistaEstadisticas from './VistaEstadisticas';
import VistaAlertas from './VistaAlertas';
import VistaRecomendaciones from './VistaRecomendaciones';
import './StateStyles.css';

const ESTADOS = {
  ESTADISTICAS: {
    getNombre: () => 'Mis Estadísticas',
    getAcciones: () => ['Ver métricas', 'Consultar IPs', 'Revisar eventos'],
    render: (datos) => <VistaEstadisticas datos={datos} />
  },
  ALERTAS: {
    getNombre: () => 'Mis Alertas',
    getAcciones: () => ['Activar alerta IP nueva', 'Activar resumen semanal'],
    render: () => <VistaAlertas />
  },
  RECOMENDACIONES: {
    getNombre: () => 'Recomendaciones',
    getAcciones: () => ['Revisar sugerencias', 'Mejorar seguridad'],
    render: (datos) => <VistaRecomendaciones datos={datos} />
  }
};

const MiActividadPanel = () => {
  const [estadoActual, setEstadoActual] = useState('ESTADISTICAS');
  const [datos, setDatos] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    setLoading(true);
    setError(null);
    const result = await obtenerMiActividad();
    if (result.success) {
      setDatos(result.data);
    } else {
      setError(result.message || 'Error al cargar datos');
    }
    setLoading(false);
  };

  const estado = ESTADOS[estadoActual];

  if (loading) {
    return (
      <div className="state-loading">
        <div className="state-spinner" />
        <span>Cargando tu actividad...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="state-error">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <circle cx="12" cy="12" r="10" />
          <line x1="15" y1="9" x2="9" y2="15" />
          <line x1="9" y1="9" x2="15" y2="15" />
        </svg>
        {error}
      </div>
    );
  }

  return (
    <div className="state-panel">
      <div className="state-panel-header">
        <div className="state-panel-title">
          <h2>Mi Actividad Digital</h2>
          {datos && (
            <span className="state-panel-username">@{datos.username}</span>
          )}
        </div>
        <button className="state-refresh-btn" onClick={cargarDatos} title="Actualizar datos">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16">
            <polyline points="23 4 23 10 17 10" />
            <path d="M20.49 15a9 9 0 11-2.12-9.36L23 10" />
          </svg>
          Actualizar
        </button>
      </div>

      <nav className="state-nav">
        {Object.entries(ESTADOS).map(([key, st]) => (
          <button
            key={key}
            className={`state-nav-btn ${estadoActual === key ? 'active' : ''}`}
            onClick={() => setEstadoActual(key)}
          >
            {st.getNombre()}
          </button>
        ))}
      </nav>

      <div className="state-content">
        {estado.render(datos)}
      </div>
    </div>
  );
};

export default MiActividadPanel;
