import { EstrategiaVisualizacion } from './Strategy';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';

const COLORS = {
  BASICA: '#1d9bf0',
  COMPLETA: '#d4a853',
  SEGURIDAD: '#f4212e'
};

const LABELS = {
  BASICA: 'Básica',
  COMPLETA: 'Completa',
  SEGURIDAD: 'Seguridad'
};

export class EstrategiaSeveridad extends EstrategiaVisualizacion {
  getNombre() { return 'Severidad de Eventos'; }
  getDescripcion() { return 'Clasificación de eventos por tipo de auditoría y nivel de criticidad'; }
  getIcono() { return 'shield-exclamation'; }
  getTipo() { return 'SEVERIDAD'; }

  procesarDatos(dataBruta) {
    if (!dataBruta || !dataBruta.datos) {
      return { distribucion: [], total: 0, metricas: {} };
    }
    const datos = dataBruta.datos;
    const basica = datos.filter(d => (d.tipo || '').toUpperCase() === 'BASICA').length;
    const completa = datos.filter(d => (d.tipo || '').toUpperCase() === 'COMPLETA').length;
    const seguridad = datos.filter(d => (d.tipo || '').toUpperCase() === 'SEGURIDAD').length;

    const distribucion = [
      { name: LABELS.BASICA, value: basica, color: COLORS.BASICA, key: 'BASICA' },
      { name: LABELS.COMPLETA, value: completa, color: COLORS.COMPLETA, key: 'COMPLETA' },
      { name: LABELS.SEGURIDAD, value: seguridad, color: COLORS.SEGURIDAD, key: 'SEGURIDAD' }
    ].filter(d => d.value > 0);

    const total = basica + completa + seguridad;
    return {
      distribucion,
      total,
      metricas: {
        ...(dataBruta.metricas || {}),
        porcentajeSeguridad: total > 0 ? Math.round((seguridad / total) * 100) : 0
      },
      insights: dataBruta.insights || []
    };
  }

  renderizar(datosProcesados) {
    const { distribucion, total } = datosProcesados;
    if (!distribucion || distribucion.length === 0) {
      return <div className="strategy-empty">Sin datos de severidad disponibles</div>;
    }
    return (
      <div className="strategy-chart-container">
        <h3 className="strategy-chart-title">Distribución por Tipo — {total} eventos totales</h3>
        <ResponsiveContainer width="100%" height={350}>
          <PieChart>
            <Pie
              data={distribucion}
              cx="50%"
              cy="50%"
              innerRadius={70}
              outerRadius={130}
              paddingAngle={3}
              dataKey="value"
              label={({ name, value, percent }) =>
                `${name}: ${value} (${(percent * 100).toFixed(0)}%)`
              }
              labelLine={{ stroke: '#8899a6', strokeWidth: 1 }}
            >
              {distribucion.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip
              contentStyle={{
                background: '#1a2332',
                border: '1px solid rgba(212,168,83,0.3)',
                borderRadius: '8px',
                color: '#e2e8f0'
              }}
            />
            <Legend
              wrapperStyle={{ color: '#8899a6' }}
            />
          </PieChart>
        </ResponsiveContainer>
        {datosProcesados.metricas?.porcentajeSeguridad > 0 && (
          <div className="strategy-insight-banner strategy-insight-warning">
            Eventos de seguridad: {datosProcesados.metricas.porcentajeSeguridad}% del total
          </div>
        )}
      </div>
    );
  }
}
