import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import VistaAlertas from '../VistaAlertas';

describe('VistaAlertas (State 2)', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('renderiza los 4 toggles con sus labels', () => {
    render(<VistaAlertas />);
    expect(screen.getByText('Notificarme cuando inicie sesión desde una IP nueva')).toBeDefined();
    expect(screen.getByText('Notificarme cuando haya más de 3 intentos fallidos')).toBeDefined();
    expect(screen.getByText('Notificarme cuando mi cuenta sea accedida fuera de horario')).toBeDefined();
    expect(screen.getByText('Resumen semanal de actividad')).toBeDefined();
  });

  it('renderiza el titulo y el mensaje informativo', () => {
    render(<VistaAlertas />);
    expect(screen.getByText('Alertas Personalizadas')).toBeDefined();
    expect(screen.getByText(/Las alertas se guardan localmente/)).toBeDefined();
  });

  it('toggles comienzan desactivados', () => {
    render(<VistaAlertas />);
    const checkboxes = screen.getAllByRole('checkbox');
    expect(checkboxes).toHaveLength(4);
    checkboxes.forEach(cb => {
      expect(cb.checked).toBe(false);
    });
  });

  it('toggle se activa al hacer clic', async () => {
    const user = userEvent.setup();
    render(<VistaAlertas />);
    const checkbox = screen.getAllByRole('checkbox')[0];
    expect(checkbox.checked).toBe(false);
    await user.click(checkbox);
    expect(checkbox.checked).toBe(true);
  });

  it('toggle se desactiva al hacer clic dos veces', async () => {
    const user = userEvent.setup();
    render(<VistaAlertas />);
    const checkbox = screen.getAllByRole('checkbox')[0];
    await user.click(checkbox);
    expect(checkbox.checked).toBe(true);
    await user.click(checkbox);
    expect(checkbox.checked).toBe(false);
  });

  it('persiste en localStorage al cambiar toggle', async () => {
    const user = userEvent.setup();
    render(<VistaAlertas />);
    await user.click(screen.getAllByRole('checkbox')[0]);
    const saved = JSON.parse(localStorage.getItem('mi-actividad-alertas'));
    expect(saved.nueva_ip).toBe(true);
  });

  it('carga estado inicial desde localStorage', () => {
    localStorage.setItem('mi-actividad-alertas', JSON.stringify({ nueva_ip: true, intentos_fallidos: false, horario_atipico: true, resumen_semanal: false }));
    render(<VistaAlertas />);
    const checkboxes = screen.getAllByRole('checkbox');
    expect(checkboxes[0].checked).toBe(true);
    expect(checkboxes[2].checked).toBe(true);
  });

  it('muestra indicador Guardado al cambiar toggle', async () => {
    const user = userEvent.setup();
    render(<VistaAlertas />);
    await user.click(screen.getAllByRole('checkbox')[0]);
    expect(screen.getByText('Guardado')).toBeDefined();
  });
});
