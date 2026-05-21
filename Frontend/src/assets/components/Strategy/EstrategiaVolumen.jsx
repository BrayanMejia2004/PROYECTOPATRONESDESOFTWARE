import { EstrategiaVisualizacion } from './Strategy';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';

const COLORS = ['#d4a853', '#f5c26b', '#e8b84e', '#c99a3a', '#b8892e'];

export class EstrategiaVolumen extends EstrategiaVisualizacion {
  getNombre() { return 'Volumen de Actividad'; }
  getDescripcion() { return 'Eventos agrupados por día y hora para identificar picos de actividad'; }
  getIcono() { return 'chart-bar'; }
  getTipo() { return 'VOLUMEN'; }

  procesarDatos(dataBruta) {
    if (!dataBruta || !dataBruta.datos) return { series: [], metricas: {} };
    return {
      series: dataBruta.datos.map(d => ({
        fecha: d.fecha || d.fechaHora || '',
        total: d.total || 0,
        detalle: d.detalle || ''
      })),
      metricas: dataBruta.metricas || {},
      insights: dataBruta.insights || []
    };
  }

  renderizar(datosProcesados) {
    const { series } = datosProcesados;
    if (!series || series.length === 0) {
      return <div className="strategy-empty">Sin datos de volumen disponibles</div>;
    }
    return (
      <div className="strategy-chart-container">
        <h3 className="strategy-chart-title">Eventos por Período</h3>
        <ResponsiveContainer width="100%" height={350}>
          <BarChart data={series} margin={{ top: 10, right: 20, left: 0, bottom: 30 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" />
            <XAxis
              dataKey="fecha"
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
            <Bar dataKey="total" radius={[4, 4, 0, 0]}>
              {series.map((_, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    );
  }
}
