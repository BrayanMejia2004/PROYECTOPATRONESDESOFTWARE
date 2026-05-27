import { describe, it, expect } from 'vitest';
import {
  VisitanteIP, aceptar, FilaVisitante, GraficoVisitante,
  MetricasVisitante, CsvVisitante, TemporalFlowVisitante,
  TimeFrameVisitante, COLORES_FONDO, COLORES_BARRA,
} from '../VisitanteIP';

const ipMock = {
  ipOrigen: '10.0.0.1',
  totalEventos: 50,
  totalUsuariosDistintos: 3,
  primeraVez: '2026-05-01T10:00:00Z',
  ultimaVez: '2026-05-27T15:00:00Z',
  nivelIntensidad: 7,
  esSospechosa: true,
};

describe('COLORES_FONDO', () => {
  it('retorna color base para nivel 0 o null', () => {
    expect(COLORES_FONDO(0)).toBe('#1a2332');
    expect(COLORES_FONDO(null)).toBe('#1a2332');
    expect(COLORES_FONDO(undefined)).toBe('#1a2332');
  });

  it('retorna amarillo para nivel 1-3', () => {
    expect(COLORES_FONDO(1)).toBe('rgba(234, 179, 8, 0.2)');
    expect(COLORES_FONDO(3)).toBe('rgba(234, 179, 8, 0.2)');
  });

  it('retorna naranja para nivel 4-6', () => {
    expect(COLORES_FONDO(4)).toBe('rgba(249, 115, 22, 0.25)');
    expect(COLORES_FONDO(6)).toBe('rgba(249, 115, 22, 0.25)');
  });

  it('retorna rojo para nivel 7-9', () => {
    expect(COLORES_FONDO(7)).toBe('rgba(239, 68, 68, 0.3)');
    expect(COLORES_FONDO(9)).toBe('rgba(239, 68, 68, 0.3)');
  });

  it('retorna rojo oscuro para nivel 10', () => {
    expect(COLORES_FONDO(10)).toBe('rgba(185, 28, 28, 0.45)');
  });
});

describe('COLORES_BARRA', () => {
  it('retorna color base para nivel 0 o null', () => {
    expect(COLORES_BARRA(0)).toBe('#1a2332');
    expect(COLORES_BARRA(null)).toBe('#1a2332');
  });

  it('retorna amarillo para nivel 1-3', () => {
    expect(COLORES_BARRA(2)).toBe('#eab308');
  });

  it('retorna naranja para nivel 4-6', () => {
    expect(COLORES_BARRA(5)).toBe('#f97316');
  });

  it('retorna rojo para nivel 7-9', () => {
    expect(COLORES_BARRA(8)).toBe('#ef4444');
  });

  it('retorna rojo oscuro para nivel 10', () => {
    expect(COLORES_BARRA(10)).toBe('#b91c1c');
  });
});

describe('VisitanteIP', () => {
  it('visitar lanza error por defecto', () => {
    const v = new VisitanteIP();
    expect(() => v.visitar({})).toThrow('Debe implementar visitar(ip)');
  });
});

describe('aceptar', () => {
  it('aplica visitante a cada IP', () => {
    const visitante = { visitar: (ip) => ({ ...ip, procesado: true }) };
    const ips = [{ id: 1 }, { id: 2 }];
    const result = aceptar(ips, visitante);
    expect(result).toHaveLength(2);
    expect(result[0].procesado).toBe(true);
    expect(result[1].procesado).toBe(true);
  });

  it('retorna array vacio si ips esta vacio', () => {
    const visitante = { visitar: (ip) => ip };
    expect(aceptar([], visitante)).toEqual([]);
  });
});

