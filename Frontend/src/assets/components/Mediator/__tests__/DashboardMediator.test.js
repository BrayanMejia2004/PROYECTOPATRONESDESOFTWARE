import { describe, it, expect, vi, beforeEach } from 'vitest';
import DashboardMediator, { DashboardComponent } from '../DashboardMediator';

describe('DashboardMediator', () => {
  let mediator;

  beforeEach(() => {
    mediator = new DashboardMediator();
  });

  it('register almacena un componente por nombre', () => {
    const comp = { setMediator: vi.fn() };
    mediator.register('mapaGlobal', comp);
    expect(mediator.components.mapaGlobal).toBe(comp);
  });

  it('register llama a setMediator en el componente', () => {
    const comp = { setMediator: vi.fn() };
    mediator.register('test', comp);
    expect(comp.setMediator).toHaveBeenCalledWith(mediator);
  });

  it('register no falla si componente no tiene setMediator', () => {
    const comp = {};
    mediator.register('test', comp);
    expect(mediator.components.test).toBe(comp);
  });

  it('register no falla si componente es null', () => {
    mediator.register('test', null);
    expect(mediator.components.test).toBeNull();
  });

  it('FILTROS_CAMBIADOS llama a aplicarFiltros en mapaGlobal, lineaTiempo y contadores', () => {
    const mapaGlobal = { aplicarFiltros: vi.fn() };
    const lineaTiempo = { aplicarFiltros: vi.fn() };
    const contadores = { aplicarFiltros: vi.fn() };
    mediator.register('mapaGlobal', mapaGlobal);
    mediator.register('lineaTiempo', lineaTiempo);
    mediator.register('contadores', contadores);
    const filtros = { tipo: 'SEGURIDAD' };
    mediator.notify(null, 'FILTROS_CAMBIADOS', filtros);
    expect(mapaGlobal.aplicarFiltros).toHaveBeenCalledWith(filtros);
    expect(lineaTiempo.aplicarFiltros).toHaveBeenCalledWith(filtros);
    expect(contadores.aplicarFiltros).toHaveBeenCalledWith(filtros);
  });

  it('EVENTO_SELECCIONADO llama a enfocarEvento en mapaGlobal', () => {
    const mapaGlobal = { enfocarEvento: vi.fn() };
    mediator.register('mapaGlobal', mapaGlobal);
    const evento = { id: 1, latitud: 10, longitud: -70 };
    mediator.notify(null, 'EVENTO_SELECCIONADO', evento);
    expect(mapaGlobal.enfocarEvento).toHaveBeenCalledWith(evento);
  });

  it('PAIS_SELECCIONADO llama a filtrarPorPais en lineaTiempo y contadores', () => {
    const lineaTiempo = { filtrarPorPais: vi.fn() };
    const contadores = { filtrarPorPais: vi.fn() };
    mediator.register('lineaTiempo', lineaTiempo);
    mediator.register('contadores', contadores);
    mediator.notify(null, 'PAIS_SELECCIONADO', 'Colombia');
    expect(lineaTiempo.filtrarPorPais).toHaveBeenCalledWith('Colombia');
    expect(contadores.filtrarPorPais).toHaveBeenCalledWith('Colombia');
  });

  it('WEBSOCKET_EVENTO llama a agregarEvento e incrementar en todos', () => {
    const mapaGlobal = { agregarEvento: vi.fn() };
    const lineaTiempo = { agregarEvento: vi.fn() };
    const contadores = { incrementar: vi.fn() };
    mediator.register('mapaGlobal', mapaGlobal);
    mediator.register('lineaTiempo', lineaTiempo);
    mediator.register('contadores', contadores);
    const evento = { id: 1, tipo: 'BASICA' };
    mediator.notify(null, 'WEBSOCKET_EVENTO', evento);
    expect(mapaGlobal.agregarEvento).toHaveBeenCalledWith(evento);
    expect(lineaTiempo.agregarEvento).toHaveBeenCalledWith(evento);
    expect(contadores.incrementar).toHaveBeenCalledWith(evento);
  });

  it('TIPO_CAMBIADO llama a filtrarPorTipo en mapaGlobal y lineaTiempo', () => {
    const mapaGlobal = { filtrarPorTipo: vi.fn() };
    const lineaTiempo = { filtrarPorTipo: vi.fn() };
    mediator.register('mapaGlobal', mapaGlobal);
    mediator.register('lineaTiempo', lineaTiempo);
    mediator.notify(null, 'TIPO_CAMBIADO', 'COMPLETA');
    expect(mapaGlobal.filtrarPorTipo).toHaveBeenCalledWith('COMPLETA');
    expect(lineaTiempo.filtrarPorTipo).toHaveBeenCalledWith('COMPLETA');
  });

  it('notify no falla si un componente no esta registrado', () => {
    mediator.notify(null, 'FILTROS_CAMBIADOS', {});
    mediator.notify(null, 'EVENTO_SELECCIONADO', {});
    mediator.notify(null, 'WEBSOCKET_EVENTO', {});
  });

  it('notify con evento desconocido no hace nada', () => {
    const mapaGlobal = { aplicarFiltros: vi.fn() };
    mediator.register('mapaGlobal', mapaGlobal);
    mediator.notify(null, 'EVENTO_INEXISTENTE', {});
    expect(mapaGlobal.aplicarFiltros).not.toHaveBeenCalled();
  });

  it('sender se ignora (no afecta la logica)', () => {
    const comp = { aplicarFiltros: vi.fn() };
    mediator.register('mapaGlobal', comp);
    mediator.notify('cualquierSender', 'FILTROS_CAMBIADOS', {});
    expect(comp.aplicarFiltros).toHaveBeenCalled();
  });
});

describe('DashboardComponent', () => {
  it('setMediator almacena el mediator', () => {
    const comp = new DashboardComponent();
    const mediator = { notify: vi.fn() };
    comp.setMediator(mediator);
    expect(comp.mediator).toBe(mediator);
  });

  it('notifyMediator llama a mediator.notify con sender, event y data', () => {
    const comp = new DashboardComponent();
    const mediator = { notify: vi.fn() };
    comp.setMediator(mediator);
    comp.notifyMediator('EVENTO_TEST', { foo: 'bar' });
    expect(mediator.notify).toHaveBeenCalledWith(comp, 'EVENTO_TEST', { foo: 'bar' });
  });

  it('notifyMediator no falla si no hay mediator', () => {
    const comp = new DashboardComponent();
    expect(() => comp.notifyMediator('TEST', {})).not.toThrow();
  });
});
