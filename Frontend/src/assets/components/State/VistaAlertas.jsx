import { useState, useEffect } from 'react';

const STORAGE_KEY = 'mi-actividad-alertas';

const alertasConfig = [
  {
    id: 'nueva_ip',
    label: 'Notificarme cuando inicie sesión desde una IP nueva',
    descripcion: 'Recibirás una alerta si detectamos un acceso desde una ubicación no habitual'
  },
  {
    id: 'intentos_fallidos',
    label: 'Notificarme cuando haya más de 3 intentos fallidos',
    descripcion: 'Te avisaremos si alguien intenta acceder a tu cuenta sin éxito repetidamente'
  },
  {
    id: 'horario_atipico',
    label: 'Notificarme cuando mi cuenta sea accedida fuera de horario',
    descripcion: 'Recibirás alerta por accesos entre las 12:00 AM y 5:00 AM'
  },
  {
    id: 'resumen_semanal',
    label: 'Resumen semanal de actividad',
    descripcion: 'Cada lunes recibirás un correo con el resumen de tu actividad de la semana'
  }
];

const cargarAlertas = () => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      return JSON.parse(saved);
    }
  } catch {}
  const inicial = {};
  alertasConfig.forEach(a => { inicial[a.id] = false; });
  return inicial;
};

const VistaAlertas = () => {
  const [alertas, setAlertas] = useState(cargarAlertas);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(alertas));
    setSaved(true);
    const timer = setTimeout(() => setSaved(false), 2000);
    return () => clearTimeout(timer);
  }, [alertas]);

  const toggleAlerta = (id) => {
    setAlertas(prev => ({ ...prev, [id]: !prev[id] }));
  };

  return (
    <div className="state-section">
      <div className="state-details-card">
        <div className="state-alertas-header">
          <h3 className="state-details-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="18" height="18">
              <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9" />
              <path d="M13.73 21a2 2 0 01-3.46 0" />
            </svg>
            Alertas Personalizadas
          </h3>
          {saved && <span className="state-alertas-saved">Guardado</span>}
        </div>
        <p className="state-alertas-subtitle">
          Configura las notificaciones que deseas recibir sobre tu actividad
        </p>

        <div className="state-alertas-list">
          {alertasConfig.map(alerta => (
            <label key={alerta.id} className="state-alerta-item">
              <div className="state-alerta-info">
                <span className="state-alerta-label">{alerta.label}</span>
                <span className="state-alerta-desc">{alerta.descripcion}</span>
              </div>
              <div className={`state-toggle ${alertas[alerta.id] ? 'active' : ''}`}>
                <div className="state-toggle-thumb" />
              </div>
              <input
                type="checkbox"
                className="state-toggle-input"
                checked={alertas[alerta.id] || false}
                onChange={() => toggleAlerta(alerta.id)}
              />
            </label>
          ))}
        </div>
      </div>

      <div className="state-details-card state-alertas-info-card">
        <h3 className="state-details-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="18" height="18">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="16" x2="12" y2="12" />
            <line x1="12" y1="8" x2="12.01" y2="8" />
          </svg>
          ¿Cómo funcionan las alertas?
        </h3>
        <p className="state-alertas-info-text">
          Las alertas se guardan localmente en tu navegador. Cuando una alerta está activada,
          el sistema te notificará visualmente cuando ocurra la condición correspondiente.
          Estas configuraciones son personales y no afectan a otros usuarios.
        </p>
      </div>
    </div>
  );
};

export default VistaAlertas;
