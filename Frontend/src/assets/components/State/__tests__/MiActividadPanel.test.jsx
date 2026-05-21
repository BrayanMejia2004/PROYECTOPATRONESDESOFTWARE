import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import MiActividadPanel from '../MiActividadPanel';

const { mockDatos, mockResolve } = vi.hoisted(() => {
  const mockDatos = {
    username: 'juan.perez',
    email: 'juan@correo.com',
    nombre: 'Juan',
    apellido: 'Perez',
    fechaCreacion: '2026-04-10T09:15:00',
    totalSesiones: 24,
    ipsUtilizadas: ['192.168.1.10'],
    ultimaSesion: '2026-05-14T14:30:00',
    ultimosEventos: [{ accion: 'LOGIN', descripcion: 'Inicio de sesion exitoso', fecha: '2026-05-14T14:30:00' }],
    scoreSeguridad: 75,
  };
  return {
    mockDatos,
    mockResolve: vi.fn().mockResolvedValue({ success: true, data: mockDatos }),
  };
});

vi.mock('../../../Services/miActividadService', () => ({
  obtenerMiActividad: mockResolve,
}));

describe('MiActividadPanel (Context - State Pattern)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renderiza los 3 botones de navegacion de estado', async () => {
    render(<MiActividadPanel />);
    await waitFor(() => {
      expect(screen.getByText('Mis Estadisticas')).toBeDefined();
      expect(screen.getByText('Mis Alertas')).toBeDefined();
      expect(screen.getByText('Recomendaciones')).toBeDefined();
    });
  });

  it('estado inicial es ESTADISTICAS', async () => {
    render(<MiActividadPanel />);
    await waitFor(() => {
      expect(screen.getByText('@juan.perez')).toBeDefined();
      expect(screen.getByText('Score de Seguridad')).toBeDefined();
    });
  });

  it('boton ESTADISTICAS tiene clase active al inicio', async () => {
    render(<MiActividadPanel />);
    await waitFor(() => {
      const btn = screen.getByText('Mis Estadisticas');
      expect(btn.classList.contains('active')).toBe(true);
    });
  });

  it('cambiar a ALERTAS muestra VistaAlertas con toggles', async () => {
    const user = userEvent.setup();
    render(<MiActividadPanel />);
    await waitFor(() => {
      expect(screen.getByText('Mis Estadisticas')).toBeDefined();
    });
    await user.click(screen.getByText('Mis Alertas'));
    expect(screen.getByText('Alertas Personalizadas')).toBeDefined();
    expect(screen.getAllByRole('checkbox')).toHaveLength(4);
  });

  it('cambiar a RECOMENDACIONES muestra recomendaciones', async () => {
    const user = userEvent.setup();
    render(<MiActividadPanel />);
    await waitFor(() => {
      expect(screen.getByText('Mis Estadisticas')).toBeDefined();
    });
    await user.click(screen.getByText('Recomendaciones'));
    expect(screen.getByText('Recomendaciones de Seguridad')).toBeDefined();
  });

  it('navegacion circular: ESTADISTICAS -> ALERTAS -> RECOMENDACIONES -> ESTADISTICAS', async () => {
    const user = userEvent.setup();
    render(<MiActividadPanel />);
    await waitFor(() => {
      expect(screen.getByText('Score de Seguridad')).toBeDefined();
    });
    await user.click(screen.getByText('Mis Alertas'));
    expect(screen.getByText('Alertas Personalizadas')).toBeDefined();
    await user.click(screen.getByText('Recomendaciones'));
    expect(screen.getByText('Recomendaciones de Seguridad')).toBeDefined();
    await user.click(screen.getByText('Mis Estadisticas'));
    expect(screen.getByText('Score de Seguridad')).toBeDefined();
  });

  it('el boton activo cambia al hacer clic en otro estado', async () => {
    const user = userEvent.setup();
    render(<MiActividadPanel />);
    await waitFor(() => {
      expect(screen.getByText('Mis Estadisticas')).toBeDefined();
    });
    await user.click(screen.getByText('Mis Alertas'));
    expect(screen.getByText('Mis Alertas').classList.contains('active')).toBe(true);
    expect(screen.getByText('Mis Estadisticas').classList.contains('active')).toBe(false);
  });

  it('muestra loading mientras carga', () => {
    render(<MiActividadPanel />);
    expect(screen.getByText('Cargando tu actividad...')).toBeDefined();
  });

  it('muestra mensaje de error cuando falla la carga', async () => {
    mockResolve.mockResolvedValue({ success: false, message: 'Error de conexion' });
    render(<MiActividadPanel />);
    await waitFor(() => {
      expect(screen.getByText('Error de conexion')).toBeDefined();
    });
  });

  it('renderiza boton Actualizar', async () => {
    render(<MiActividadPanel />);
    await waitFor(() => {
      expect(screen.getByText('Actualizar')).toBeDefined();
    });
  });
});
