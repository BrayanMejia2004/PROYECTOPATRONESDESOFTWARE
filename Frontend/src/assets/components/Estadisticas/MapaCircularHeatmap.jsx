import { useState, useRef } from 'react';

const NIVEL_COLORES = ['#1a2332', '#153044', '#1a5276', '#d4a853', '#f5a623'];
const getNivel = (valor, maxVal) => {
  if (valor === 0 || maxVal === 0) return 0;
  const ratio = valor / maxVal;
  if (ratio <= 0.2) return 1;
  if (ratio <= 0.4) return 2;
  if (ratio <= 0.7) return 3;
  return 4;
};

const MapaCircularHeatmap = ({ data }) => {
  const [tooltip, setTooltip] = useState(null);
  const wrapperRef = useRef(null);

  if (!data || data.length === 0) return <div className="mapa-circular-empty">Sin datos</div>;

  const cx = 180;
  const cy = 150;
  const ringWidth = 14;
  const gap = 2;
  const innerR = 30;
  const totalDias = data.length;
  const maxVal = Math.max(...data.flatMap(d => d.horas));

  const anillos = data.map((dia, di) => {
    const rOuter = innerR + (totalDias - 1 - di) * (ringWidth + gap) + ringWidth;
    const rInner = rOuter - ringWidth;
    const segmentos = dia.horas.map((valor, hi) => {
      const angInicio = (hi / 24) * 360 - 90;
      const angFin = ((hi + 1) / 24) * 360 - 90;
      const aIni = (angInicio * Math.PI) / 180;
      const aFin = (angFin * Math.PI) / 180;
      const x1 = cx + rInner * Math.cos(aIni);
      const y1 = cy + rInner * Math.sin(aIni);
      const x2 = cx + rOuter * Math.cos(aIni);
      const y2 = cy + rOuter * Math.sin(aIni);
      const x3 = cx + rOuter * Math.cos(aFin);
      const y3 = cy + rOuter * Math.sin(aFin);
      const x4 = cx + rInner * Math.cos(aFin);
      const y4 = cy + rInner * Math.sin(aFin);
      const largeArc = angFin - angInicio > 180 ? 1 : 0;
      const pathD = `M ${x1} ${y1} L ${x2} ${y2} A ${rOuter} ${rOuter} 0 ${largeArc} 1 ${x3} ${y3} L ${x4} ${y4} A ${rInner} ${rInner} 0 ${largeArc} 0 ${x1} ${y1} Z`;
      const nivel = getNivel(valor, maxVal);
      return (
        <path
          key={`${di}-${hi}`}
          d={pathD}
          fill={NIVEL_COLORES[nivel]}
          opacity={valor === 0 ? 0.3 : 0.85}
          className="mapa-circular-segmento"
          onMouseEnter={(e) => {
            const rect = wrapperRef.current?.getBoundingClientRect();
            if (rect) setTooltip({ x: e.clientX - rect.left, y: e.clientY - rect.top - 10, dia: dia.dia, hora: `${String(hi).padStart(2, '0')}:00`, valor });
          }}
          onMouseLeave={() => setTooltip(null)}
        />
      );
    });
    return segmentos;
  });

  const marcasHora = [];
  for (let h = 0; h < 24; h += 3) {
    const ang = (h / 24) * 360 - 90;
    const aRad = (ang * Math.PI) / 180;
    const rTexto = innerR + totalDias * (ringWidth + gap) + 10;
    const x = cx + rTexto * Math.cos(aRad);
    const y = cy + rTexto * Math.sin(aRad);
    marcasHora.push(
      <text key={`h-${h}`} x={x} y={y} textAnchor="middle" dominantBaseline="middle" fill="#5e6f7d" fontSize="8" fontWeight="500">
        {String(h).padStart(2, '0')}h
      </text>
    );
  }

  const rOuterMax = innerR + (totalDias - 1) * (ringWidth + gap) + ringWidth;
  const rTextoMax = innerR + totalDias * (ringWidth + gap) + 10;
  const vbR = Math.max(rOuterMax, rTextoMax) + 15;
  const vbSize = vbR * 2;

  return (
    <div className="mapa-circular-wrapper" ref={wrapperRef}>
      <svg width="100%" height="260" viewBox={`${cx - vbR} ${cy - vbR} ${vbSize} ${vbSize}`}>
        {anillos}
        <circle cx={cx} cy={cy} r={innerR - 4} fill="none" stroke="rgba(231,233,234,0.06)" strokeWidth="1" />
        {marcasHora}
        <text x={cx} y={cy} textAnchor="middle" dominantBaseline="middle" fill="#8899a6" fontSize="10" fontWeight="600">
          24h
        </text>
      </svg>
      <div className="mapa-circular-leyenda">
        {NIVEL_COLORES.map((c, i) => (
          <span key={i} className="mapa-circular-leyenda-item">
            <span className="mapa-circular-leyenda-color" style={{ background: c }} />
            {['Sin', 'Baja', 'Media', 'Alta', 'Máx'][i]}
          </span>
        ))}
      </div>
      {tooltip && (
        <div className="mapa-circular-tooltip" style={{ left: tooltip.x + 12, top: tooltip.y - 36 }}>
          <span className="mapa-circular-tooltip-dia">{tooltip.dia}</span> <span className="mapa-circular-tooltip-hora">{tooltip.hora}</span> — <strong>{tooltip.valor}</strong>
        </div>
      )}
    </div>
  );
};

export default MapaCircularHeatmap;
