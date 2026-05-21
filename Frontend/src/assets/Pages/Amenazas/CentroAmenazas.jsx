import { useEffect, useRef } from 'react';
import ThreatAlertPanel from '../../Components/Observer/ThreatAlertPanel';
import ThreatMetricsCards from '../../Components/Observer/ThreatMetricsCards';
import threatEventBus from '../../Components/Observer/ThreatEventBus';
import { conectarStreamAmenazas, obtenerAmenazasActivas } from '../../Services/amenazas/threatService';
import './CentroAmenazas.css';

const CentroAmenazas = () => {
  const eventSourceRef = useRef(null);

  useEffect(() => {
    const cargarIniciales = async () => {
      const result = await obtenerAmenazasActivas();
      if (result.success && Array.isArray(result.data)) {
        for (const threat of result.data) {
          threatEventBus.notify(threat);
        }
      }
    };
    cargarIniciales();

    eventSourceRef.current = conectarStreamAmenazas((threat) => {
      threatEventBus.notify(threat);
    });

    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }
    };
  }, []);

  return (
    <div className="centro-amenazas">
      <div className="centro-amenazas-header">
        <div className="centro-amenazas-title">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#ef4444" strokeWidth="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z" />
            <path d="M2 17l10 5 10-5" />
            <path d="M2 12l10 5 10-5" />
          </svg>
          <h2>Centro de Detección de Amenazas</h2>
        </div>
        <p className="centro-amenazas-subtitle">
          Monitoreo en vivo de actividad sospechosa en el sistema
        </p>
      </div>
      <ThreatMetricsCards />
      <ThreatAlertPanel />
    </div>
  );
};

export default CentroAmenazas;
