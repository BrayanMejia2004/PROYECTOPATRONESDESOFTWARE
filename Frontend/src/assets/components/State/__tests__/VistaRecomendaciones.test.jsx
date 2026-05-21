import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import VistaRecomendaciones from '../VistaRecomendaciones';

const baseDatos = {
  username: 'juan.perez',
  email: 'juan@correo.com',
  nombre: 'Juan',
  apellido: 'Perez',
  fechaCreacion: '2026-04-10T09:15:00',
  totalSesiones: 24,
  ipsUtilizadas: ['192.168.1.10'],
  ultimaSesion: '2026-05-14T14:30:00',
  ultimosEventos: [],
  scoreSeguridad: 75,
};

describe('VistaRecomendaciones (State 3)', () => {

  it('renderiza titulo y subtitulo', () => {
    render(<VistaRecomendaciones datos={baseDatos} />);
    expect(screen.getByText('Recomendaciones de Seguridad')).toBeDefined();
    expect(screen.getByText(/Sugerencias personalizadas/)).toBeDefined();
  });

  it('renderiza mensaje critico cuando score < 50', () => {
    render(<VistaRecomendaciones datos={{ ...baseDatos, scoreSeguridad: 30 }} />);
    expect(screen.getByText(/Tu score de seguridad es crítico/i)).toBeDefined();
  });

  it('renderiza mensaje de advertencia cuando score entre 50 y 79', () => {
    render(<VistaRecomendaciones datos={{ ...baseDatos, scoreSeguridad: 65 }} />);
    expect(screen.getByText(/65\/100/)).toBeDefined();
  });

  it('renderiza mensaje de exito cuando score >= 80', () => {
    render(<VistaRecomendaciones datos={{ ...baseDatos, scoreSeguridad: 85 }} />);
    expect(screen.getByText(/Excelente/i)).toBeDefined();
  });

  it('sugiere completar perfil cuando falta nombre', () => {
    render(<VistaRecomendaciones datos={{ ...baseDatos, nombre: null }} />);
    expect(screen.getByText(/Completa tu perfil/i)).toBeDefined();
  });

  it('sugiere no acceder desde varias IPs cuando hay mas de 2', () => {
    render(<VistaRecomendaciones datos={{ ...baseDatos, ipsUtilizadas: ['1.1.1.1', '2.2.2.2', '3.3.3.3'] }} />);
    expect(screen.getByText(/accedido desde varias IPs/i)).toBeDefined();
  });

  it('muestra mensaje positivo cuando usa IP unica', () => {
    render(<VistaRecomendaciones datos={{ ...baseDatos, ipsUtilizadas: ['192.168.1.1'] }} />);
    expect(screen.getByText(/IP única/i)).toBeDefined();
  });

  it('muestra recomendacion de cambiar contrasena', () => {
    render(<VistaRecomendaciones datos={baseDatos} />);
    expect(screen.getByText(/Cambia tu contraseña periódicamente/i)).toBeDefined();
  });

  it('no genera recomendaciones cuando datos es null', () => {
    const { container } = render(<VistaRecomendaciones datos={null} />);
    expect(container.querySelector('.state-recomendacion')).toBeNull();
  });
});
