export const COLORES_FONDO = (nivel) => {
  if (!nivel || nivel <= 0) return '#1a2332';
  if (nivel <= 3) return 'rgba(234, 179, 8, 0.2)';
  if (nivel <= 6) return 'rgba(249, 115, 22, 0.25)';
  if (nivel <= 9) return 'rgba(239, 68, 68, 0.3)';
  return 'rgba(185, 28, 28, 0.45)';
};

export const COLORES_BARRA = (nivel) => {
  if (!nivel || nivel <= 0) return '#1a2332';
  if (nivel <= 3) return '#eab308';
  if (nivel <= 6) return '#f97316';
  if (nivel <= 9) return '#ef4444';
  return '#b91c1c';
};

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  return new Date(fecha).toLocaleString('es-ES');
};

export class VisitanteIP {
  visitar(ip) {
    throw new Error('Debe implementar visitar(ip)');
  }
}

export function aceptar(ips, visitante) {
  return ips.map((ip) => visitante.visitar(ip));
}

export class FilaVisitante extends VisitanteIP {
  visitar(ip) {
    return {
      ...ip,
      _colorFondo: COLORES_FONDO(ip.nivelIntensidad),
      _colorBarra: COLORES_BARRA(ip.nivelIntensidad),
      _primeraVezStr: formatearFecha(ip.primeraVez),
      _ultimaVezStr: formatearFecha(ip.ultimaVez),
      _estadoLabel: ip.esSospechosa ? '\u26A0 Sospechosa' : '\u2713 Normal',
      _estadoClase: ip.esSospechosa ? 'badge-sospechosa' : 'badge-normal',
      _anchoBarra: `${ip.nivelIntensidad * 10}%`,
    };
  }
}

export class GraficoVisitante extends VisitanteIP {
  visitar(ip) {
    return {
      ipOrigen: ip.ipOrigen,
      totalEventos: ip.totalEventos,
      totalUsuariosDistintos: ip.totalUsuariosDistintos,
      nivelIntensidad: ip.nivelIntensidad,
      esSospechosa: ip.esSospechosa,
      _color: COLORES_BARRA(ip.nivelIntensidad),
    };
  }
}

export class MetricasVisitante extends VisitanteIP {
  calcular(ips) {
    const totalEventos = ips.reduce((s, i) => s + (i.totalEventos || 0), 0);
    return {
      totalEventos,
      totalSospechosas: ips.filter((i) => i.esSospechosa).length,
      ipsAltaActividad: ips.filter((i) => i.nivelIntensidad >= 7).length,
      ipsCriticas: ips.filter((i) => i.nivelIntensidad === 10).length,
      totalUsuarios: ips.reduce((s, i) => s + (i.totalUsuariosDistintos || 0), 0),
      promedioEventos:
        ips.length > 0 ? Math.round((totalEventos / ips.length) * 10) / 10 : 0,
    };
  }
}

export class CsvVisitante extends VisitanteIP {
  visitar(ip) {
    return `${ip.ipOrigen},${ip.totalEventos},${ip.totalUsuariosDistintos},${ip.primeraVez ?? ''},${ip.ultimaVez ?? ''},${ip.nivelIntensidad},${ip.esSospechosa}`;
  }

  exportar(ips) {
    const header = 'IP,Eventos,Usuarios Distintos,Primera Vez,Última Vez,Intensidad,Sospechosa';
    const rows = ips.map((ip) => this.visitar(ip));
    return [header, ...rows].join('\n');
  }
}
