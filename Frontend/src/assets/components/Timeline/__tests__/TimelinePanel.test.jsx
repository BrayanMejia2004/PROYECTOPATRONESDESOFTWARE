import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

const { mockObtenerTimeline, mockObtenerResumenUsuario, mockObtenerActividadCalendario, EVENTOS_MOCK, RESUMEN_MOCK, CALENDARIO_MOCK } = vi.hoisted(() => {
  const EVENTOS_MOCK = [
    { id: 1, tipo: 'BASICA', accion: 'LOGIN', descripcion: 'Inicio de sesion', fecha: '2026-05-01T10:00:00Z', ipOrigen: '10.0.0.1' },
    { id: 2, tipo: 'SEGURIDAD', accion: 'ALERTA', descripcion: 'Intento fallido', fecha: '2026-05-02T15:30:00Z', ipOrigen: '10.0.0.2' },
    { id: 3, tipo: 'COMPLETA', accion: 'LOGOUT', descripcion: 'Cierre de sesion', fecha: '2026-05-03T20:00:00Z', ipOrigen: '10.0.0.3' },
  ];
  const RESUMEN_MOCK = {
    nombre: 'Juan', apellido: 'Perez', username: 'jperez',
    email: 'juan@test.com', estado: true,
  };
  const CALENDARIO_MOCK = [
    { fecha: '2026-05-01', total: 5 },
    { fecha: '2026-05-02', total: 3 },
  ];
  return {
    mockObtenerTimeline: vi.fn(),
    mockObtenerResumenUsuario: vi.fn(),
    mockObtenerActividadCalendario: vi.fn(),
    EVENTOS_MOCK, RESUMEN_MOCK, CALENDARIO_MOCK,
  };
});

vi.mock('../../../Services/timeline/timelineService', () => ({
  obtenerTimeline: mockObtenerTimeline,
  obtenerResumenUsuario: mockObtenerResumenUsuario,
  obtenerActividadCalendario: mockObtenerActividadCalendario,
}));

vi.mock('../../../Utils/datosSimulados', () => ({
  generarDistribucionHoras: () => [
    { label: '0-4', hInicio: 0, hFin: 4, eventos: 2 },
    { label: '5-8', hInicio: 5, hFin: 8, eventos: 5 },
    { label: '9-12', hInicio: 9, hFin: 12, eventos: 8 },
    { label: '13-16', hInicio: 13, hFin: 16, eventos: 12 },
    { label: '17-20', hInicio: 17, hFin: 20, eventos: 6 },
    { label: '21-23', hInicio: 21, hFin: 23, eventos: 3 },
  ],
}));

import TimelinePanel from '../TimelinePanel';

const setupMocks = () => {
  mockObtenerTimeline.mockResolvedValue({ success: true, data: EVENTOS_MOCK });
  mockObtenerResumenUsuario.mockResolvedValue({ success: true, data: RESUMEN_MOCK });
  mockObtenerActividadCalendario.mockResolvedValue({ success: true, data: CALENDARIO_MOCK });
};

