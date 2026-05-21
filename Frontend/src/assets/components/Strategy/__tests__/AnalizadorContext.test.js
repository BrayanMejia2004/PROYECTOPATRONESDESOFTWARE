import { describe, it, expect, beforeEach, vi } from 'vitest';
import { AnalizadorContext, obtenerEstrategiasInfo } from '../AnalizadorContext';

const mockAxiosPost = vi.fn();
vi.mock('../../../Services/analisis/analisisService', () => ({
  obtenerAnalisis: (...args) => mockAxiosPost(...args)
}));

describe('obtenerEstrategiasInfo', () => {
  it('retorna las 4 estrategias registradas', () => {
    const info = obtenerEstrategiasInfo();
    expect(info).toHaveLength(4);
    const tipos = info.map(i => i.tipo);
    expect(tipos).toContain('VOLUMEN');
    expect(tipos).toContain('SEVERIDAD');
    expect(tipos).toContain('TENDENCIA');
    expect(tipos).toContain('COMPARATIVA');
  });

  it('cada estrategia tiene nombre, descripcion e icono', () => {
    const info = obtenerEstrategiasInfo();
    info.forEach(e => {
      expect(e.nombre).toBeTruthy();
      expect(e.descripcion).toBeTruthy();
      expect(e.icono).toBeTruthy();
    });
  });
});

describe('AnalizadorContext', () => {
  let context;

  beforeEach(() => {
    context = new AnalizadorContext();
    vi.clearAllMocks();
  });

  it('constructor inicia sin estrategia', () => {
    expect(context.getEstrategia()).toBeNull();
    expect(context.getEstrategiaTipo()).toBeNull();
  });

  it('setEstrategia cambia la estrategia activa', () => {
    context.setEstrategia('VOLUMEN');
    expect(context.getEstrategia()).not.toBeNull();
    expect(context.getEstrategiaTipo()).toBe('VOLUMEN');
  });

  it('setEstrategia con tipo invalido no cambia nada', () => {
    context.setEstrategia('INVALIDA');
    expect(context.getEstrategia()).toBeNull();
  });

  it('setEstrategia notifica a listeners suscritos', () => {
    const listener = vi.fn();
    context.subscribe(listener);
    context.setEstrategia('SEVERIDAD');
    expect(listener).toHaveBeenCalledTimes(1);
  });

  it('ejecutarAnalisis procesa datos con la estrategia activa', async () => {
    context.setEstrategia('VOLUMEN');
    const dataBruta = {
      datos: [{ fecha: '2025-01-01', total: 10 }],
      metricas: { total: 10 }
    };
    const result = await context.ejecutarAnalisis(dataBruta);
    expect(result).not.toBeNull();
    expect(result.estrategia.getTipo()).toBe('VOLUMEN');
    expect(result.datos.series).toHaveLength(1);
    expect(result.vista).not.toBeNull();
  });

  it('ejecutarAnalisis retorna null si no hay estrategia activa', async () => {
    const result = await context.ejecutarAnalisis({});
    expect(result).toBeNull();
  });

  it('ejecutarAnalisis con estrategia activa automaticamente al setear', () => {
    context.datosBrutos = { datos: [{ fecha: '2025-01-01', total: 10 }], metricas: {} };
    context.setEstrategia('TENDENCIA');
    expect(context.getEstrategiaTipo()).toBe('TENDENCIA');
  });

  it('getResultado retorna null inicialmente', () => {
    expect(context.getResultado()).toBeNull();
  });

  it('getResultado retorna resultado tras ejecutarAnalisis', async () => {
    context.setEstrategia('COMPARATIVA');
    await context.ejecutarAnalisis({
      datos: [],
      metricas: { topUsuarios: [], topAcciones: [] }
    });
    expect(context.getResultado()).not.toBeNull();
  });

  it('subscribe retorna funcion para desuscribirse', () => {
    const listener = vi.fn();
    const unsub = context.subscribe(listener);
    expect(typeof unsub).toBe('function');
    context.setEstrategia('VOLUMEN');
    expect(listener).toHaveBeenCalledTimes(1);
    unsub();
    context.setEstrategia('SEVERIDAD');
    expect(listener).toHaveBeenCalledTimes(1);
  });
});
