import { useState } from 'react';

const EcosistemaBurbujas = ({ data }) => {
  const [expandido, setExpandido] = useState(null);

  if (!data || data.length === 0) return <div className="burbujas-empty">Sin datos</div>;

  const maxTotal = Math.max(...data.map(r => r.total), 1);
  const getRadio = (total) => Math.max(30, (total / maxTotal) * 60);
  const totalGeneral = data.reduce((s, r) => s + r.total, 0);

  const toggleExpandido = (idx) => {
    setExpandido(prev => prev === idx ? null : idx);
  };

  return (
    <div className="burbujas-wrapper">
      <svg width="100%" height="240" viewBox="0 0 480 240">
        {data.map((rol, i) => {
          const exp = expandido === i;
          const otrosOcultos = expandido != null && !exp;

          let x, cy, r, opacidad;
          if (expandido == null) {
            x = 130 + i * 110;
            cy = 105;
            r = getRadio(rol.total);
            opacidad = 0.7;
          } else if (exp) {
            x = 105;
            cy = 100;
            r = getRadio(rol.total) * 1.15;
            opacidad = 0.85;
          } else {
            const idx = i > expandido ? i - 1 : i;
            x = 280 + idx * 80;
            cy = 70 + idx * 15;
            r = getRadio(rol.total) * 0.75;
            opacidad = 0.12;
          }

          return (
            <g key={rol.key}>
              <defs>
                <radialGradient id={`bg-${i}`} cx="35%" cy="35%">
                  <stop offset="0%" stopColor={rol.color} stopOpacity="0.95" />
                  <stop offset="100%" stopColor={rol.color} stopOpacity="0.5" />
                </radialGradient>
              </defs>
              <circle
                cx={x} cy={cy} r={r}
                fill={`url(#bg-${i})`}
                opacity={opacidad}
                stroke={exp ? '#fff' : 'none'}
                strokeWidth={exp ? 2 : 0}
                className="burbuja-circulo"
                style={{ transition: 'cx 0.5s ease, cy 0.5s ease, r 0.5s ease, opacity 0.5s ease' }}
                onClick={() => toggleExpandido(i)}
              />
              <text
                x={x} y={cy - 5}
                textAnchor="middle"
                fill="#fff"
                fontSize={exp ? '16' : '14'}
                fontWeight="700"
                className="burbuja-texto"
                style={{ transition: 'x 0.5s ease, y 0.5s ease' }}
                onClick={() => toggleExpandido(i)}
              >
                {rol.total}
              </text>
              <text
                x={x} y={cy + 12}
                textAnchor="middle"
                fill="rgba(255,255,255,0.65)"
                fontSize={exp ? '10' : '8'}
                className="burbuja-texto"
                style={{ transition: 'x 0.5s ease, y 0.5s ease' }}
              >
                {rol.label}
              </text>

              {exp && (
                <g>
                  <text x={310} y={110} textAnchor="middle" fill="#8899a6" fontSize="8" fontWeight="600" textTransform="uppercase" letterSpacing="1">
                    USUARIOS
                  </text>
                  {rol.usuarios.slice(0, 5).map((u, ui) => (
                    <g key={`u-${ui}`} className="burbuja-usuario-item">
                      <rect
                        x={272}
                        y={118 + ui * 22}
                        width={76}
                        height={18}
                        rx={5}
                        fill={rol.color}
                        opacity={0.12}
                      />
                      <text
                        x={310}
                        y={131 + ui * 22}
                        textAnchor="middle"
                        fill={rol.color}
                        fontSize="9"
                        fontWeight="600"
                      >
                        {u.nombre}
                      </text>
                      <text
                        x={332}
                        y={131 + ui * 22}
                        textAnchor="start"
                        fill="#5e6f7d"
                        fontSize="7"
                      >
                        {u.acciones}
                      </text>
                    </g>
                  ))}
                  {rol.usuarios.length > 5 && (
                    <text x={310} y={131 + 5 * 22} textAnchor="middle" fill="#5e6f7d" fontSize="7">
                      +{rol.usuarios.length - 5} más
                    </text>
                  )}
                </g>
              )}
            </g>
          );
        })}

        <text x={240} y={230} textAnchor="middle" fill="#5e6f7d" fontSize="9">
          {totalGeneral.toLocaleString()} eventos — Click en burbuja para ver usuarios
        </text>
      </svg>
    </div>
  );
};

export default EcosistemaBurbujas;
