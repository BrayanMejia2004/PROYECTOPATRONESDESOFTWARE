import { useState, useEffect, useRef, useCallback } from 'react';
import { AnalizadorContext, obtenerEstrategiasInfo } from '../../Components/Strategy/AnalizadorContext';
import SelectorEstrategias from '../../Components/Strategy/SelectorEstrategias';
import InsightsPanel from '../../Components/Strategy/InsightsPanel';
import { obtenerEstrategias } from '../../Services/analisis/analisisService';
import '../../Components/Strategy/StrategyStyles.css';
import './AnalizadorPage.css';

const FILTROS_INICIALES = {
  fechaDesde: '',
  fechaHasta: '',
  filtros: {}
};

const AnalizadorPage = () => {
  const [context] = useState(() => new AnalizadorContext());
  const [estrategiaActiva, setEstrategiaActiva] = useState(null);
  const [estrategiasApi, setEstrategiasApi] = useState([]);
  const [resultado, setResultado] = useState(null);
  const [insights, setInsights] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [filtros, setFiltros] = useState(FILTROS_INICIALES);
  const mountedRef = useRef(true);

  useEffect(() => {
    obtenerEstrategias()
      .then(res => setEstrategiasApi(res.data))
      .catch(() => {});
  }, []);

  useEffect(() => {
    mountedRef.current = true;
    const unsub = context.subscribe((estrategia) => {
      if (mountedRef.current && estrategia) {
        setEstrategiaActiva(estrategia.getTipo());
      }
    });
    return () => {
      mountedRef.current = false;
      unsub();
    };
  }, [context]);

  useEffect(() => {
    if (!estrategiaActiva) return;
    const current = context.getResultado();
    if (current && current.estrategia && current.estrategia.getTipo() === estrategiaActiva) {
      setResultado(current);
      setInsights(current.insights || []);
    }
  }, [estrategiaActiva, context]);

  const handleSeleccionarEstrategia = useCallback((tipo) => {
    context.setEstrategia(tipo);
  }, [context]);

  const ejecutarAnalisis = useCallback(async () => {
    if (!context.getEstrategiaTipo()) {
      setError('Selecciona una estrategia primero');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const result = await context.cargarYAnalizar(filtros);
      if (result) {
        setResultado(result);
        setInsights(result.insights || []);
      }
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Error al ejecutar análisis');
    } finally {
      setLoading(false);
    }
  }, [context, filtros]);

  const estrategiaPrimeraVez = useCallback(() => {
    const info = obtenerEstrategiasInfo();
    if (info.length > 0 && !context.getEstrategiaTipo()) {
      context.setEstrategia(info[0].tipo);
    }
  }, [context]);

  useEffect(() => {
    estrategiaPrimeraVez();
  }, [estrategiaPrimeraVez]);

  return (
    <div className="analizador-page">
      <div className="analizador-header">
        <h1 className="analizador-titulo">Analizador Inteligente</h1>
        <p className="analizador-subtitulo">
          Analiza los datos de auditoría desde múltiples perspectivas intercambiando estrategias de visualización
        </p>
      </div>

      <SelectorEstrategias
        estrategiaActiva={estrategiaActiva}
        onSeleccionar={handleSeleccionarEstrategia}
      />

      <div className="analizador-filtros">
        <div className="filtros-group">
          <label className="filtro-label">Fecha Desde</label>
          <input
            type="date"
            className="filtro-input"
            value={filtros.fechaDesde}
            onChange={(e) => setFiltros(prev => ({ ...prev, fechaDesde: e.target.value }))}
          />
        </div>
        <div className="filtros-group">
          <label className="filtro-label">Fecha Hasta</label>
          <input
            type="date"
            className="filtro-input"
            value={filtros.fechaHasta}
            onChange={(e) => setFiltros(prev => ({ ...prev, fechaHasta: e.target.value }))}
          />
        </div>
        <div className="filtros-group">
          <label className="filtro-label">Usuario</label>
          <input
            type="text"
            className="filtro-input"
            placeholder="Filtrar por usuario"
            value={filtros.filtros.usuario || ''}
            onChange={(e) => setFiltros(prev => ({
              ...prev,
              filtros: { ...prev.filtros, usuario: e.target.value }
            }))}
          />
        </div>
        <div className="filtros-group">
          <label className="filtro-label">Acción</label>
          <input
            type="text"
            className="filtro-input"
            placeholder="Filtrar por acción"
            value={filtros.filtros.accion || ''}
            onChange={(e) => setFiltros(prev => ({
              ...prev,
              filtros: { ...prev.filtros, accion: e.target.value }
            }))}
          />
        </div>
        <button
          className="filtro-btn-ejecutar"
          onClick={ejecutarAnalisis}
          disabled={loading}
        >
          {loading ? 'Analizando...' : 'Ejecutar Análisis'}
        </button>
      </div>

      {error && (
        <div className="analizador-error">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
            <circle cx="12" cy="12" r="10" />
            <line x1="15" y1="9" x2="9" y2="15" />
            <line x1="9" y1="9" x2="15" y2="15" />
          </svg>
          {error}
        </div>
      )}

      {loading && (
        <div className="analizador-loading">
          <div className="analizador-spinner" />
          <span>Procesando análisis...</span>
        </div>
      )}

      {!loading && !error && resultado && (
        <div className="analizador-visualizacion">
          <div className="strategy-chart-container">
            {resultado.vista}
          </div>

          <InsightsPanel insights={insights} />
        </div>
      )}

      {!loading && !error && !resultado && (
        <div className="analizador-empty">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" width="48" height="48">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="16" x2="12" y2="12" />
            <line x1="12" y1="8" x2="12.01" y2="8" />
          </svg>
          <p>Selecciona una estrategia, ajusta los filtros y ejecuta el análisis para visualizar los datos</p>
        </div>
      )}
    </div>
  );
};

export default AnalizadorPage;
