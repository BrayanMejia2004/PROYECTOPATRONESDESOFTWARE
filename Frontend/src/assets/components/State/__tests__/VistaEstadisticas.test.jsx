import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import VistaEstadisticas from '../VistaEstadisticas';

const mockDatos = {
  username: 'juan.perez',
  email: 'juan@correo.com',
  nombre: 'Juan',
  apellido: 'Perez',
  fechaCreacion: '2026-04-10T09:15:00',
  totalSesiones: 24,
  ipsUtilizadas: ['192.168.1.10', '10.0.0.5'],
  ultimaSesion: '2026-05-14T14:30:00',
  ultimosEventos: [
    { accion: 'LOGIN', descripcion: 'Inicio de sesion exitoso', fecha: '2026-05-14T14:30:00' },
    { accion: 'REGISTRO', descripcion: 'Registro de usuario', fecha: '2026-04-10T09:15:00' },
  ],
  scoreSeguridad: 75,
};

describe('VistaEstadisticas (State 1)', () => {

  it('renderiza username y metricas principales', () => {
    render(<VistaEstadisticas datos={mockDatos} />);
    expect(screen.getByText('@juan.perez')).toBeDefined();
    expect(screen.getByText('24')).toBeDefined();
    expect(screen.getByText('75')).toBeDefined();
    expect(screen.getByText('Inicios de Sesión')).toBeDefined();
    expect(screen.getByText('IPs Distintas')).toBeDefined();
    expect(screen.getByText('Score de Seguridad')).toBeDefined();
  });

  it('muestra lista de IPs', () => {
    render(<VistaEstadisticas datos={mockDatos} />);
    expect(screen.getByText('192.168.1.10')).toBeDefined();
    expect(screen.getByText('10.0.0.5')).toBeDefined();
  });

  it('muestra mensaje vacio cuando no hay IPs', () => {
    render(<VistaEstadisticas datos={{ ...mockDatos, ipsUtilizadas: [] }} />);
    expect(screen.getByText('Sin datos de IPs')).toBeDefined();
  });

  it('muestra ultimos eventos', () => {
    render(<VistaEstadisticas datos={mockDatos} />);
    expect(screen.getByText('LOGIN')).toBeDefined();
    expect(screen.getByText('REGISTRO')).toBeDefined();
    expect(screen.getByText('Inicio de sesion exitoso')).toBeDefined();
  });

  it('muestra mensaje vacio cuando no hay eventos', () => {
    render(<VistaEstadisticas datos={{ ...mockDatos, ultimosEventos: [] }} />);
    expect(screen.getByText('Sin eventos recientes')).toBeDefined();
  });

  it('renderiza informacion de la cuenta', () => {
    render(<VistaEstadisticas datos={mockDatos} />);
    expect(screen.getByText('@juan.perez')).toBeDefined();
    expect(screen.getByText('juan@correo.com')).toBeDefined();
  });

  it('no renderiza nada cuando datos es null', () => {
    const { container } = render(<VistaEstadisticas datos={null} />);
    expect(container.innerHTML).toBe('');
  });

  it('muestra score color verde cuando >= 80', () => {
    render(<VistaEstadisticas datos={{ ...mockDatos, scoreSeguridad: 85 }} />);
    expect(screen.getByText('Seguro')).toBeDefined();
  });

  it('muestra score color rojo cuando < 50', () => {
    render(<VistaEstadisticas datos={{ ...mockDatos, scoreSeguridad: 30 }} />);
    expect(screen.getByText('Crítico')).toBeDefined();
  });
});
