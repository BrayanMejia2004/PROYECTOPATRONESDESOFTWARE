const TIPO_COLORS = {
  LOGIN: { bg: '#1a3a5c', text: '#64b5f6' },
  REGISTRO: { bg: '#1a5c2a', text: '#66bb6a' },
  CONSULTA: { bg: '#5c3a1a', text: '#ffb74d' },
  LOGOUT: { bg: '#5c1a1a', text: '#ef5350' },
};

const EscenarioCard = ({ escenario, onEjecutar, ejecutando }) => {
  return (
    <div style={styles.card}>
      <div style={styles.cardHeader}>
        <h3 style={styles.titulo}>{escenario.nombre}</h3>
        <span style={styles.badge}>{escenario.totalEventos} eventos</span>
      </div>
      <p style={styles.descripcion}>{escenario.descripcion}</p>
      <div style={styles.tiposContainer}>
        {escenario.tiposEvento.map((tipo) => {
          const colors = TIPO_COLORS[tipo] || { bg: '#333', text: '#ccc' };
          return (
            <span
              key={tipo}
              style={{
                ...styles.tipoBadge,
                backgroundColor: colors.bg,
                color: colors.text,
              }}
            >
              {tipo}
            </span>
          );
        })}
      </div>
      <button
        style={{
          ...styles.botonEjecutar,
          opacity: ejecutando ? 0.6 : 1,
          cursor: ejecutando ? 'not-allowed' : 'pointer',
        }}
        onClick={() => !ejecutando && onEjecutar(escenario)}
        disabled={ejecutando}
      >
        {ejecutando ? 'Ejecutando...' : 'Ejecutar Escenario'}
      </button>
    </div>
  );
};

const styles = {
  card: {
    background: 'linear-gradient(135deg, #1a2332 0%, #1e2d3d 100%)',
    border: '1px solid rgba(212, 168, 83, 0.2)',
    borderRadius: '12px',
    padding: '1.5rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem',
    transition: 'all 0.3s ease',
  },
  cardHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  titulo: {
    margin: 0,
    color: '#d4a853',
    fontSize: '1.1rem',
    fontWeight: 600,
  },
  badge: {
    backgroundColor: 'rgba(212, 168, 83, 0.15)',
    color: '#d4a853',
    padding: '4px 10px',
    borderRadius: '20px',
    fontSize: '0.8rem',
    fontWeight: 500,
  },
  descripcion: {
    margin: 0,
    color: '#94a3b8',
    fontSize: '0.9rem',
    lineHeight: 1.5,
  },
  tiposContainer: {
    display: 'flex',
    gap: '0.5rem',
    flexWrap: 'wrap',
  },
  tipoBadge: {
    padding: '3px 10px',
    borderRadius: '12px',
    fontSize: '0.75rem',
    fontWeight: 500,
  },
  botonEjecutar: {
    backgroundColor: 'rgba(212, 168, 83, 0.15)',
    color: '#d4a853',
    border: '1px solid rgba(212, 168, 83, 0.3)',
    borderRadius: '8px',
    padding: '0.7rem 1rem',
    fontSize: '0.9rem',
    fontWeight: 500,
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    marginTop: '0.5rem',
  },
};

export default EscenarioCard;
