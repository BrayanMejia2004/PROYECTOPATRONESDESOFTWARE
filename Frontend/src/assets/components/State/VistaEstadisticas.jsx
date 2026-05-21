const getColorByScore = (score) => {
  if (score >= 80) return '#00ba7c';
  if (score >= 50) return '#d4a853';
  return '#f4212e';
};

const getLabelByScore = (score) => {
  if (score >= 80) return 'Seguro';
  if (score >= 50) return 'Moderado';
  return 'Crítico';
};

const VistaEstadisticas = ({ datos }) => {
  if (!datos) return null;

  const scoreColor = getColorByScore(datos.scoreSeguridad);
  const scoreLabel = getLabelByScore(datos.scoreSeguridad);

  return (
    <div className="state-section">
      <div className="state-grid-4">
        <div className="state-card">
          <div className="state-card-icon state-icon-sessions">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="3" width="18" height="18" rx="2" />
              <line x1="3" y1="9" x2="21" y2="9" />
              <line x1="9" y1="21" x2="9" y2="9" />
            </svg>
          </div>
          <div className="state-card-value">{datos.totalSesiones}</div>
          <div className="state-card-label">Inicios de Sesión</div>
        </div>

        <div className="state-card">
          <div className="state-card-icon state-icon-ips">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="2" y1="12" x2="22" y2="12" />
              <path d="M12 2a15.3 15.3 0 014 10 15.3 15.3 0 01-4 10 15.3 15.3 0 01-4-10 15.3 15.3 0 014-10z" />
            </svg>
          </div>
          <div className="state-card-value">{datos.ipsUtilizadas?.length || 0}</div>
          <div className="state-card-label">IPs Distintas</div>
        </div>

        <div className="state-card">
          <div className="state-card-icon state-icon-profile">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </div>
          <div className="state-card-value">{datos.nombre ? 'Completo' : 'Incompleto'}</div>
          <div className="state-card-label">Perfil</div>
        </div>

        <div className={`state-card state-score-${scoreLabel.toLowerCase()}`}>
          <div className="state-card-icon state-icon-score">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
            </svg>
          </div>
          <div className="state-card-value" style={{ color: scoreColor }}>{datos.scoreSeguridad}</div>
          <div className="state-card-label">Score de Seguridad</div>
          <div className="state-score-bar">
            <div className="state-score-fill" style={{ width: `${datos.scoreSeguridad}%`, backgroundColor: scoreColor }} />
          </div>
          <div className="state-score-label" style={{ color: scoreColor }}>{scoreLabel}</div>
        </div>
      </div>

      <div className="state-details-grid">
        <div className="state-details-card">
          <h3 className="state-details-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="18" height="18">
              <circle cx="12" cy="12" r="10" />
              <line x1="2" y1="12" x2="22" y2="12" />
              <path d="M12 2a15.3 15.3 0 014 10 15.3 15.3 0 01-4 10 15.3 15.3 0 01-4-10 15.3 15.3 0 014-10z" />
            </svg>
            IPs Utilizadas
          </h3>
          <div className="state-ips-list">
            {datos.ipsUtilizadas?.length > 0 ? (
              datos.ipsUtilizadas.map((ip, idx) => (
                <div key={idx} className="state-ip-item">
                  <span className="state-ip-dot" />
                  <span className="state-ip-address">{ip}</span>
                </div>
              ))
            ) : (
              <p className="state-empty">Sin datos de IPs</p>
            )}
          </div>
        </div>

        <div className="state-details-card">
          <h3 className="state-details-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="18" height="18">
              <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="16" y1="13" x2="8" y2="13" />
              <line x1="16" y1="17" x2="8" y2="17" />
            </svg>
            Últimos Eventos
          </h3>
          <div className="state-event-list">
            {datos.ultimosEventos?.length > 0 ? (
              datos.ultimosEventos.map((evt, idx) => (
                <div key={idx} className="state-event-item">
                  <div className="state-event-indicator" />
                  <div className="state-event-info">
                    <span className="state-event-accion">{evt.accion}</span>
                    <span className="state-event-descripcion">{evt.descripcion}</span>
                  </div>
                  <span className="state-event-fecha">
                    {evt.fecha ? new Date(evt.fecha).toLocaleString('es-CO') : ''}
                  </span>
                </div>
              ))
            ) : (
              <p className="state-empty">Sin eventos recientes</p>
            )}
          </div>
        </div>
      </div>

      <div className="state-details-card">
        <h3 className="state-details-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="18" height="18">
            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
            <circle cx="12" cy="7" r="4" />
          </svg>
          Información de la Cuenta
        </h3>
        <div className="state-info-grid">
          <div className="state-info-item">
            <span className="state-info-label">Usuario</span>
            <span className="state-info-value">@{datos.username}</span>
          </div>
          <div className="state-info-item">
            <span className="state-info-label">Email</span>
            <span className="state-info-value">{datos.email}</span>
          </div>
          <div className="state-info-item">
            <span className="state-info-label">Registrado</span>
            <span className="state-info-value">
              {datos.fechaCreacion ? new Date(datos.fechaCreacion).toLocaleDateString('es-CO') : '-'}
            </span>
          </div>
          <div className="state-info-item">
            <span className="state-info-label">Último Acceso</span>
            <span className="state-info-value">
              {datos.ultimaSesion ? new Date(datos.ultimaSesion).toLocaleString('es-CO') : '-'}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default VistaEstadisticas;
