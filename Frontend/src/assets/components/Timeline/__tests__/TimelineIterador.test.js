import { describe, it, expect, vi } from 'vitest';
import { TimelineIterador, CronologicoIterador, InversoIterador, FiltroIterador } from '../TimelineIterador';

const mockObtenerFn = vi.fn();

const eventosMock = [
  { id: 1, tipo: 'BASICA', accion: 'LOGIN', fecha: '2026-05-01T10:00:00Z' },
  { id: 2, tipo: 'SEGURIDAD', accion: 'ALERTA', fecha: '2026-05-02T10:00:00Z' },
  { id: 3, tipo: 'COMPLETA', accion: 'LOGOUT', fecha: '2026-05-03T10:00:00Z' },
];

describe('TimelineIterador (base)', () => {
  it('constructor establece valores por defecto', () => {
    const it = new TimelineIterador(1, mockObtenerFn);
    expect(it.usuarioId).toBe(1);
    expect(it.obtenerFn).toBe(mockObtenerFn);
    expect(it._eventos).toEqual([]);
    expect(it._limite).toBe(50);
  });

  it('get eventos retorna _eventos', () => {
    const it = new TimelineIterador(1, mockObtenerFn);
    it._eventos = eventosMock;
    expect(it.eventos).toBe(it._eventos);
    expect(it.eventos).toHaveLength(3);
  });

  it('get limite retorna _limite', () => {
    const it = new TimelineIterador(1, mockObtenerFn);
    expect(it.limite).toBe(50);
  });

  it('cargar llama a obtenerFn con usuarioId y limite', async () => {
    mockObtenerFn.mockResolvedValue({ success: true, data: eventosMock });
    const it = new TimelineIterador(1, mockObtenerFn);
    const result = await it.cargar();
    expect(mockObtenerFn).toHaveBeenCalledWith(1, 50);
    expect(result.success).toBe(true);
    expect(it._eventos).toEqual(eventosMock);
  });

  it('cargar asigna array vacio si result.data es null', async () => {
    mockObtenerFn.mockResolvedValue({ success: true, data: null });
    const it = new TimelineIterador(1, mockObtenerFn);
    await it.cargar();
    expect(it._eventos).toEqual([]);
  });

  it('cargar no modifica _eventos si success es false', async () => {
    mockObtenerFn.mockResolvedValue({ success: false, data: null, message: 'Error' });
    const it = new TimelineIterador(1, mockObtenerFn);
    it._eventos = [{ id: 0 }];
    await it.cargar();
    expect(it._eventos).toEqual([{ id: 0 }]);
  });

  it('cargarMas incrementa limite en 50 y recarga', async () => {
    mockObtenerFn.mockResolvedValue({ success: true, data: eventosMock });
    const it = new TimelineIterador(1, mockObtenerFn);
    it._eventos = eventosMock.slice(0, 2);
    it._limite = 50;
    await it.cargarMas();
    expect(it._limite).toBe(100);
    expect(mockObtenerFn).toHaveBeenCalledWith(1, 100);
  });

  it('hayMas retorna true cuando _eventos.length >= _limite', () => {
    const it = new TimelineIterador(1, mockObtenerFn);
    it._eventos = new Array(50);
    it._limite = 50;
    expect(it.hayMas()).toBe(true);
  });

  it('hayMas retorna false cuando _eventos.length < _limite', () => {
    const it = new TimelineIterador(1, mockObtenerFn);
    it._eventos = new Array(30);
    it._limite = 50;
    expect(it.hayMas()).toBe(false);
  });

  it('reiniciar vuelve a valores iniciales', () => {
    const it = new TimelineIterador(1, mockObtenerFn);
    it._eventos = eventosMock;
    it._limite = 200;
    it.reiniciar();
    expect(it._limite).toBe(50);
    expect(it._eventos).toEqual([]);
  });
});

describe('CronologicoIterador', () => {
  it('extiende TimelineIterador y retorna eventos en orden original', () => {
    const it = new CronologicoIterador(1, mockObtenerFn);
    expect(it).toBeInstanceOf(TimelineIterador);
    expect(it).toBeInstanceOf(CronologicoIterador);
    it._eventos = eventosMock;
    expect(it.eventos).toEqual(eventosMock);
  });
});

describe('InversoIterador', () => {
  it('extiende TimelineIterador y retorna eventos en orden inverso', () => {
    const it = new InversoIterador(1, mockObtenerFn);
    expect(it).toBeInstanceOf(TimelineIterador);
    expect(it).toBeInstanceOf(InversoIterador);
    it._eventos = eventosMock;
    const esperado = [...eventosMock].reverse();
    expect(it.eventos).toEqual(esperado);
  });

  it('no muta el array original al invertir', () => {
    const it = new InversoIterador(1, mockObtenerFn);
    it._eventos = eventosMock;
    const copiaOriginal = [...eventosMock];
    it.eventos;
    expect(it._eventos).toEqual(copiaOriginal);
  });
});

describe('FiltroIterador', () => {
  it('extiende TimelineIterador y acepta filtroFn en constructor', () => {
    const filtroFn = (e) => e.tipo === 'SEGURIDAD';
    const it = new FiltroIterador(1, mockObtenerFn, filtroFn);
    expect(it).toBeInstanceOf(TimelineIterador);
    expect(it).toBeInstanceOf(FiltroIterador);
    expect(it.filtroFn).toBe(filtroFn);
  });

  it('filtra eventos segun filtroFn', () => {
    const it = new FiltroIterador(1, mockObtenerFn, (e) => e.tipo === 'SEGURIDAD');
    it._eventos = eventosMock;
    expect(it.eventos).toHaveLength(1);
    expect(it.eventos[0].id).toBe(2);
  });

  it('retorna todos los eventos si filtroFn es null', () => {
    const it = new FiltroIterador(1, mockObtenerFn, null);
    it._eventos = eventosMock;
    expect(it.eventos).toHaveLength(3);
  });

  it('retorna todos los eventos si filtroFn es undefined', () => {
    const it = new FiltroIterador(1, mockObtenerFn);
    it._eventos = eventosMock;
    expect(it.eventos).toHaveLength(3);
  });

  it('retorna array vacio si ningun evento cumple filtro', () => {
    const it = new FiltroIterador(1, mockObtenerFn, () => false);
    it._eventos = eventosMock;
    expect(it.eventos).toEqual([]);
  });
});