describe('FilaVisitante', () => {
  it('agrega campos _prefijados sin mutar original', () => {
    const visitante = new FilaVisitante();
    const original = { ...ipMock };
    const result = visitante.visitar(ipMock);
    expect(result._colorFondo).toBe('rgba(239, 68, 68, 0.3)');
    expect(result._colorBarra).toBe('#ef4444');
    expect(result._primeraVezStr).toBeDefined();
    expect(result._ultimaVezStr).toBeDefined();
    expect(result._estadoLabel).toBe('⚠ Sospechosa');
    expect(result._estadoClase).toBe('badge-sospechosa');
    expect(result._anchoBarra).toBe('70%');
    expect(result.ipOrigen).toBe('10.0.0.1');
    expect(ipMock).toEqual(original);
  });

  it('usa label Normal para IP no sospechosa', () => {
    const result = new FilaVisitante().visitar({ ...ipMock, esSospechosa: false });
    expect(result._estadoLabel).toBe('✓ Normal');
    expect(result._estadoClase).toBe('badge-normal');
  });

  it('formatea fechas correctamente', () => {
    const result = new FilaVisitante().visitar(ipMock);
    expect(typeof result._primeraVezStr).toBe('string');
    expect(result._primeraVezStr.length).toBeGreaterThan(0);
  });

  it('tolera ip sin fechas', () => {
    const result = new FilaVisitante().visitar({ ...ipMock, primeraVez: null, ultimaVez: undefined });
    expect(result._primeraVezStr).toBe('-');
    expect(result._ultimaVezStr).toBe('-');
  });
});

describe('GraficoVisitante', () => {
  it('extrae solo campos necesarios y agrega _color', () => {
    const result = new GraficoVisitante().visitar(ipMock);
    expect(result).toEqual({
      ipOrigen: '10.0.0.1',
      totalEventos: 50,
      totalUsuariosDistintos: 3,
      nivelIntensidad: 7,
      esSospechosa: true,
      _color: '#ef4444',
    });
  });
});

describe('MetricasVisitante', () => {
  it('calcula metricas sobre conjunto de IPs', () => {
    const ips = [
      { totalEventos: 100, totalUsuariosDistintos: 5, esSospechosa: true, nivelIntensidad: 8 },
      { totalEventos: 50, totalUsuariosDistintos: 2, esSospechosa: false, nivelIntensidad: 3 },
      { totalEventos: 200, totalUsuariosDistintos: 10, esSospechosa: true, nivelIntensidad: 10 },
    ];
    const m = new MetricasVisitante().calcular(ips);
    expect(m.totalEventos).toBe(350);
    expect(m.totalSospechosas).toBe(2);
    expect(m.ipsAltaActividad).toBe(2);
    expect(m.ipsCriticas).toBe(1);
    expect(m.totalUsuarios).toBe(17);
    expect(m.promedioEventos).toBe(116.7);
  });

  it('retorna ceros para array vacio', () => {
    const m = new MetricasVisitante().calcular([]);
    expect(m.totalEventos).toBe(0);
    expect(m.totalSospechosas).toBe(0);
    expect(m.ipsAltaActividad).toBe(0);
    expect(m.ipsCriticas).toBe(0);
    expect(m.totalUsuarios).toBe(0);
    expect(m.promedioEventos).toBe(0);
  });
});

describe('CsvVisitante', () => {
  it('visitar retorna linea CSV de la IP', () => {
    const result = new CsvVisitante().visitar(ipMock);
    expect(result).toBe('10.0.0.1,50,3,2026-05-01T10:00:00Z,2026-05-27T15:00:00Z,7,true');
  });

  it('exportar genera CSV completo con header', () => {
    const ips = [
      { ipOrigen: '10.0.0.1', totalEventos: 50, totalUsuariosDistintos: 3, primeraVez: '2026-05-01T10:00:00Z', ultimaVez: '2026-05-27T15:00:00Z', nivelIntensidad: 7, esSospechosa: true },
      { ipOrigen: '10.0.0.2', totalEventos: 10, totalUsuariosDistintos: 1, primeraVez: '2026-05-10T08:00:00Z', ultimaVez: '2026-05-20T12:00:00Z', nivelIntensidad: 3, esSospechosa: false },
    ];
    const csv = new CsvVisitante().exportar(ips);
    const lines = csv.split('\n');
    expect(lines[0]).toBe('IP,Eventos,Usuarios Distintos,Primera Vez,Última Vez,Intensidad,Sospechosa');
    expect(lines[1]).toBe('10.0.0.1,50,3,2026-05-01T10:00:00Z,2026-05-27T15:00:00Z,7,true');
    expect(lines[2]).toBe('10.0.0.2,10,1,2026-05-10T08:00:00Z,2026-05-20T12:00:00Z,3,false');
    expect(lines).toHaveLength(3);
  });
});

