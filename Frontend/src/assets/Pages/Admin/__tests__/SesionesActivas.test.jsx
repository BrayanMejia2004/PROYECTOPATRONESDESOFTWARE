import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

const { mockObtenerSesiones, mockObtenerMetricas, mockRevocarSesion, mockSesiones, mockMetricas } = vi.hoisted(() => {
  const mockSesiones = [
    { id: 1, username: 'jperez', ipOrigen: '10.0.0.1', minutosActivo: 30 },
    { id: 2, username: 'admin', ipOrigen: '10.0.0.2', minutosActivo: 500 },
    { id: 3, username: 'lmaria', ipOrigen: null, minutosActivo: 1500 },
  ];

  const mockMetricas = {
    revocacionesHoy: 3,
    revocacionesSemana: 15,
    revocacionesTotales: 128,
    sesionesHoy: 42,
  };

  return {
  mockObtenerSesiones: vi.fn().mockResolvedValue({ success: true, data: mockSesiones }),
  mockObtenerMetricas: vi.fn().mockResolvedValue({ success: true, data: mockMetricas }),
  mockRevocarSesion: vi.fn().mockResolvedValue({ success: true }),
  mockSesiones,
  mockMetricas,
  };
});

vi.mock('../../../Services/sesiones/sesionesService', () => ({
  obtenerSesionesActivas: mockObtenerSesiones,
  obtenerMetricas: mockObtenerMetricas,
  revocarSesion: mockRevocarSesion,
}));

vi.mock('../../../Utils/datosSimulados', () => ({
  generarTendenciaDiaria: () => [
    { fecha: '2026-05-21', valor: 8 },
    { fecha: '2026-05-22', valor: 12 },
    { fecha: '2026-05-23', valor: 4 },
    { fecha: '2026-05-24', valor: 6 },
    { fecha: '2026-05-25', valor: 10 },
    { fecha: '2026-05-26', valor: 14 },
    { fecha: '2026-05-27', valor: 9 },
  ],
  generarDistribucionDuracion: () => [
    { rango: '< 5 min', cantidad: 8 },
    { rango: '5-30 min', cantidad: 15 },
    { rango: '30 min-2h', cantidad: 22 },
    { rango: '2-8h', cantidad: 12 },
    { rango: '> 8h', cantidad: 5 },
  ],
  generarSesionesPorHora: () => [
    { hora: '00:00', sesiones: 3 },
    { hora: '06:00', sesiones: 8 },
    { hora: '09:00', sesiones: 25 },
    { hora: '12:00', sesiones: 18 },
    { hora: '15:00', sesiones: 30 },
    { hora: '18:00', sesiones: 15 },
    { hora: '21:00', sesiones: 7 },
  ],
}));

import SesionesActivas from '../SesionesActivas';