describe('TimelinePanel', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    setupMocks();
  });

  it('renderiza loading al montar', () => {
    mockObtenerTimeline.mockImplementation(() => new Promise(() => {}));
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    expect(screen.getByText('Cargando...')).toBeDefined();
  });

  it('renderiza error cuando falla carga del timeline', async () => {
    mockObtenerTimeline.mockResolvedValue({ success: false, message: 'Error de servidor' });
    mockObtenerActividadCalendario.mockResolvedValue({ success: false, data: null });
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('Error de servidor')).toBeDefined();
    });
  });

  it('renderiza eventos correctamente', async () => {
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('Inicio de sesion')).toBeDefined();
    });
    expect(screen.getByText('Intento fallido')).toBeDefined();
    expect(screen.getByText('Cierre de sesion')).toBeDefined();
    expect(screen.getByText('3 eventos')).toBeDefined();
  });

  it('muestra informacion del usuario en el encabezado', async () => {
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText(/Juan Perez/)).toBeDefined();
    });
    expect(screen.getByText('@jperez')).toBeDefined();
    expect(screen.getByText('juan@test.com')).toBeDefined();
    expect(screen.getByText(/Activo/)).toBeDefined();
  });

  it('alterna entre orden ascendente y descendente', async () => {
    const user = userEvent.setup();
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('Inicio de sesion')).toBeDefined();
    });

    const btn = screen.getByText('Más antiguos');
    expect(btn).toBeDefined();

    await user.click(btn);
    await waitFor(() => {
      expect(screen.getByText('Más recientes')).toBeDefined();
    });

    await user.click(screen.getByText('Más recientes'));
    await waitFor(() => {
      expect(screen.getByText('Más antiguos')).toBeDefined();
    });
  });

  it('muestra boton Ver mas cuando hayMas es true', async () => {
    mockObtenerTimeline.mockResolvedValue({ success: true, data: Array.from({ length: 50 }, (_, i) => ({ id: i + 1, tipo: 'BASICA', accion: 'TEST', descripcion: `Evento ${i + 1}`, fecha: '2026-05-01T10:00:00Z' })) });
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('Ver más')).toBeDefined();
    });
  });

  it('no muestra Ver mas cuando hayMas es false', async () => {
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('Inicio de sesion')).toBeDefined();
    });
    expect(screen.queryByText('Ver más')).toBeNull();
  });

  it('carga mas eventos al hacer clic en Ver mas', async () => {
    mockObtenerTimeline.mockResolvedValue({ success: true, data: Array.from({ length: 50 }, (_, i) => ({ id: i + 1, tipo: 'BASICA', accion: 'TEST', descripcion: `Evento ${i + 1}`, fecha: '2026-05-01T10:00:00Z' })) });
    const user = userEvent.setup();
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('Ver más')).toBeDefined();
    });
    await user.click(screen.getByText('Ver más'));
    expect(mockObtenerTimeline).toHaveBeenCalledWith(1, 100);
  });

  it('filtra por tipo de evento al hacer clic en un pill', async () => {
    const user = userEvent.setup();
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('Inicio de sesion')).toBeDefined();
    });

    const seguridadBtn = screen.getByRole('button', { name: 'Seguridad' });
    await user.click(seguridadBtn);
    await waitFor(() => {
      expect(screen.getByText('Intento fallido')).toBeDefined();
    });
    expect(screen.queryByText('Inicio de sesion')).toBeNull();
  });

  it('muestra el boton Limpiar cuando hay filtros activos', async () => {
    const user = userEvent.setup();
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('Inicio de sesion')).toBeDefined();
    });

    const seguridadBtn = screen.getByRole('button', { name: 'Seguridad' });
    await user.click(seguridadBtn);
    await waitFor(() => {
      expect(screen.getByText('Limpiar')).toBeDefined();
    });
  });

  it('renderiza el calendario de actividad', async () => {
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('Calendario de Actividad')).toBeDefined();
    });
    expect(screen.getByText('BD')).toBeDefined();
  });

  it('muestra SIM cuando no hay datos del calendario', async () => {
    mockObtenerActividadCalendario.mockResolvedValue({ success: true, data: [] });
    render(<TimelinePanel usuarioId={1} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText('SIM')).toBeDefined();
    });
  });

  it('llama a onCerrar al hacer clic en el overlay', async () => {
    const onCerrar = vi.fn();
    const user = userEvent.setup();
    render(<TimelinePanel usuarioId={1} onCerrar={onCerrar} />);
    await waitFor(() => {
      expect(screen.getByText('Inicio de sesion')).toBeDefined();
    });
    const overlay = document.querySelector('.timeline-overlay');
    await user.click(overlay);
    expect(onCerrar).toHaveBeenCalled();
  });

  it('no llama a onCerrar al hacer clic dentro del panel', async () => {
    const onCerrar = vi.fn();
    const user = userEvent.setup();
    render(<TimelinePanel usuarioId={1} onCerrar={onCerrar} />);
    await waitFor(() => {
      expect(screen.getByText('Inicio de sesion')).toBeDefined();
    });
    const panel = document.querySelector('.timeline-panel');
    await user.click(panel);
    expect(onCerrar).not.toHaveBeenCalled();
  });

  it('muestra timeline-id-badge con el usuarioId', async () => {
    render(<TimelinePanel usuarioId={42} onCerrar={() => {}} />);
    await waitFor(() => {
      expect(screen.getByText(/ID: 42/)).toBeDefined();
    });
  });
});
