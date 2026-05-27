import { describe, it, expect, vi, beforeEach } from 'vitest';
import { DashboardMemento, DashboardOriginator, dashboardCaretaker } from '../DashboardMemento';

describe('DashboardMemento', () => {
  it('constructor asigna data y timestamp correctamente', () => {
    const data = { eventosHoy: 10, eventosMes: 100 };
    const ts = 1234567890;
    const memento = new DashboardMemento(data, ts);
    expect(memento.getData()).toBe(data);
    expect(memento.getTimestamp()).toBe(ts);
  });

  it('getData retorna el mismo objeto que se paso al constructor', () => {
    const data = { foo: 'bar' };
    const memento = new DashboardMemento(data, 100);
    expect(memento.getData()).toEqual({ foo: 'bar' });
  });

  it('getTimestamp retorna el timestamp exacto', () => {
    const memento = new DashboardMemento({}, 999);
    expect(memento.getTimestamp()).toBe(999);
  });
});

describe('DashboardOriginator', () => {
  it('guardar crea un DashboardMemento con Date.now()', () => {
    const now = 5000000;
    vi.spyOn(Date, 'now').mockReturnValue(now);
    const originator = new DashboardOriginator();
    const data = { eventosHoy: 5 };
    const memento = originator.guardar(data);
    expect(memento).toBeInstanceOf(DashboardMemento);
    expect(memento.getTimestamp()).toBe(now);
  });

  it('guardar realiza una copia profunda (deep clone) de los datos', () => {
    vi.spyOn(Date, 'now').mockReturnValue(0);
    const originator = new DashboardOriginator();
    const data = { eventosHoy: 5, anidado: { foo: 'bar' } };
    const memento = originator.guardar(data);
    expect(memento.getData()).toEqual(data);
    expect(memento.getData()).not.toBe(data);
    data.eventosHoy = 999;
    expect(memento.getData().eventosHoy).toBe(5);
  });

  it('guardar funciona con arrays y valores primitivos dentro del objeto', () => {
    vi.spyOn(Date, 'now').mockReturnValue(0);
    const originator = new DashboardOriginator();
    const data = { items: [1, 2, { x: 10 }] };
    const memento = originator.guardar(data);
    expect(memento.getData()).toEqual(data);
    expect(memento.getData().items).not.toBe(data.items);
  });

  it('restaurar retorna los datos del memento', () => {
    const originator = new DashboardOriginator();
    const data = { eventosHoy: 10 };
    const memento = new DashboardMemento(data, 100);
    const resultado = originator.restaurar(memento);
    expect(resultado).toBe(data);
  });

  it('restaurar retorna null cuando recibe null', () => {
    const originator = new DashboardOriginator();
    expect(originator.restaurar(null)).toBeNull();
  });

  it('restaurar retorna null cuando recibe undefined', () => {
    const originator = new DashboardOriginator();
    expect(originator.restaurar(undefined)).toBeNull();
  });
});

describe('DashboardCaretaker', () => {
  beforeEach(() => {
    dashboardCaretaker.limpiar();
  });

  it('guardar almacena el memento', () => {
    const memento = new DashboardMemento({ foo: 'bar' }, 100);
    dashboardCaretaker.guardar(memento);
    expect(dashboardCaretaker.recuperar()).toBe(memento);
  });

  it('recuperar retorna null si no se ha guardado nada', () => {
    expect(dashboardCaretaker.recuperar()).toBeNull();
  });

  it('recuperar retorna null despues de limpiar', () => {
    const memento = new DashboardMemento({}, 100);
    dashboardCaretaker.guardar(memento);
    dashboardCaretaker.limpiar();
    expect(dashboardCaretaker.recuperar()).toBeNull();
  });

  it('guardar reemplaza el memento anterior', () => {
    const m1 = new DashboardMemento({ a: 1 }, 100);
    const m2 = new DashboardMemento({ b: 2 }, 200);
    dashboardCaretaker.guardar(m1);
    dashboardCaretaker.guardar(m2);
    expect(dashboardCaretaker.recuperar()).toBe(m2);
  });

  describe('hayMementoValido', () => {
    it('retorna true cuando el memento no ha expirado', () => {
      const ahora = Date.now();
      vi.spyOn(Date, 'now').mockReturnValue(ahora);
      const memento = new DashboardMemento({}, ahora);
      dashboardCaretaker.guardar(memento);
      vi.spyOn(Date, 'now').mockReturnValue(ahora + 5000);
      expect(dashboardCaretaker.hayMementoValido(30)).toBe(true);
    });

    it('retorna false cuando el memento ha expirado', () => {
      const ahora = Date.now();
      vi.spyOn(Date, 'now').mockReturnValue(ahora);
      const memento = new DashboardMemento({}, ahora);
      dashboardCaretaker.guardar(memento);
      vi.spyOn(Date, 'now').mockReturnValue(ahora + 31000);
      expect(dashboardCaretaker.hayMementoValido(30)).toBe(false);
    });

    it('retorna false cuando no hay memento', () => {
      vi.spyOn(Date, 'now').mockReturnValue(0);
      expect(dashboardCaretaker.hayMementoValido(30)).toBe(false);
    });

    it('respeta el parametro de segundos personalizado', () => {
      const ahora = Date.now();
      vi.spyOn(Date, 'now').mockReturnValue(ahora);
      const memento = new DashboardMemento({}, ahora);
      dashboardCaretaker.guardar(memento);
      vi.spyOn(Date, 'now').mockReturnValue(ahora + 5000);
      expect(dashboardCaretaker.hayMementoValido(3)).toBe(false);
      expect(dashboardCaretaker.hayMementoValido(10)).toBe(true);
    });

    it('retorna true exactamente en el limite de segundos (borde inferior)', () => {
      const ahora = Date.now();
      vi.spyOn(Date, 'now').mockReturnValue(ahora);
      const memento = new DashboardMemento({}, ahora);
      dashboardCaretaker.guardar(memento);
      vi.spyOn(Date, 'now').mockReturnValue(ahora + 29999);
      expect(dashboardCaretaker.hayMementoValido(30)).toBe(true);
    });

    it('retorna false justo despues del limite de segundos', () => {
      const ahora = Date.now();
      vi.spyOn(Date, 'now').mockReturnValue(ahora);
      const memento = new DashboardMemento({}, ahora);
      dashboardCaretaker.guardar(memento);
      vi.spyOn(Date, 'now').mockReturnValue(ahora + 30001);
      expect(dashboardCaretaker.hayMementoValido(30)).toBe(false);
    });

    it('lanza error si recibe timestamp string (la comparacion falla)', () => {
      const ahora = Date.now();
      vi.spyOn(Date, 'now').mockReturnValue(ahora);
      const memento = new DashboardMemento({}, ahora);
      dashboardCaretaker.guardar(memento);
      dashboardCaretaker.limpiar();
      expect(dashboardCaretaker.hayMementoValido()).toBe(false);
    });
  });
});
