import { obtenerEstrategiasInfo } from './AnalizadorContext';

const ICONOS = {
  'chart-bar': (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="28" height="28">
      <line x1="18" y1="20" x2="18" y2="10" />
      <line x1="12" y1="20" x2="12" y2="4" />
      <line x1="6" y1="20" x2="6" y2="14" />
    </svg>
  ),
  'shield-exclamation': (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="28" height="28">
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
      <line x1="12" y1="8" x2="12" y2="12" />
      <line x1="12" y1="16" x2="12.01" y2="16" />
    </svg>
  ),
  'chart-line': (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="28" height="28">
      <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
    </svg>
  ),
  'trophy': (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="28" height="28">
      <path d="M6 9H4.5a2.5 2.5 0 010-5H6" />
      <path d="M18 9h1.5a2.5 2.5 0 000-5H18" />
      <path d="M4 22h16" />
      <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22" />
      <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22" />
      <path d="M18 2H6v7a6 6 0 0012 0V2z" />
    </svg>
  )
};

const SelectorEstrategias = ({ estrategiaActiva, onSeleccionar }) => {
  const estrategias = obtenerEstrategiasInfo();

  return (
    <div className="selector-estrategias">
      {estrategias.map((est) => {
        const activa = est.tipo === estrategiaActiva;
        return (
          <button
            key={est.tipo}
            className={`selector-card ${activa ? 'activa' : ''}`}
            onClick={() => onSeleccionar(est.tipo)}
            title={est.descripcion}
          >
            <div className="selector-icon">
              {ICONOS[est.icono] || ICONOS['chart-bar']}
            </div>
            <div className="selector-info">
              <span className="selector-nombre">{est.nombre}</span>
              <span className="selector-desc">{est.descripcion}</span>
            </div>
            {activa && (
              <div className="selector-check">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" width="16" height="16">
                  <polyline points="20 6 9 17 4 12" />
                </svg>
              </div>
            )}
          </button>
        );
      })}
    </div>
  );
};

export default SelectorEstrategias;
