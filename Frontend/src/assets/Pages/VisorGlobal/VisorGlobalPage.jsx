import { useRef, useState, useEffect, useCallback } from 'react';
import DashboardMediator from '../../Components/Mediator/DashboardMediator';
import MapaGlobal from '../../Components/Mediator/MapaGlobal';
import LineaTiempoFilterable from '../../Components/Mediator/LineaTiempoFilterable';
import FiltrosPanel from '../../Components/Mediator/FiltrosPanel';
import ContadoresGlobales from '../../Components/Mediator/ContadoresGlobales';
import ConexionWebSocket from '../../Components/Mediator/ConexionWebSocket';
import { obtenerEventosGlobales, obtenerResumenGlobal } from '../../Services/eventoGlobalService';
import './VisorGlobalPage.css';

const VisorGlobalPage = () => {
  const mediatorRef = useRef(new DashboardMediator());
  const mapaRef = useRef(null);
  const timelineRef = useRef(null);
  const contadoresRef = useRef(null);
  const [eventos, setEventos] = useState([]);
  const [resumen, setResumen] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    mediatorRef.current.register('contadores', contadoresRef.current);
  }, []);

  useEffect(() => {
    if (!loading) {
      mediatorRef.current.register('mapaGlobal', mapaRef.current);
      mediatorRef.current.register('lineaTiempo', timelineRef.current);
    }
  }, [loading]);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = useCallback(async (filtros = {}) => {
    setLoading(true);
    const [eventosRes, resumenRes] = await Promise.all([
      obtenerEventosGlobales(filtros),
      obtenerResumenGlobal(),
    ]);
    if (eventosRes.success) {
      setEventos(eventosRes.data || []);
    }
    if (resumenRes.success) {
      setResumen(resumenRes.data);
    }
    setLoading(false);
  }, []);

  const handleFiltrosCambiados = useCallback((filtros) => {
    cargarDatos(filtros);
  }, [cargarDatos]);

  return (
    <div className="visor-global">
      <div className="visor-header">
        <h1>Visor Global</h1>
        <p>Monitoreo en vivo de eventos de auditoría en todo el mundo</p>
      </div>

      <FiltrosPanel
        mediator={mediatorRef.current}
        onFiltrosCambiados={handleFiltrosCambiados}
      />

      <ContadoresGlobales ref={contadoresRef} resumen={resumen} />

      <div className="visor-grid">
        <div className="visor-mapa">
          <div className="visor-panel-header">
            <h3>Mapa Global</h3>
          </div>
          <div className="panel-body">
            {loading ? (
              <div className="loading-container">
                <div className="spinner"></div>
              </div>
            ) : (
              <MapaGlobal ref={mapaRef} />
            )}
          </div>
        </div>

        <div className="visor-timeline">
          <div className="visor-panel-header">
            <h3>Línea de Tiempo</h3>
            <span className="eventos-count">{eventos.length} eventos</span>
          </div>
          <div className="panel-body">
            {loading ? (
              <div className="loading-container">
                <div className="spinner"></div>
              </div>
            ) : (
              <LineaTiempoFilterable ref={timelineRef} eventos={eventos} />
            )}
          </div>
        </div>
      </div>

      <ConexionWebSocket mediator={mediatorRef.current} />
    </div>
  );
};

export default VisorGlobalPage;
