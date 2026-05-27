import { describe, it, expect, vi } from 'vitest';
import { SesionGard, TiempoGard, AvatarGard, IpGard, RiesgoGard } from '../SesionGard';

describe('SesionGard (abstract handler)', () => {
  it('setNext guarda la referencia y retorna el siguiente Gard', () => {
    const base = new SesionGard();
    const next = new SesionGard();
    const retorno = base.setNext(next);
    expect(base.nextGard).toBe(next);
    expect(retorno).toBe(next);
  });

  it('procesar delega al siguiente Gard cuando existe nextGard', () => {
    const base = new SesionGard();
    const mockNext = new SesionGard();
    vi.spyOn(mockNext, 'procesar');
    base.setNext(mockNext);
    const sesion = { id: 1 };
    base.procesar(sesion);
    expect(mockNext.procesar).toHaveBeenCalledWith(sesion);
  });

  it('procesar retorna la sesion cuando no hay nextGard (fin de cadena)', () => {
    const base = new SesionGard();
    const sesion = { id: 1, usuario: 'test' };
    const resultado = base.procesar(sesion);
    expect(resultado).toBe(sesion);
  });
});

describe('TiempoGard', () => {
  it('devuelve "Ahora" cuando minutosActivo < 1', () => {
    const gard = new TiempoGard();
    const sesion = { id: 1, minutosActivo: 0 };
    const resultado = gard.procesar(sesion);
    expect(resultado.tiempoFormateado).toBe('Ahora');
  });

  it('devuelve "Hace X min" cuando minutosActivo < 60', () => {
    const gard = new TiempoGard();
    const sesion = { id: 2, minutosActivo: 45 };
    const resultado = gard.procesar(sesion);
    expect(resultado.tiempoFormateado).toBe('Hace 45 min');
  });

  it('devuelve "Hace Xh Ymin" cuando minutosActivo < 1440', () => {
    const gard = new TiempoGard();
    const sesion = { id: 3, minutosActivo: 150 };
    const resultado = gard.procesar(sesion);
    expect(resultado.tiempoFormateado).toBe('Hace 2h 30min');
  });

  it('devuelve "Hace Xd Yh" cuando minutosActivo >= 1440', () => {
    const gard = new TiempoGard();
    const sesion = { id: 4, minutosActivo: 3000 };
    const resultado = gard.procesar(sesion);
    expect(resultado.tiempoFormateado).toBe('Hace 2d 2h');
  });

  it('no modifica los campos originales de la sesion', () => {
    const gard = new TiempoGard();
    const sesion = { id: 5, minutosActivo: 90, username: 'admin' };
    const resultado = gard.procesar(sesion);
    expect(resultado.id).toBe(5);
    expect(resultado.minutosActivo).toBe(90);
    expect(resultado.username).toBe('admin');
  });
});

describe('AvatarGard', () => {
  it('extrae la primera letra del username en mayuscula', () => {
    const gard = new AvatarGard();
    const sesion = { id: 1, username: 'juan' };
    const resultado = gard.procesar(sesion);
    expect(resultado.avatarLetra).toBe('J');
  });

  it('funciona con username de una sola letra', () => {
    const gard = new AvatarGard();
    const sesion = { id: 2, username: 'a' };
    const resultado = gard.procesar(sesion);
    expect(resultado.avatarLetra).toBe('A');
  });
});

describe('IpGard', () => {
  it('usa el valor de ipOrigen cuando existe', () => {
    const gard = new IpGard();
    const sesion = { id: 1, ipOrigen: '192.168.1.1' };
    const resultado = gard.procesar(sesion);
    expect(resultado.ipDisplay).toBe('192.168.1.1');
  });

  it('retorna "-" cuando ipOrigen es null', () => {
    const gard = new IpGard();
    const sesion = { id: 2, ipOrigen: null };
    const resultado = gard.procesar(sesion);
    expect(resultado.ipDisplay).toBe('-');
  });

  it('retorna "-" cuando ipOrigen es undefined', () => {
    const gard = new IpGard();
    const sesion = { id: 3 };
    const resultado = gard.procesar(sesion);
    expect(resultado.ipDisplay).toBe('-');
  });
});

