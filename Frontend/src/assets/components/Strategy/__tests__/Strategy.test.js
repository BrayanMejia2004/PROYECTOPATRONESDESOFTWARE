import { describe, it, expect } from 'vitest';
import { EstrategiaVisualizacion } from '../Strategy';
import { EstrategiaVolumen } from '../EstrategiaVolumen.jsx';
import { EstrategiaSeveridad } from '../EstrategiaSeveridad.jsx';
import { EstrategiaTendencia } from '../EstrategiaTendencia.jsx';
import { EstrategiaComparativa } from '../EstrategiaComparativa.jsx';

describe('EstrategiaVisualizacion (base class)', () => {
  it('getNombre retorna string vacio por defecto', () => {
    expect(new EstrategiaVisualizacion().getNombre()).toBe('');
  });

  it('getDescripcion retorna string vacio por defecto', () => {
    expect(new EstrategiaVisualizacion().getDescripcion()).toBe('');
  });

  it('getIcono retorna string vacio por defecto', () => {
    expect(new EstrategiaVisualizacion().getIcono()).toBe('');
  });

  it('getTipo retorna string vacio por defecto', () => {
    expect(new EstrategiaVisualizacion().getTipo()).toBe('');
  });

  it('procesarDatos retorna objeto vacio por defecto', () => {
    expect(new EstrategiaVisualizacion().procesarDatos({})).toEqual({});
  });

  it('renderizar retorna null por defecto', () => {
    expect(new EstrategiaVisualizacion().renderizar({})).toBeNull();
  });
});

describe('EstrategiaVolumen', () => {
  const estrategia = new EstrategiaVolumen();

  it('getNombre retorna Volumen de Actividad', () => {
    expect(estrategia.getNombre()).toBe('Volumen de Actividad');
  });

  it('getTipo retorna VOLUMEN', () => {
    expect(estrategia.getTipo()).toBe('VOLUMEN');
  });

  it('getIcono retorna chart-bar', () => {
    expect(estrategia.getIcono()).toBe('chart-bar');
  });

  it('procesarDatos transforma dataBruta en series', () => {
    const dataBruta = {
      datos: [
        { fecha: '2025-01-01', total: 10 },
        { fecha: '2025-01-02', total: 20 }
      ],
      metricas: { total: 30 }
    };
    const result = estrategia.procesarDatos(dataBruta);
    expect(result.series).toHaveLength(2);
    expect(result.series[0].fecha).toBe('2025-01-01');
    expect(result.series[0].total).toBe(10);
    expect(result.metricas.total).toBe(30);
  });

  it('procesarDatos retorna series vacia si no hay datos', () => {
    const result = estrategia.procesarDatos({});
    expect(result.series).toEqual([]);
  });

  it('renderizar retorna JSX con datos validos', () => {
    const datos = { series: [{ fecha: '2025-01-01', total: 10 }], metricas: {} };
    const jsx = estrategia.renderizar(datos);
    expect(jsx).not.toBeNull();
  });

  it('renderizar retorna mensaje vacio sin datos', () => {
    const jsx = estrategia.renderizar({ series: [] });
    expect(jsx).not.toBeNull();
  });
});

describe('EstrategiaSeveridad', () => {
  const estrategia = new EstrategiaSeveridad();

  it('getNombre retorna Severidad de Eventos', () => {
    expect(estrategia.getNombre()).toBe('Severidad de Eventos');
  });

  it('getTipo retorna SEVERIDAD', () => {
    expect(estrategia.getTipo()).toBe('SEVERIDAD');
  });

  it('procesarDatos clasifica por tipo', () => {
    const dataBruta = {
      datos: [
        { tipo: 'BASICA' },
        { tipo: 'BASICA' },
        { tipo: 'SEGURIDAD' },
        { tipo: 'COMPLETA' }
      ]
    };
    const result = estrategia.procesarDatos(dataBruta);
    expect(result.total).toBe(4);
    expect(result.distribucion).toHaveLength(3);
    expect(result.distribucion.find(d => d.key === 'BASICA').value).toBe(2);
    expect(result.distribucion.find(d => d.key === 'SEGURIDAD').value).toBe(1);
  });

  it('procesarDatos retorna distribucion vacia si no hay datos', () => {
    const result = estrategia.procesarDatos({});
    expect(result.distribucion).toEqual([]);
    expect(result.total).toBe(0);
  });
});

describe('EstrategiaTendencia', () => {
  const estrategia = new EstrategiaTendencia();

  it('getNombre retorna Tendencia Temporal', () => {
    expect(estrategia.getNombre()).toBe('Tendencia Temporal');
  });

  it('getTipo retorna TENDENCIA', () => {
    expect(estrategia.getTipo()).toBe('TENDENCIA');
  });

  it('procesarDatos transforma periodos con actual/anterior', () => {
    const dataBruta = {
      datos: [
        { periodo: '2025-W01', actual: 15, anterior: 10, variacion: 50 },
        { periodo: '2025-W02', actual: 20, anterior: 15, variacion: 33 }
      ],
      metricas: { variaciones: [50, 33] }
    };
    const result = estrategia.procesarDatos(dataBruta);
    expect(result.periodos).toHaveLength(2);
    expect(result.periodos[0].actual).toBe(15);
    expect(result.periodos[0].anterior).toBe(10);
  });

  it('procesarDatos retorna periodos vacio si no hay datos', () => {
    const result = estrategia.procesarDatos({});
    expect(result.periodos).toEqual([]);
  });
});

describe('EstrategiaComparativa', () => {
  const estrategia = new EstrategiaComparativa();

  it('getNombre retorna Ranking Comparativo', () => {
    expect(estrategia.getNombre()).toBe('Ranking Comparativo');
  });

  it('getTipo retorna COMPARATIVA', () => {
    expect(estrategia.getTipo()).toBe('COMPARATIVA');
  });

  it('procesarDatos extrae rankings de metricas', () => {
    const dataBruta = {
      datos: [{ accion: 'LOGIN', usuarioId: 1 }],
      metricas: {
        topUsuarios: [{ usuario: 'User1', total: 10 }],
        topAcciones: [{ accion: 'LOGIN', total: 20 }]
      }
    };
    const result = estrategia.procesarDatos(dataBruta);
    expect(result.topUsuarios).toHaveLength(1);
    expect(result.topAcciones).toHaveLength(1);
    expect(result.topUsuarios[0].usuario).toBe('User1');
  });

  it('procesarDatos retorna listas vacias si no hay metricas', () => {
    const result = estrategia.procesarDatos({});
    expect(result.topUsuarios).toEqual([]);
    expect(result.topAcciones).toEqual([]);
  });
});
