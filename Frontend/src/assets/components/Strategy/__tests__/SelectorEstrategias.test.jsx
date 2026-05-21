import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import SelectorEstrategias from '../SelectorEstrategias';

describe('SelectorEstrategias', () => {
  const defaultProps = {
    estrategiaActiva: null,
    onSeleccionar: vi.fn()
  };

  it('renderiza las 4 tarjetas de estrategia', () => {
    render(<SelectorEstrategias {...defaultProps} />);
    expect(screen.getByText('Volumen de Actividad')).toBeTruthy();
    expect(screen.getByText('Severidad de Eventos')).toBeTruthy();
    expect(screen.getByText('Tendencia Temporal')).toBeTruthy();
    expect(screen.getByText('Ranking Comparativo')).toBeTruthy();
  });

  it('ninguna tarjeta tiene clase activa si no se pasa estrategiaActiva', () => {
    const { container } = render(<SelectorEstrategias {...defaultProps} />);
    const cards = container.querySelectorAll('.selector-card');
    cards.forEach(card => {
      expect(card.classList.contains('activa')).toBe(false);
    });
  });

  it('la tarjeta activa tiene clase "activa"', () => {
    const { container } = render(
      <SelectorEstrategias
        estrategiaActiva="VOLUMEN"
        onSeleccionar={vi.fn()}
      />
    );
    const cards = container.querySelectorAll('.selector-card');
    const activa = Array.from(cards).find(c => c.classList.contains('activa'));
    expect(activa).toBeTruthy();
    expect(activa.textContent).toContain('Volumen de Actividad');
  });

  it('llama a onSeleccionar con el tipo al hacer clic', () => {
    const onSeleccionar = vi.fn();
    render(
      <SelectorEstrategias
        estrategiaActiva={null}
        onSeleccionar={onSeleccionar}
      />
    );
    fireEvent.click(screen.getByText('Tendencia Temporal'));
    expect(onSeleccionar).toHaveBeenCalledWith('TENDENCIA');
  });

  it('no marca otras tarjetas como activas', () => {
    const { container } = render(
      <SelectorEstrategias
        estrategiaActiva="SEVERIDAD"
        onSeleccionar={vi.fn()}
      />
    );
    const cards = container.querySelectorAll('.selector-card.activa');
    expect(cards).toHaveLength(1);
    expect(cards[0].textContent).toContain('Severidad de Eventos');
  });
});
