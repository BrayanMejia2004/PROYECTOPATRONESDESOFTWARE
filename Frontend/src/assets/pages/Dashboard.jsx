import { useState } from 'react';
import { useAuth } from '../Context/AuthContext';
import MapaCalorIP from '../Components/MapaCalorIP';
import './Dashboard.css';

const formatearNumero = (n) => {
  if (n == null) return '0';
  return n.toLocaleString('es-ES');
};

const Dashboard = () => {
  const { isAdmin, isAuditor } = useAuth();
  const [metricas, setMetricas] = useState(null);

  return (
    <div className="dashboard">
      <main className="dashboard-main">
        {(isAdmin() || isAuditor()) && (
          <section className="dashboard-mapa-section">
            <div className="dashboard-mapa-header">
              <h1>Mapa de Calor por IP</h1>
              <p>Análisis de actividad agrupada por dirección IP de origen</p>
            </div>
            {metricas && (
              <div className="mapa-calor-metricas">
                <div className="metrica-card">
                  <div className="metrica-icono metrica-icono-eventos">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
                    </svg>
                  </div>
                  <div className="metrica-info">
                    <span className="metrica-valor">{formatearNumero(metricas.totalEventos)}</span>
                    <span className="metrica-etiqueta">Total Eventos</span>
                  </div>
                </div>
                <div className="metrica-card">
                  <div className="metrica-icono metrica-icono-alta">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M18 20V10M12 20V4M6 20v-6" />
                    </svg>
                  </div>
                  <div className="metrica-info">
                    <span className="metrica-valor">{metricas.ipsAltaActividad}</span>
                    <span className="metrica-etiqueta">IPs Nivel &ge; 7</span>
                  </div>
                </div>
                <div className="metrica-card">
                  <div className="metrica-icono metrica-icono-promedio">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <line x1="4" y1="20" x2="4" y2="14" />
                      <line x1="10" y1="20" x2="10" y2="8" />
                      <line x1="16" y1="20" x2="16" y2="2" />
                      <line x1="22" y1="20" x2="22" y2="12" />
                    </svg>
                  </div>
                  <div className="metrica-info">
                    <span className="metrica-valor">{metricas.promedioEventos}</span>
                    <span className="metrica-etiqueta">Prom. Eventos/IP</span>
                  </div>
                </div>
                <div className="metrica-card">
                  <div className="metrica-icono metrica-icono-usuarios">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                      <circle cx="9" cy="7" r="4" />
                      <path d="M23 21v-2a4 4 0 00-3-3.87" />
                      <path d="M16 3.13a4 4 0 010 7.75" />
                    </svg>
                  </div>
                  <div className="metrica-info">
                    <span className="metrica-valor">{formatearNumero(metricas.totalUsuarios)}</span>
                    <span className="metrica-etiqueta">Usuarios Únicos</span>
                  </div>
                </div>
              </div>
            )}
            <MapaCalorIP onMetricasUpdate={setMetricas} />
          </section>
        )}
      </main>
    </div>
  );
};

export default Dashboard;
