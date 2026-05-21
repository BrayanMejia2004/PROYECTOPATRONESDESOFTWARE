const INSIGHT_ICON = (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="18" height="18">
    <circle cx="12" cy="12" r="10" />
    <line x1="12" y1="16" x2="12" y2="12" />
    <line x1="12" y1="8" x2="12.01" y2="8" />
  </svg>
);

const InsightsPanel = ({ insights }) => {
  if (!insights || insights.length === 0) {
    return (
      <div className="insights-panel">
        <div className="insights-empty">
          {INSIGHT_ICON}
          <span>Selecciona una estrategia y ejecuta un análisis para ver hallazgos automáticos</span>
        </div>
      </div>
    );
  }

  return (
    <div className="insights-panel">
      <h3 className="insights-title">
        {INSIGHT_ICON}
        Hallazgos Automáticos
      </h3>
      <div className="insights-list">
        {insights.map((insight, idx) => (
          <div key={idx} className="insight-card">
            <div className="insight-bullet" />
            <span className="insight-text">{insight}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default InsightsPanel;
