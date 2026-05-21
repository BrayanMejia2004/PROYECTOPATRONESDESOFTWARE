import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import EscenarioCard from '../EscenarioCard';

const escenario = {
  nombre: 'Ataque Fuerza Bruta',
  descripcion: '30 LOGIN fallidos desde misma IP en 1 minuto',
  totalEventos: 30,
  tiposEvento: ['LOGIN'],
};

const escenarioMulti = {
  nombre: 'Multiusuario',
  descripcion: '20 eventos de 5 usuarios diferentes',
  totalEventos: 20,
  tiposEvento: ['LOGIN', 'REGISTRO', 'CONSULTA', 'LOGOUT'],
};

describe('EscenarioCard', () => {
  it('renderiza nombre, descripcion y badge de eventos', () => {
    render(<EscenarioCard escenario={escenario} onEjecutar={() => {}} ejecutando={false} />);
    expect(screen.getByText('Ataque Fuerza Bruta')).toBeDefined();
    expect(screen.getByText('30 LOGIN fallidos desde misma IP en 1 minuto')).toBeDefined();
    expect(screen.getByText('30 eventos')).toBeDefined();
  });

  it('renderiza badges de tipo para cada tipoEvento', () => {
    render(<EscenarioCard escenario={escenarioMulti} onEjecutar={() => {}} ejecutando={false} />);
    expect(screen.getByText('LOGIN')).toBeDefined();
    expect(screen.getByText('REGISTRO')).toBeDefined();
    expect(screen.getByText('CONSULTA')).toBeDefined();
    expect(screen.getByText('LOGOUT')).toBeDefined();
  });

  it('boton muestra "Ejecutar Escenario" cuando no esta ejecutando', () => {
    render(<EscenarioCard escenario={escenario} onEjecutar={() => {}} ejecutando={false} />);
    expect(screen.getByText('Ejecutar Escenario')).toBeDefined();
  });

  it('boton muestra "Ejecutando..." cuando ejecutando es true', () => {
    render(<EscenarioCard escenario={escenario} onEjecutar={() => {}} ejecutando={true} />);
    expect(screen.getByText('Ejecutando...')).toBeDefined();
  });

  it('boton esta deshabilitado cuando ejecutando es true', () => {
    render(<EscenarioCard escenario={escenario} onEjecutar={() => {}} ejecutando={true} />);
    expect(screen.getByText('Ejecutando...').closest('button').disabled).toBe(true);
  });

  it('click en boton llama a onEjecutar con el escenario', async () => {
    const onEjecutar = vi.fn();
    const user = userEvent.setup();
    render(<EscenarioCard escenario={escenario} onEjecutar={onEjecutar} ejecutando={false} />);
    await user.click(screen.getByText('Ejecutar Escenario'));
    expect(onEjecutar).toHaveBeenCalledWith(escenario);
  });

  it('click no llama a onEjecutar cuando ejecutando es true', async () => {
    const onEjecutar = vi.fn();
    const user = userEvent.setup();
    render(<EscenarioCard escenario={escenario} onEjecutar={onEjecutar} ejecutando={true} />);
    await user.click(screen.getByText('Ejecutando...'));
    expect(onEjecutar).not.toHaveBeenCalled();
  });
});
