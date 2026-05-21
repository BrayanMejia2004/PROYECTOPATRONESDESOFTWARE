import { useState, useEffect, useRef } from 'react';
import threatEventBus from './ThreatEventBus';
import { resolverAmenaza } from '../../Services/amenazas/threatService';
import './ObserverStyles.css';

const SEVERIDAD_CONFIG = {
  CRITICA: { color: '#ef4444', bg: 'rgba(239,68,68,0.15)', icono: '🔴', label: 'Crítica' },
  ALTA: { color: '#f97316', bg: 'rgba(249,115,22,0.15)', icono: '🟠', label: 'Alta' },
  MEDIA: { color: '#f59e0b', bg: 'rgba(245,158,11,0.15)', icono: '🟡', label: 'Media' },
  BAJA: { color: '#3b82f6', bg: 'rgba(59,130,246,0.15)', icono: '🔵', label: 'Baja' },
};

const ThreatAlertPanel = () => {
  const [threats, setThreats] = useState([]);

  const observerRef = useRef({
    update: (threat) => {
      setThreats(prev => {
        if (prev.some(t => t.id === threat.id)) return prev;
        return [threat, ...prev];
      });
    },
  });

  useEffect(() => {
    const initial = threatEventBus.getActiveThreats();
    if (initial.length > 0) setThreats(initial);
    threatEventBus.attach(observerRef.current);
    return () => threatEventBus.detach(observerRef.current);
  }, []);

  const handleResolver = async (id) => {
    await resolverAmenaza(id);
    threatEventBus.clearResolved(id);
    setThreats(prev => prev.filter(t => t.id !== id));
  };

  if (threats.length === 0) {
    return (
      <div className="threat-panel-empty">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#22c55e" strokeWidth="1.5">
          <path d="M12 2L2 7l10 5 10-5-10-5z" />
          <path d="M2 17l10 5 10-5" />
          <path d="M2 12l10 5 10-5" />
        </svg>
        <p>No hay amenazas activas</p>
        <span>Sistema seguro</span>
      </div>
    );
  }

  return (
    <div className="threat-panel">
      <div className="threat-panel-header">
        <h3>Amenazas Activas</h3>
        <span className="threat-count">{threats.length}</span>
      </div>
      <div className="threat-list">
        {threats.map(threat => {
          const config = SEVERIDAD_CONFIG[threat.severidad] || SEVERIDAD_CONFIG.BAJA;
          return (
            <div
              key={threat.id}
              className="threat-card"
              style={{ borderLeftColor: config.color, background: config.bg }}
            >
              <div className="threat-card-header">
                <span className="threat-icon">{config.icono}</span>
                <span className="threat-severity" style={{ color: config.color }}>
                  {config.label}
                </span>
                <span className="threat-type">{threat.tipo?.replace(/_/g, ' ')}</span>
              </div>
              <p className="threat-desc">{threat.descripcion}</p>
              <div className="threat-meta">
                {threat.ipOrigen && <span>IP: {threat.ipOrigen}</span>}
                {threat.fecha && <span>{new Date(threat.fecha).toLocaleString()}</span>}
              </div>
              <button className="threat-resolve-btn" onClick={() => handleResolver(threat.id)}>
                Resolver
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default ThreatAlertPanel;