describe('SesionesActivas (Chain of Responsibility - Client)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockObtenerSesiones.mockResolvedValue({ success: true, data: mockSesiones });
    mockObtenerMetricas.mockResolvedValue({ success: true, data: mockMetricas });
    mockRevocarSesion.mockResolvedValue({ success: true });
  });

  it('muestra loading al iniciar', () => {
    render(<SesionesActivas />);
    expect(screen.getByText('Cargando sesiones activas...')).toBeDefined();
  });

  it('renderiza el titulo y subtitulo con conteo de sesiones', async () => {
    const { container } = render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('Sesiones Activas')).toBeDefined();
    });
    const subtitle = container.querySelector('.sesiones-subtitle');
    expect(subtitle.textContent).toBe('3 sesiónes activas en el sistema');
  });

  it('renderiza las 4 metricas cuando hay datos', async () => {
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('Revoc. Hoy')).toBeDefined();
    });
    expect(screen.getByText('3')).toBeDefined();
    expect(screen.getByText('15')).toBeDefined();
    expect(screen.getByText('128')).toBeDefined();
    expect(screen.getByText('42')).toBeDefined();
  });

  it('renderiza los nombres de sesion en la tabla', async () => {
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('jperez')).toBeDefined();
    });
    expect(screen.getByText('admin')).toBeDefined();
    expect(screen.getByText('lmaria')).toBeDefined();
  });

  it('aplica los campos del Chain of Responsibility a cada sesion (avatar, ip, tiempo, riesgo)', async () => {
    const { container } = render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('jperez')).toBeDefined();
    });
    const avatares = container.querySelectorAll('.sesion-avatar');
    expect(avatares).toHaveLength(3);
    expect(avatares[0].textContent).toBe('J');
    expect(avatares[1].textContent).toBe('A');
    expect(avatares[2].textContent).toBe('L');
    const ips = container.querySelectorAll('.ip-code');
    expect(ips[0].textContent).toBe('10.0.0.1');
    expect(ips[1].textContent).toBe('10.0.0.2');
    expect(ips[2].textContent).toBe('-');
  });

  it('muestra mensaje de error cuando falla la carga', async () => {
    mockObtenerSesiones.mockResolvedValue({ success: false, message: 'Error al cargar sesiones' });
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('Error al cargar sesiones')).toBeDefined();
    });
  });

  it('muestra "No hay sesiones activas" cuando la lista esta vacia', async () => {
    mockObtenerSesiones.mockResolvedValue({ success: true, data: [] });
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('No hay sesiones activas')).toBeDefined();
    });
  });

  it('actualiza subtitulo a singular cuando hay 1 sesion', async () => {
    mockObtenerSesiones.mockResolvedValue({ success: true, data: [{ id: 1, username: 'admin', ipOrigen: null, minutosActivo: 10 }] });
    const { container } = render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('Sesiones Activas')).toBeDefined();
    });
    const subtitle = container.querySelector('.sesiones-subtitle');
    expect(subtitle.textContent).toBe('1 sesión activa en el sistema');
  });

  it('abre el modal de confirmacion al hacer clic en Revocar', async () => {
    const user = userEvent.setup();
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('jperez')).toBeDefined();
    });
    const botones = screen.getAllByText('Revocar');
    await user.click(botones[0]);
    expect(screen.getByText(/Se cerrará la sesión de/)).toBeDefined();
    expect(screen.getByText('"jperez"')).toBeDefined();
  });

  it('cierra el modal al hacer clic en Cancelar', async () => {
    const user = userEvent.setup();
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('jperez')).toBeDefined();
    });
    await user.click(screen.getAllByText('Revocar')[0]);
    expect(screen.getByText(/Se cerrará la sesión de/)).toBeDefined();
    await user.click(screen.getByText('Cancelar'));
    expect(screen.queryByText(/Se cerrará la sesión de/)).toBeNull();
  });

  it('llama a revocarSesion al confirmar y recarga datos', async () => {
    const user = userEvent.setup();
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('jperez')).toBeDefined();
    });
    const llamadasAntes = mockObtenerSesiones.mock.calls.length;
    await user.click(screen.getAllByText('Revocar')[0]);
    await user.click(screen.getByText('Revocar', { selector: '.modal-btn-confirm' }));
    await waitFor(() => {
      expect(mockRevocarSesion).toHaveBeenCalledWith(1);
    });
    expect(mockObtenerSesiones.mock.calls.length).toBeGreaterThan(llamadasAntes);
  });

  it('renderiza el boton de refresco manual', async () => {
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('Sesiones Activas')).toBeDefined();
    });
    expect(screen.getByText(/Actualizando en/)).toBeDefined();
  });

  it('llama a obtenerMetricas con los datos del servicio', async () => {
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('Revoc. Hoy')).toBeDefined();
    });
    expect(mockObtenerMetricas).toHaveBeenCalled();
  });

  it('tolera error en obtenerMetricas (solo falla sesiones)', async () => {
    mockObtenerMetricas.mockResolvedValue({ success: false });
    render(<SesionesActivas />);
    await waitFor(() => {
      expect(screen.getByText('jperez')).toBeDefined();
    });
    expect(screen.queryByText('Revoc. Hoy')).toBeNull();
  });
});
