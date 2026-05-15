import { useState, useEffect, useRef } from 'react';
import threatEventBus from './ThreatEventBus';
import './ObserverStyles.css';

const ThreatMetricsCards = () => {
  const [metrics, setMetrics] = useState({
    activas: 0,
    criticas: 0,
    ipsBloqueadas: 0,
    total: 0,
  });

  const observerRef = useRef({
    update: () => {
      const all = threatEventBus.getAllThreats();
      const active = all.filter(t => t.activa !== false);
      const ips = new Set(active.map(t => t.ipOrigen).filter(Boolean));
      setMetrics({
        activas: active.length,
        criticas: active.filter(t => t.severidad === 'CRITICA').length,
        ipsBloqueadas: ips.size,
        total: all.length,
      });
    },
  });

  useEffect(() => {
    observerRef.current.update();
    threatEventBus.attach(observerRef.current);
    return () => threatEventBus.detach(observerRef.current);
  }, []);

  const cards = [
    { label: 'Amenazas Activas', value: metrics.activas, color: '#f97316' },
    { label: 'Críticas', value: metrics.criticas, color: '#ef4444' },
    { label: 'IPs Involucradas', value: metrics.ipsBloqueadas, color: '#8b5cf6' },
    { label: 'Total Detectadas', value: metrics.total, color: '#3b82f6' },
  ];

  return (
    <div className="threat-metrics">
      {cards.map(card => (
        <div key={card.label} className="threat-metric-card" style={{ borderTopColor: card.color }}>
          <span className="threat-metric-value" style={{ color: card.color }}>
            {card.value}
          </span>
          <span className="threat-metric-label">{card.label}</span>
        </div>
      ))}
    </div>
  );
};

export default ThreatMetricsCards;
