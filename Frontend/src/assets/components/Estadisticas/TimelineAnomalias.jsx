import { useState } from 'react';

const TimelineAnomalias = ({ data }) => {
  const [tooltip, setTooltip] = useState(null);

  if (!data || !data.serie || data.serie.length === 0) return <div className="timeline-anomalias-empty">Sin datos</div>;

  const { serie, media, desv, totalAnomalias, prediccion } = data;
  const todos = [...serie, ...prediccion];
  const W = 500;
  const H = 200;
  const pad = { top: 20, right: 20, bottom: 30, left: 40 };
  const w = W - pad.left - pad.right;
  const h = H - pad.top - pad.bottom;
  const maxVal = Math.max(...todos.map(d => d.valor)) * 1.15;
  const minVal = 0;

  const xScale = (i) => pad.left + (i / (todos.length - 1)) * w;
  const yScale = (v) => pad.top + h - ((v - minVal) / (maxVal - minVal)) * h;

  const superior = serie.map(() => media + desv * 2);
  const inferior = serie.map(() => Math.max(0, media - desv * 2));

  const areaPath = serie.map((_, i) => `${xScale(i)},${yScale(superior[i])}`).join(' ') + ' L ' + serie.map((_, i) => `${xScale(i)},${yScale(inferior[i])}`).reverse().join(' ');

  const linePath = serie.map((d, i) => `${xScale(i)},${yScale(d.valor)}`).join(' ');

  const predPath = prediccion.map((d, i) => `${xScale(serie.length + i)},${yScale(d.valor)}`).join(' ');

  return (
    <div className="timeline-anomalias-wrapper">
      <svg width="100%" height="210" viewBox={`0 0 ${W} ${H}`}>
        <defs>
          <linearGradient id="bandaGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="rgba(29,155,240,0.08)" />
            <stop offset="100%" stopColor="rgba(29,155,240,0.02)" />
          </linearGradient>
        </defs>

        <path d={areaPath} fill="url(#bandaGrad)" />

        <line x1={pad.left} y1={yScale(media)} x2={pad.left + w} y2={yScale(media)} stroke="rgba(136,153,166,0.25)" strokeWidth="1" strokeDasharray="4,4" />

        <polyline points={linePath} fill="none" stroke="#1d9bf0" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />

        <polyline points={predPath} fill="none" stroke="#d4a853" strokeWidth="1.5" strokeDasharray="5,3" strokeLinecap="round" strokeLinejoin="round" />

        {serie.map((d, i) =>
          d.anomalia ? (
            <g key={`anom-${i}`} className="timeline-anomalia-punto">
              <circle
                cx={xScale(i)}
                cy={yScale(d.valor)}
                r={6}
                fill="#f4212e"
                stroke="#f4212e"
                strokeWidth="2"
                opacity={0.9}
                onMouseEnter={() => setTooltip({ x: xScale(i), y: yScale(d.valor), ...d })}
                onMouseLeave={() => setTooltip(null)}
              />
              <circle
                cx={xScale(i)}
                cy={yScale(d.valor)}
                r={10}
                fill="none"
                stroke="#f4212e"
                strokeWidth="1"
                opacity={0.4}
                className="timeline-anomalia-ripple"
              />
            </g>
          ) : null
        )}

        {serie.map((d, i) => (
          <circle
            key={`dot-${i}`}
            cx={xScale(i)}
            cy={yScale(d.valor)}
            r={2}
            fill="#1d9bf0"
            opacity={0.6}
            onMouseEnter={() => setTooltip({ x: xScale(i), y: yScale(d.valor), ...d })}
            onMouseLeave={() => setTooltip(null)}
          />
        ))}

        {[0, 1, 2, 3, 4, 5].map(i => {
          const idx = Math.round((i / 5) * (todos.length - 1));
          if (idx >= todos.length) return null;
          return (
            <text key={`xl-${i}`} x={xScale(idx)} y={H - 5} textAnchor="middle" fill="#5e6f7d" fontSize="8">
              {todos[idx]?.fecha?.substring(5) || ''}
            </text>
          );
        })}

        {[0, Math.round(maxVal / 2), Math.round(maxVal)].map((v, i) => (
          <text key={`yl-${i}`} x={pad.left - 8} y={yScale(v) + 3} textAnchor="end" fill="#5e6f7d" fontSize="8">
            {v}
          </text>
        ))}
      </svg>

      <div className="timeline-anomalias-stats">
        <div className="timeline-anomalias-stat">
          <span className="timeline-anomalias-stat-line" />
          <span>Real</span>
        </div>
        <div className="timeline-anomalias-stat">
          <span className="timeline-anomalias-stat-dash" />
          <span>Predicción</span>
        </div>
        <div className="timeline-anomalias-stat">
          <span className="timeline-anomalias-stat-band" />
          <span>Rango normal (±2σ)</span>
        </div>
        <div className="timeline-anomalias-stat">
          <span className="timeline-anomalias-stat-anom" />
          <span><strong>{totalAnomalias}</strong> anomalías</span>
        </div>
      </div>

      {tooltip && (
        <div className="timeline-anomalias-tooltip" style={{ left: Math.min(tooltip.x + 10, W - 200), top: Math.max(tooltip.y - 60, 0) }}>
          <div className="timeline-anomalias-tooltip-fecha">{tooltip.fecha}</div>
          <div className="timeline-anomalias-tooltip-valor">{tooltip.valor} eventos</div>
          {tooltip.anomalia && <div className="timeline-anomalias-tooltip-anom">⚠ Anomalía detectada</div>}
        </div>
      )}
    </div>
  );
};

export default TimelineAnomalias;
