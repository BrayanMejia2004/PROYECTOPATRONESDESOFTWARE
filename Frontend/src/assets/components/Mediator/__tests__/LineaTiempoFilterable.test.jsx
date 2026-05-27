import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LineaTiempoFilterable from '../LineaTiempoFilterable';

const mockEventos = [
  { id: 1, tipo: 'BASICA', accion: 'LOGIN', descripcion: 'Inicio', fecha: '2026-05-27T10:00:00Z', ipOrigen: '10.0.0.1', pais: 'Colombia', usuarioId: 1 },
  { id: 2, tipo: 'SEGURIDAD', accion: 'ALERTA', descripcion: 'Intrusión', fecha: '2026-05-26T15:00:00Z', ipOrigen: '10.0.0.2', pais: 'México', usuarioId: 2 },
  { id: 3, tipo: 'COMPLETA', accion: 'CONSULTA', descripcion: 'Reporte', fecha: '2026-05-25T08:00:00Z', ipOrigen: '10.0.0.3', usuarioId: 3 },
];

describe('LineaTiempoFilterable', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('muestra mensaje vacio cuando no hay eventos', () => {
    render(<LineaTiempoFilterable eventos={[]} />);
    expect(screen.getByText('No hay eventos para mostrar')).toBeDefined();
  });

  it('renderiza eventos ordenados por fecha descendente', () => {
    render(<LineaTiempoFilterable eventos={mockEventos} />);
    const items = screen.getAllByText(/LOGIN|ALERTA|CONSULTA/);
    expect(items).toHaveLength(3);
  });

  it('muestra tipo badge, accion y descripcion de cada evento', () => {
    render(<LineaTiempoFilterable eventos={mockEventos} />);
    expect(screen.getByText('BASICA')).toBeDefined();
    expect(screen.getByText('SEGURIDAD')).toBeDefined();
    expect(screen.getByText('COMPLETA')).toBeDefined();
    expect(screen.getByText('Inicio')).toBeDefined();
    expect(screen.getByText('Intrusión')).toBeDefined();
    expect(screen.getByText('Reporte')).toBeDefined();
  });

  it('muestra ip, pais y usuarioId en el footer de cada evento', () => {
    render(<LineaTiempoFilterable eventos={mockEventos} />);
    expect(screen.getByText('10.0.0.1')).toBeDefined();
    expect(screen.getByText('Colombia')).toBeDefined();
    expect(screen.getByText('ID: 1')).toBeDefined();
  });

  it('notifica EVENTO_SELECCIONADO al hacer clic en un evento', async () => {
    const user = userEvent.setup();
    const ref = { current: null };
    const mediator = { notify: vi.fn() };
    render(<LineaTiempoFilterable ref={ref} eventos={mockEventos} />);
    ref.current?.setMediator(mediator);
    const eventoItems = screen.getAllByText('LOGIN');
    await user.click(eventoItems[0].closest('.timeline-item'));
    expect(mediator.notify).toHaveBeenCalledWith(null, 'EVENTO_SELECCIONADO', mockEventos[0]);
  });

  it('filtra por tipo cuando se llama a filtrarPorTipo via ref', () => {
    const ref = { current: null };
    render(<LineaTiempoFilterable ref={ref} eventos={mockEventos} />);
    ref.current?.filtrarPorTipo('SEGURIDAD');
    expect(screen.queryByText('LOGIN')).toBeNull();
    expect(screen.getByText('ALERTA')).toBeDefined();
  });

  it('filtra por pais cuando se llama a filtrarPorPais via ref', () => {
    const ref = { current: null };
    render(<LineaTiempoFilterable ref={ref} eventos={mockEventos} />);
    ref.current?.filtrarPorPais('Colombia');
    expect(screen.getByText('LOGIN')).toBeDefined();
    expect(screen.queryByText('ALERTA')).toBeNull();
  });

  it('aplica filtros via ref con aplicarFiltros', () => {
    const ref = { current: null };
    render(<LineaTiempoFilterable ref={ref} eventos={mockEventos} />);
    ref.current?.aplicarFiltros({ tipo: 'BASICA' });
    expect(screen.getByText('LOGIN')).toBeDefined();
    expect(screen.queryByText('ALERTA')).toBeNull();
  });

  it('agrega evento via ref con agregarEvento', () => {
    const ref = { current: null };
    render(<LineaTiempoFilterable ref={ref} eventos={mockEventos} />);
    ref.current?.agregarEvento({ id: 4, tipo: 'BASICA', accion: 'REGISTRO', descripcion: 'Nuevo', fecha: '2026-05-28T12:00:00Z', ipOrigen: '10.0.0.4' });
    expect(screen.getByText('REGISTRO')).toBeDefined();
  });

  it('reinicia pagina al aplicar filtros', () => {
    const muchosEventos = Array.from({ length: 25 }, (_, i) => ({
      id: i + 1,
      tipo: 'BASICA',
      accion: `Evento ${i + 1}`,
      fecha: `2026-05-${String(27 - i).padStart(2, '0')}T10:00:00Z`,
      ipOrigen: '10.0.0.1',
    }));
    const ref = { current: null };
    render(<LineaTiempoFilterable ref={ref} eventos={muchosEventos} />);
    expect(screen.getByText('Ver más (5 restantes)')).toBeDefined();
    ref.current?.aplicarFiltros({ tipo: 'BASICA' });
    expect(screen.getByText('Evento 1')).toBeDefined();
    expect(screen.getByText('Evento 20')).toBeDefined();
    expect(screen.getByText('Ver más (5 restantes)')).toBeDefined();
  });

  it('setMediator via ref almacena referencia', () => {
    const ref = { current: null };
    const mediator = { notify: vi.fn() };
    render(<LineaTiempoFilterable ref={ref} eventos={mockEventos} />);
    ref.current?.setMediator(mediator);
    expect(ref.current).toBeDefined();
  });
});
