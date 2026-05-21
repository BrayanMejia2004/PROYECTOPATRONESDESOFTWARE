import { EstrategiaVisualizacion } from './Strategy';

const RANK_COLORS = ['#d4a853', '#c9a040', '#b8892e', '#8a6b1f', '#6b5218'];

export class EstrategiaComparativa extends EstrategiaVisualizacion {
  getNombre() { return 'Ranking Comparativo'; }
  getDescripcion() { return 'Usuarios más activos y acciones más frecuentes en el período seleccionado'; }
  getIcono() { return 'trophy'; }
  getTipo() { return 'COMPARATIVA'; }

  procesarDatos(dataBruta) {
    if (!dataBruta || !dataBruta.datos) {
      return { topUsuarios: [], topAcciones: [], distribucion: [], metricas: {} };
    }
    return {
      topUsuarios: dataBruta.metricas?.topUsuarios || [],
      topAcciones: dataBruta.metricas?.topAcciones || [],
      distribucion: dataBruta.datos || [],
      metricas: dataBruta.metricas || {},
      insights: dataBruta.insights || []
    };
  }

  renderizar(datosProcesados) {
    const { topUsuarios, topAcciones } = datosProcesados;
    const noData = (!topUsuarios || topUsuarios.length === 0) && (!topAcciones || topAcciones.length === 0);

    if (noData) {
      return <div className="strategy-empty">Sin datos comparativos disponibles</div>;
    }

    return (
      <div className="strategy-chart-container">
        <div className="strategy-ranking-grid">
          {topUsuarios && topUsuarios.length > 0 && (
            <div className="strategy-ranking-card">
              <h3 className="strategy-chart-title">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#d4a853" strokeWidth="2" style={{ marginRight: 8, verticalAlign: 'middle' }}>
                  <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                  <circle cx="9" cy="7" r="4" />
                  <path d="M23 21v-2a4 4 0 00-3-3.87" />
                  <path d="M16 3.13a4 4 0 010 7.75" />
                </svg>
                Usuarios Más Activos
              </h3>
              <div className="strategy-ranking-list">
                {topUsuarios.map((item, idx) => (
                  <div key={idx} className="strategy-ranking-item">
                    <span className="strategy-rank" style={{ backgroundColor: RANK_COLORS[idx] || '#333' }}>
                      {idx + 1}
                    </span>
                    <span className="strategy-rank-label">{item.usuario || item.username || `Usuario #${item.id}`}</span>
                    <span className="strategy-rank-value">{item.total || item.eventos || 0} eventos</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {topAcciones && topAcciones.length > 0 && (
            <div className="strategy-ranking-card">
              <h3 className="strategy-chart-title">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#d4a853" strokeWidth="2" style={{ marginRight: 8, verticalAlign: 'middle' }}>
                  <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
                </svg>
                Acciones Más Frecuentes
              </h3>
              <div className="strategy-ranking-list">
                {topAcciones.map((item, idx) => (
                  <div key={idx} className="strategy-ranking-item">
                    <span className="strategy-rank" style={{ backgroundColor: RANK_COLORS[idx] || '#333' }}>
                      {idx + 1}
                    </span>
                    <span className="strategy-rank-label">{item.accion || item.nombre || `Acción #${idx + 1}`}</span>
                    <span className="strategy-rank-value">{item.total || item.conteo || 0} veces</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    );
  }
}
