export class SesionGard {
  constructor() {
    this.nextGard = null;
  }

  setNext(gard) {
    this.nextGard = gard;
    return gard;
  }

  procesar(sesion) {
    if (this.nextGard) {
      return this.nextGard.procesar(sesion);
    }
    return sesion;
  }
}

export class TiempoGard extends SesionGard {
  procesar(sesion) {
    const formatearTiempo = (minutos) => {
      if (minutos < 1) return 'Ahora';
      if (minutos < 60) return `Hace ${minutos} min`;
      const horas = Math.floor(minutos / 60);
      const mins = minutos % 60;
      if (horas < 24) return `Hace ${horas}h ${mins}min`;
      const dias = Math.floor(horas / 24);
      return `Hace ${dias}d ${horas % 24}h`;
    };

    return super.procesar({
      ...sesion,
      tiempoFormateado: formatearTiempo(sesion.minutosActivo),
    });
  }
}

export class AvatarGard extends SesionGard {
  procesar(sesion) {
    return super.procesar({
      ...sesion,
      avatarLetra: sesion.username.charAt(0).toUpperCase(),
    });
  }
}

export class IpGard extends SesionGard {
  procesar(sesion) {
    return super.procesar({
      ...sesion,
      ipDisplay: sesion.ipOrigen || '-',
    });
  }
}

export class RiesgoGard extends SesionGard {
  procesar(sesion) {
    const horas = sesion.minutosActivo / 60;
    let nivelRiesgo = 'baja';
    let colorRiesgo = '#00ba7c';

    if (horas > 12) {
      nivelRiesgo = 'alta';
      colorRiesgo = '#f4212e';
    } else if (horas > 4) {
      nivelRiesgo = 'media';
      colorRiesgo = '#d4a853';
    }

    return super.procesar({
      ...sesion,
      nivelRiesgo,
      colorRiesgo,
    });
  }
}
