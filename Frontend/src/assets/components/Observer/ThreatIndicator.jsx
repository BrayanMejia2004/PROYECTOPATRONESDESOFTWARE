import { useState, useEffect, useRef } from 'react';
import threatEventBus from './ThreatEventBus';

const getColor = (count, maxSeveridad) => {
  if (count === 0) return '#6b7280';
  if (maxSeveridad === 'CRITICA') return '#ef4444';
  if (maxSeveridad === 'ALTA') return '#f97316';
  return '#f59e0b';
};

const ThreatIndicator = () => {
  const [count, setCount] = useState(0);
  const [maxSeveridad, setMaxSeveridad] = useState(null);

  const observerRef = useRef({
    update: (threat) => {
      setCount(prev => prev + 1);
      const niveles = { CRITICA: 4, ALTA: 3, MEDIA: 2, BAJA: 1 };
      setMaxSeveridad(prev => {
        if (!prev) return threat.severidad;
        return (niveles[threat.severidad] || 0) > (niveles[prev] || 0) ? threat.severidad : prev;
      });
    },
  });

  useEffect(() => {
    const active = threatEventBus.getActiveThreats();
    if (active.length > 0) {
      setCount(active.length);
      const niveles = { CRITICA: 4, ALTA: 3, MEDIA: 2, BAJA: 1 };
      let max = 'BAJA';
      for (const t of active) {
        if ((niveles[t.severidad] || 0) > (niveles[max] || 0)) max = t.severidad;
      }
      setMaxSeveridad(max);
    }
    threatEventBus.attach(observerRef.current);
    return () => threatEventBus.detach(observerRef.current);
  }, []);

  const color = getColor(count, maxSeveridad);
  const isPulsing = count > 0 && maxSeveridad === 'CRITICA';

  return (
    <div className="threat-indicator" title={`${count} amenaza(s) activa(s)`}>
      <div
        style={{
          width: '12px',
          height: '12px',
          borderRadius: '50%',
          backgroundColor: color,
          animation: isPulsing ? 'threat-pulse 1.5s ease-in-out infinite' : 'none',
          boxShadow: isPulsing ? `0 0 8px ${color}` : 'none',
          transition: 'all 0.3s ease',
        }}
      />
      {count > 0 && (
        <span
          style={{
            fontSize: '10px',
            fontWeight: 700,
            color: '#fff',
            backgroundColor: color,
            borderRadius: '50%',
            width: '16px',
            height: '16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            position: 'absolute',
            top: '-6px',
            right: '-6px',
          }}
        >
          {count > 9 ? '9+' : count}
        </span>
      )}
    </div>
  );
};

export default ThreatIndicator;
