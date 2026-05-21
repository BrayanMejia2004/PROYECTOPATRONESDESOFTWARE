const generarRecomendaciones = (datos) => {
  if (!datos) return [];

  const recomendaciones = [];

  const score = datos.scoreSeguridad || 0;
  const ips = datos.ipsUtilizadas || [];
  const tienePerfil = !!datos.nombre;
  const totalSesiones = datos.totalSesiones || 0;

  if (score < 50) {
    recomendaciones.push({
      tipo: 'critica',
      icono: 'danger',
      titulo: 'Tu score de seguridad es crítico',
      descripcion: `Tu puntuación actual es ${score}/100. Te recomendamos seguir las sugerencias a continuación para mejorar tu seguridad.`
    });
  } else if (score < 80) {
    recomendaciones.push({
      tipo: 'advertencia',
      icono: 'warning',
      titulo: `Tu score de seguridad es ${score}/100`,
      descripcion: 'Vas por buen camino, pero aún puedes mejorar. Revisa las recomendaciones abajo.'
    });
  } else {
    recomendaciones.push({
      tipo: 'exito',
      icono: 'check',
      titulo: `¡Excelente! Score de seguridad: ${score}/100`,
      descripcion: 'Tu cuenta tiene un buen nivel de seguridad. Sigue así.'
    });
  }

  if (!tienePerfil) {
    recomendaciones.push({
      tipo: 'mejora',
      icono: 'user',
      titulo: 'Completa tu perfil',
      descripcion: 'Agregar tu nombre y apellido aumenta tu score en 20 puntos. Ve a "Mi Perfil" para completarlo.'
    });
  }

  if (ips.length > 2) {
    recomendaciones.push({
      tipo: 'mejora',
      icono: 'globe',
      titulo: 'Has accedido desde varias IPs',
      descripcion: `Has utilizado ${ips.length} direcciones IP diferentes. Intenta usar redes confiables y evita conectarte desde redes públicas.`
    });
  } else if (ips.length === 1) {
    recomendaciones.push({
      tipo: 'exito',
      icono: 'shield',
      titulo: 'Acceso desde IP única',
      descripcion: 'Siempre accedes desde la misma IP, lo cual es un buen hábito de seguridad.'
    });
  }

  if (totalSesiones === 0) {
    recomendaciones.push({
      tipo: 'mejora',
      icono: 'clock',
      titulo: 'Mantén tu sesión activa',
      descripcion: 'Inicia sesión regularmente para mantener tu cuenta activa y monitorear tu actividad.'
    });
  }

  recomendaciones.push({
    tipo: 'informacion',
    icono: 'info',
    titulo: 'Cambia tu contraseña periódicamente',
    descripcion: 'Se recomienda cambiar tu contraseña cada 3-6 meses para mantener tu cuenta segura.'
  });

  return recomendaciones;
};

const iconos = {
  danger: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
      <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
      <line x1="12" y1="9" x2="12" y2="13" />
      <line x1="12" y1="17" x2="12.01" y2="17" />
    </svg>
  ),
  warning: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
      <circle cx="12" cy="12" r="10" />
      <line x1="12" y1="8" x2="12" y2="12" />
      <line x1="12" y1="16" x2="12.01" y2="16" />
    </svg>
  ),
  check: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
      <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
      <polyline points="22 4 12 14.01 9 11.01" />
    </svg>
  ),
  user: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
      <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
      <circle cx="12" cy="7" r="4" />
    </svg>
  ),
  globe: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
      <circle cx="12" cy="12" r="10" />
      <line x1="2" y1="12" x2="22" y2="12" />
      <path d="M12 2a15.3 15.3 0 014 10 15.3 15.3 0 01-4 10 15.3 15.3 0 01-4-10 15.3 15.3 0 014-10z" />
    </svg>
  ),
  shield: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
    </svg>
  ),
  clock: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
      <circle cx="12" cy="12" r="10" />
      <polyline points="12 6 12 12 16 14" />
    </svg>
  ),
  info: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
      <circle cx="12" cy="12" r="10" />
      <line x1="12" y1="16" x2="12" y2="12" />
      <line x1="12" y1="8" x2="12.01" y2="8" />
    </svg>
  )
};

const VistaRecomendaciones = ({ datos }) => {
  const recomendaciones = generarRecomendaciones(datos);

  return (
    <div className="state-section">
      <div className="state-details-card">
        <h3 className="state-details-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="18" height="18">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
          </svg>
          Recomendaciones de Seguridad
        </h3>
        <p className="state-recomendaciones-subtitle">
          Sugerencias personalizadas basadas en tu comportamiento y configuración
        </p>

        <div className="state-recomendaciones-list">
          {recomendaciones.map((rec, idx) => (
            <div key={idx} className={`state-recomendacion state-rec-${rec.tipo}`}>
              <div className="state-rec-icon">{iconos[rec.icono]}</div>
              <div className="state-rec-content">
                <span className="state-rec-title">{rec.titulo}</span>
                <span className="state-rec-desc">{rec.descripcion}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default VistaRecomendaciones;