describe('RiesgoGard', () => {
  it('asigna nivel "baja" y color "#00ba7c" cuando minutosActivo <= 240 (4h)', () => {
    const gard = new RiesgoGard();
    const sesion = { id: 1, minutosActivo: 180 };
    const resultado = gard.procesar(sesion);
    expect(resultado.nivelRiesgo).toBe('baja');
    expect(resultado.colorRiesgo).toBe('#00ba7c');
  });

  it('asigna nivel "media" y color "#d4a853" cuando minutosActivo > 240 y <= 720', () => {
    const gard = new RiesgoGard();
    const sesion = { id: 2, minutosActivo: 480 };
    const resultado = gard.procesar(sesion);
    expect(resultado.nivelRiesgo).toBe('media');
    expect(resultado.colorRiesgo).toBe('#d4a853');
  });

  it('asigna nivel "alta" y color "#f4212e" cuando minutosActivo > 720 (12h)', () => {
    const gard = new RiesgoGard();
    const sesion = { id: 3, minutosActivo: 800 };
    const resultado = gard.procesar(sesion);
    expect(resultado.nivelRiesgo).toBe('alta');
    expect(resultado.colorRiesgo).toBe('#f4212e');
  });

  it('usa riesgo baja en el limite exacto de 240 minutos', () => {
    const gard = new RiesgoGard();
    const sesion = { id: 4, minutosActivo: 240 };
    const resultado = gard.procesar(sesion);
    expect(resultado.nivelRiesgo).toBe('baja');
  });

  it('usa riesgo media en el limite exacto de 720 minutos', () => {
    const gard = new RiesgoGard();
    const sesion = { id: 5, minutosActivo: 720 };
    const resultado = gard.procesar(sesion);
    expect(resultado.nivelRiesgo).toBe('media');
  });
});

describe('Cadena completa (Chain of Responsibility)', () => {
  const construirCadena = () => {
    const tiempo = new TiempoGard();
    tiempo.setNext(new AvatarGard()).setNext(new IpGard()).setNext(new RiesgoGard());
    return tiempo;
  };

  it('procesa una sesion a traves de toda la cadena y agrega todos los campos', () => {
    const cadena = construirCadena();
    const sesionCruda = {
      id: 1,
      username: 'admin',
      ipOrigen: '10.0.0.1',
      minutosActivo: 150,
    };
    const resultado = cadena.procesar(sesionCruda);
    expect(resultado.tiempoFormateado).toBe('Hace 2h 30min');
    expect(resultado.avatarLetra).toBe('A');
    expect(resultado.ipDisplay).toBe('10.0.0.1');
    expect(resultado.nivelRiesgo).toBe('baja');
    expect(resultado.colorRiesgo).toBe('#00ba7c');
  });

  it('procesa multiples sesiones con map', () => {
    const cadena = construirCadena();
    const sesionesCrudas = [
      { id: 1, username: 'jperez', ipOrigen: '10.0.0.1', minutosActivo: 30 },
      { id: 2, username: 'admin', ipOrigen: '10.0.0.2', minutosActivo: 500 },
      { id: 3, username: 'lmaria', ipOrigen: null, minutosActivo: 1500 },
    ];
    const resultado = sesionesCrudas.map(s => cadena.procesar(s));
    expect(resultado).toHaveLength(3);
    expect(resultado[0].avatarLetra).toBe('J');
    expect(resultado[0].tiempoFormateado).toBe('Hace 30 min');
    expect(resultado[0].nivelRiesgo).toBe('baja');
    expect(resultado[1].nivelRiesgo).toBe('media');
    expect(resultado[1].colorRiesgo).toBe('#d4a853');
    expect(resultado[2].ipDisplay).toBe('-');
    expect(resultado[2].nivelRiesgo).toBe('alta');
    expect(resultado[2].colorRiesgo).toBe('#f4212e');
    expect(resultado[2].tiempoFormateado).toBe('Hace 1d 1h');
  });

  it('no muta las sesiones originales', () => {
    const cadena = construirCadena();
    const sesionCruda = {
      id: 1,
      username: 'test',
      ipOrigen: '192.168.1.1',
      minutosActivo: 100,
    };
    const copiaAntes = { ...sesionCruda };
    cadena.procesar(sesionCruda);
    expect(sesionCruda).toEqual(copiaAntes);
  });

  it('es extensible insertando un nuevo Gard en medio de la cadena', () => {
    class MayusculaGard extends SesionGard {
      procesar(sesion) {
        return super.procesar({
          ...sesion,
          usernameMayus: sesion.username.toUpperCase(),
        });
      }
    }
    const tiempo = new TiempoGard();
    tiempo.setNext(new AvatarGard())
      .setNext(new MayusculaGard())
      .setNext(new IpGard())
      .setNext(new RiesgoGard());

    const sesion = { id: 1, username: 'juan', ipOrigen: '10.0.0.1', minutosActivo: 60 };
    const resultado = tiempo.procesar(sesion);
    expect(resultado.usernameMayus).toBe('JUAN');
    expect(resultado.avatarLetra).toBe('J');
    expect(resultado.tiempoFormateado).toBe('Hace 1h 0min');
    expect(resultado.ipDisplay).toBe('10.0.0.1');
  });
});
