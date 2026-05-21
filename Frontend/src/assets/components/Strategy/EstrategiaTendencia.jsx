import { EstrategiaVisualizacion } from './Strategy';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';

export class EstrategiaTendencia extends EstrategiaVisualizacion {
  getNombre() { return 'Tendencia Temporal'; }
  getDescripcion() { return 'Comparación entre períodos consecutivos para detectar tendencias al alza o baja'; }
  getIcono() { return 'chart-line'; }
  getTipo() { return 'TENDENCIA'; }

  procesarDatos(dataBruta) {
    if (!dataBruta || !dataBruta.datos) return { periodos: [], variaciones: [], metricas: {} };
    return {
      periodos: dataBruta.datos.map(d => ({
        periodo: d.periodo || d.fecha || '',
        actual: d.actual || 0,
        anterior: d.anterior || 0,
        variacion: d.variacion || 0
      })),
      variaciones: dataBruta.metricas?.variaciones || [],
      metricas: dataBruta.metricas || {},
      insights: dataBruta.insights || []
    };
  }

  renderizar(datosProcesados) {
    const { periodos } = datosProcesados;
    if (!periodos || periodos.length === 0) {
      return <div className="strategy-empty">Sin datos de tendencia disponibles</div>;
    }
    return (
      <div className="strategy-chart-container">
        <h3 className="strategy-chart-title">Evolución Temporal — Período Actual vs Anterior</h3>
        <ResponsiveContainer width="100%" height={350}>
          <LineChart data={periodos} margin={{ top: 10, right: 20, left: 0, bottom: 30 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" />
            <XAxis
              dataKey="periodo"
              tick={{ fill: '#8899a6', fontSize: 12 }}
              axisLine={{ stroke: 'rgba(255,255,255,0.1)' }}
              tickLine={false}
            />
            <YAxis
              tick={{ fill: '#8899a6', fontSize: 12 }}
              axisLine={{ stroke: 'rgba(255,255,255,0.1)' }}
              tickLine={false}
            />
            <Tooltip
              contentStyle={{
                background: '#1a2332',
                border: '1px solid rgba(212,168,83,0.3)',
                borderRadius: '8px',
                color: '#e2e8f0'
              }}
            />
            <Legend wrapperStyle={{ color: '#8899a6' }} />
            <Line
              type="monotone"
              dataKey="anterior"
              stroke="#556471"
              strokeWidth={2}
              dot={{ fill: '#556471', r: 4 }}
              name="Período Anterior"
            />
            <Line
              type="monotone"
              dataKey="actual"
              stroke="#d4a853"
              strokeWidth={3}
              dot={{ fill: '#d4a853', r: 5 }}
              name="Período Actual"
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    );
  }
}