describe('TemporalFlowVisitante', () => {
  it('transformar ordena por fecha y agrega fechaStr', () => {
    const data = [
      { fecha: '2026-05-03', total: 10 },
      { fecha: '2026-05-01', total: 5 },
      { fecha: '2026-05-02', total: 8 },
    ];
    const result = new TemporalFlowVisitante().transformar(data);
    expect(result).toHaveLength(3);
    expect(result[0].fecha).toBe('2026-05-01');
    expect(result[1].fecha).toBe('2026-05-02');
    expect(result[2].fecha).toBe('2026-05-03');
    expect(result[0].fechaStr).toBe('2026-05-01');
  });

  it('transformar retorna array vacio para null o undefined', () => {
    expect(new TemporalFlowVisitante().transformar(null)).toEqual([]);
    expect(new TemporalFlowVisitante().transformar(undefined)).toEqual([]);
    expect(new TemporalFlowVisitante().transformar([])).toEqual([]);
  });

  it('obtenerRango retorna inicio, fin y total', () => {
    const buckets = [
      { fecha: '2026-05-01' },
      { fecha: '2026-05-15' },
      { fecha: '2026-06-01' },
    ];
    const rango = new TemporalFlowVisitante().obtenerRango(buckets);
    expect(rango.inicio).toEqual(new Date('2026-05-01'));
    expect(rango.fin).toEqual(new Date('2026-06-01'));
    expect(rango.total).toBe(3);
  });

  it('obtenerRango retorna nulls para array vacio', () => {
    const rango = new TemporalFlowVisitante().obtenerRango([]);
    expect(rango.inicio).toBeNull();
    expect(rango.fin).toBeNull();
    expect(rango.total).toBe(0);
  });
});

describe('TimeFrameVisitante', () => {
  const ipTimeline = {
    ipOrigen: '10.0.0.1',
    totalEventos: 50,
    nivelIntensidad: 7,
    primeraVez: '2026-05-10T00:00:00Z',
    ultimaVez: '2026-05-20T00:00:00Z',
  };

  it('retorna valores por defecto si no hay ventana', () => {
    const v = new TimeFrameVisitante(null, null);
    const result = v.visitar(ipTimeline);
    expect(result._activoEnVentana).toBe(true);
    expect(result._opacidad).toBe(1);
    expect(result._pulsoEscala).toBe(1);
    expect(result._pulsoColor).toBe('#ef4444');
    expect(result._intensidadVentana).toBe(7);
  });

  it('marca como inactivo si no hay superposicion', () => {
    const v = new TimeFrameVisitante('2026-06-01T00:00:00Z', '2026-06-10T00:00:00Z');
    const result = v.visitar(ipTimeline);
    expect(result._activoEnVentana).toBe(false);
    expect(result._opacidad).toBe(0.2);
    expect(result._pulsoEscala).toBe(0.4);
    expect(result._pulsoColor).toBe('#1a2332');
    expect(result._intensidadVentana).toBe(0);
  });

  it('calcula intensidad proporcional cuando hay superposicion parcial', () => {
    const v = new TimeFrameVisitante('2026-05-15T00:00:00Z', '2026-05-25T00:00:00Z');
    const result = v.visitar(ipTimeline);
    expect(result._activoEnVentana).toBe(true);
    expect(result._opacidad).toBe(1);
    expect(result._intensidadVentana).toBeGreaterThanOrEqual(1);
    expect(result._intensidadVentana).toBeLessThanOrEqual(10);
  });

  it('retorna intensidad maxima cuando la IP cubre toda la ventana', () => {
    const v = new TimeFrameVisitante('2026-05-12T00:00:00Z', '2026-05-18T00:00:00Z');
    const result = v.visitar(ipTimeline);
    expect(result._activoEnVentana).toBe(true);
    expect(result._pulsoEscala).toBeGreaterThan(0.8);
    expect(result._pulsoColor).toBeDefined();
  });

  it('tolera ip sin fechas', () => {
    const v = new TimeFrameVisitante('2026-05-01T00:00:00Z', '2026-05-31T00:00:00Z');
    const result = v.visitar({ ipOrigen: '10.0.0.1', nivelIntensidad: 5 });
    expect(result._activoEnVentana).toBe(false);
    expect(result._opacidad).toBe(0.2);
  });
});
