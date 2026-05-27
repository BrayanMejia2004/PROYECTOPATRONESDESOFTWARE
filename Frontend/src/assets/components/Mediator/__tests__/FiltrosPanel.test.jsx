import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import FiltrosPanel from '../FiltrosPanel';

describe('FiltrosPanel', () => {
  it('renderiza inputs de fecha, tipo, pais y usuarioId', () => {
    render(<FiltrosPanel mediator={null} onFiltrosCambiados={() => {}} />);
    expect(screen.getByLabelText('Fecha desde')).toBeDefined();
    expect(screen.getByLabelText('Fecha hasta')).toBeDefined();
    expect(screen.getByLabelText('Tipo')).toBeDefined();
    expect(screen.getByLabelText('País')).toBeDefined();
    expect(screen.getByLabelText('Usuario ID')).toBeDefined();
  });

  it('renderiza botones Aplicar y Limpiar', () => {
    render(<FiltrosPanel mediator={null} onFiltrosCambiados={() => {}} />);
    expect(screen.getByText('Aplicar')).toBeDefined();
    expect(screen.getByText('Limpiar')).toBeDefined();
  });

  it('renderiza opciones de tipo en el select', () => {
    render(<FiltrosPanel mediator={null} onFiltrosCambiados={() => {}} />);
    expect(screen.getByText('Todos')).toBeDefined();
    expect(screen.getByText('BASICA')).toBeDefined();
    expect(screen.getByText('COMPLETA')).toBeDefined();
    expect(screen.getByText('SEGURIDAD')).toBeDefined();
  });

  it('llama a mediator.notify y onFiltrosCambiados al hacer clic en Aplicar', async () => {
    const user = userEvent.setup();
    const mediator = { notify: vi.fn() };
    const onFiltrosCambiados = vi.fn();
    render(<FiltrosPanel mediator={mediator} onFiltrosCambiados={onFiltrosCambiados} />);
    await user.click(screen.getByText('Aplicar'));
    expect(mediator.notify).toHaveBeenCalledWith(null, 'FILTROS_CAMBIADOS', {});
    expect(onFiltrosCambiados).toHaveBeenCalledWith({});
  });

  it('llama a mediator.notify y onFiltrosCambiados con filtros activos al aplicar con valores', async () => {
    const user = userEvent.setup();
    const mediator = { notify: vi.fn() };
    const onFiltrosCambiados = vi.fn();
    render(<FiltrosPanel mediator={mediator} onFiltrosCambiados={onFiltrosCambiados} />);
    const paisInput = screen.getByLabelText('País');
    await user.type(paisInput, 'Colombia');
    await user.click(screen.getByText('Aplicar'));
    expect(mediator.notify).toHaveBeenCalledWith(null, 'FILTROS_CAMBIADOS', { pais: 'Colombia' });
    expect(onFiltrosCambiados).toHaveBeenCalledWith({ pais: 'Colombia' });
  });

  it('llama a mediator.notify y onFiltrosCambiados con objeto vacio al limpiar', async () => {
    const user = userEvent.setup();
    const mediator = { notify: vi.fn() };
    const onFiltrosCambiados = vi.fn();
    render(<FiltrosPanel mediator={mediator} onFiltrosCambiados={onFiltrosCambiados} />);
    await user.click(screen.getByText('Limpiar'));
    expect(mediator.notify).toHaveBeenCalledWith(null, 'FILTROS_CAMBIADOS', {});
    expect(onFiltrosCambiados).toHaveBeenCalledWith({});
  });

  it('no falla si mediator es null', async () => {
    const user = userEvent.setup();
    render(<FiltrosPanel mediator={null} onFiltrosCambiados={() => {}} />);
    await user.click(screen.getByText('Aplicar'));
    await user.click(screen.getByText('Limpiar'));
  });

  it('no falla si onFiltrosCambiados es undefined', async () => {
    const user = userEvent.setup();
    render(<FiltrosPanel mediator={null} />);
    await user.click(screen.getByText('Aplicar'));
  });

  it('resetea los inputs al hacer clic en Limpiar', async () => {
    const user = userEvent.setup();
    render(<FiltrosPanel mediator={null} onFiltrosCambiados={() => {}} />);
    const paisInput = screen.getByLabelText('País');
    await user.type(paisInput, 'Colombia');
    expect(paisInput.value).toBe('Colombia');
    await user.click(screen.getByText('Limpiar'));
    expect(paisInput.value).toBe('');
  });
});
