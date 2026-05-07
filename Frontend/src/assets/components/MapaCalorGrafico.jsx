import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell
} from 'recharts';
import './MapaCalorGrafico.css';

const getColorPorNivel = (nivel) => {
  if (!nivel || nivel <= 0) return '#1a2332';
  if (nivel <= 3) return '#eab308';
  if (nivel <= 6) return '#f97316';
  if (nivel <= 9) return '#ef4444';
  return '#b91c1c';
};

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload || !payload.length) return null;
  const data = payload[0].payload;
  return (
    <div className="chart-tooltip">
      <div className="chart-tooltip-ip">{data.ipOrigen}</div>
      <div className="chart-tooltip-row">
        <span>Eventos:</span> <strong>{data.totalEventos}</strong>
      </div>
      <div className="chart-tooltip-row">
        <span>Usuarios distintos:</span> <strong>{data.totalUsuariosDistintos}</strong>
      </div>
      <div className="chart-tooltip-row">
        <span>Intensidad:</span>
        <span className="chart-tooltip-nivel" style={{ color: getColorPorNivel(data.nivelIntensidad) }}>
          {data.nivelIntensidad}/10
        </span>
      </div>
      <div className="chart-tooltip-row">
        <span>Estado:</span>
        <span className={data.esSospechosa ? 'chart-tooltip-sospechosa' : 'chart-tooltip-normal'}>
          {data.esSospechosa ? '⚠ Sospechosa' : '✓ Normal'}
        </span>
      </div>
    </div>
  );
};

const MapaCalorGrafico = ({ ips, onIpClick }) => {
  if (!ips || ips.length === 0) return null;

  return (
    <div className="chart-container">
      <ResponsiveContainer width="100%" height={Math.max(250, ips.length * 55)}>
        <BarChart
          data={ips}
          layout="vertical"
          margin={{ top: 5, right: 30, left: 120, bottom: 5 }}
          onClick={(entry) => {
            if (entry?.activePayload?.[0]?.payload?.ipOrigen) {
              onIpClick(entry.activePayload[0].payload.ipOrigen);
            }
          }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(231,233,234,0.06)" />
          <XAxis type="number" tick={{ fill: '#8899a6', fontSize: 12 }} />
          <YAxis
            type="category"
            dataKey="ipOrigen"
            tick={{ fill: '#e7e9ea', fontSize: 12 }}
            width={110}
          />
          <Tooltip content={<CustomTooltip />} cursor={{ fill: 'rgba(212,168,83,0.08)' }} />
          <Bar dataKey="totalEventos" radius={[0, 4, 4, 0]} minPointSize={3}>
            {ips.map((entry, idx) => (
              <Cell key={idx} fill={getColorPorNivel(entry.nivelIntensidad)} fillOpacity={0.75} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default MapaCalorGrafico;
