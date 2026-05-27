import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

const { mockObtenerDashboard, MOCK_DATA } = vi.hoisted(() => {
  const MOCK_DATA = {
    eventosHoy: 15,
    eventoSemana: 120,
    eventosMes: 500,
    ultimosEventosSeguridad: [
      { id: 1, usuario_id: 1, accion: 'LOGIN', descripcion: 'Inicio de sesion', fecha: '2026-05-27T10:00:00Z', ip_origen: '10.0.0.1' },
      { id: 2, usuario_id: 2, accion: 'LOGOUT', descripcion: 'Cierre de sesion', fecha: '2026-05-27T09:30:00Z', ip_origen: '10.0.0.2' },
    ],
  };
  return {
    mockObtenerDashboard: vi.fn().mockResolvedValue({ success: true, data: MOCK_DATA }),
    MOCK_DATA,
  };
});

vi.mock('../../../Services/dashboard/auditorDashboardService', () => ({
  obtenerDashboard: mockObtenerDashboard,
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
  generarActividadMensualPorAnio: () => ({
    meses: [
      { mes: 'Ene', actual: 220, anterior: 200 },
      { mes: 'Feb', actual: 180, anterior: 190 },
    ],
    totalActual: 2400,
    totalAnterior: 2200,
  }),
}));

import AuditorDashboard from '../AuditorDashboard';
import { dashboardCaretaker, DashboardMemento, DashboardOriginator } from '../DashboardMemento';

describe('AuditorDashboard (Memento - Integration)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    dashboardCaretaker.limpiar();
    mockObtenerDashboard.mockResolvedValue({ success: true, data: MOCK_DATA });
  });

  it('muestra loading al iniciar', () => {
    render(<AuditorDashboard />);
    expect(screen.getByText('Cargando dashboard...')).toBeDefined();
  });

  it('llama a obtenerDashboard cuando no hay memento', async () => {
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(mockObtenerDashboard).toHaveBeenCalledTimes(1);
    });
  });

  it('renderiza el titulo y datos principales tras carga exitosa', async () => {
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    expect(screen.getByText('15')).toBeDefined();
    expect(screen.getByText('120')).toBeDefined();
    expect(screen.getByText('500')).toBeDefined();
    expect(screen.getByText('Hoy')).toBeDefined();
    expect(screen.getByText('Esta Semana')).toBeDefined();
    expect(screen.getByText('Este Mes')).toBeDefined();
  });

  it('no renderiza loading ni error cuando la carga es exitosa', async () => {
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    expect(screen.queryByText('Cargando dashboard...')).toBeNull();
    expect(screen.queryByText('Error al cargar dashboard')).toBeNull();
  });

  it('guarda el memento en el caretaker tras carga exitosa', async () => {
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    const memento = dashboardCaretaker.recuperar();
    expect(memento).not.toBeNull();
    expect(memento.getData()).toEqual(MOCK_DATA);
  });

  it('usa el memento cacheado cuando es valido (no llama al servicio)', async () => {
    const originator = new DashboardOriginator();
    dashboardCaretaker.guardar(originator.guardar(MOCK_DATA));
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    expect(mockObtenerDashboard).not.toHaveBeenCalled();
    expect(screen.getByText('15')).toBeDefined();
  });

  it('usa memento expirado pero refresca silenciosamente', async () => {
    const tsViejo = Date.now() - 60000;
    const mementoViejo = new DashboardMemento(MOCK_DATA, tsViejo);
    dashboardCaretaker.guardar(mementoViejo);
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    expect(mockObtenerDashboard).toHaveBeenCalledTimes(1);
    expect(screen.getByText('15')).toBeDefined();
  });

  it('restaura del memento y muestra ultima actualizacion correcta', async () => {
    const ts = 1700000000000;
    const memento = new DashboardMemento(MOCK_DATA, ts);
    dashboardCaretaker.guardar(memento);
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    expect(screen.getByText(/Última actualización/)).toBeDefined();
  });

  it('muestra estado de error cuando falla el servicio', async () => {
    mockObtenerDashboard.mockResolvedValue({ success: false, message: 'Error de red' });
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Error de red')).toBeDefined();
    });
    expect(screen.getByText('Reintentar')).toBeDefined();
  });

  it('reintenta la carga al hacer clic en Reintentar', async () => {
    mockObtenerDashboard.mockResolvedValue({ success: false, message: 'Error de red' });
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Error de red')).toBeDefined();
    });
    mockObtenerDashboard.mockResolvedValue({ success: true, data: MOCK_DATA });
    const user = userEvent.setup();
    await user.click(screen.getByText('Reintentar'));
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    expect(mockObtenerDashboard).toHaveBeenCalledTimes(2);
  });

  it('renderiza la tabla de eventos de seguridad', async () => {
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Últimos Eventos de Seguridad')).toBeDefined();
    });
    expect(screen.getByText('LOGIN')).toBeDefined();
    expect(screen.getByText('LOGOUT')).toBeDefined();
    expect(screen.getByText('10.0.0.1')).toBeDefined();
    expect(screen.getByText('10.0.0.2')).toBeDefined();
  });

  it('muestra mensaje vacio cuando no hay eventos de seguridad', async () => {
    mockObtenerDashboard.mockResolvedValue({
      success: true,
      data: { eventosHoy: 0, eventoSemana: 0, eventosMes: 0, ultimosEventosSeguridad: [] },
    });
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('No hay eventos de seguridad')).toBeDefined();
    });
  });

  it('renderiza los period pills (7d, 30d, 90d, 1a)', async () => {
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    expect(screen.getByText('7d')).toBeDefined();
    expect(screen.getByText('30d')).toBeDefined();
    expect(screen.getByText('90d')).toBeDefined();
    expect(screen.getByText('1a')).toBeDefined();
  });

  it('tiene el periodo 1a como activo por defecto', async () => {
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    const pill1a = screen.getByText('1a');
    expect(pill1a.className).toContain('period-pill-active');
  });

  it('renderiza el boton de Actualizar', async () => {
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    expect(screen.getByText('Actualizar')).toBeDefined();
  });

  it('llama a obtenerDashboard al hacer clic en Actualizar', async () => {
    const user = userEvent.setup();
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    const llamadasAntes = mockObtenerDashboard.mock.calls.length;
    await user.click(screen.getByText('Actualizar'));
    await waitFor(() => {
      expect(mockObtenerDashboard.mock.calls.length).toBeGreaterThan(llamadasAntes);
    });
  });

  it('muestra las variaciones (badges) en las cards', async () => {
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    const badges = document.querySelectorAll('.variacion-badge');
    expect(badges.length).toBe(3);
  });

  it('tolera que obtenerDashboard devuelva success false sin mensaje', async () => {
    mockObtenerDashboard.mockResolvedValue({ success: false, data: null });
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Error al cargar dashboard')).toBeDefined();
    });
  });

  it('carga normalmente si el memento tiene getData() null', async () => {
    const mementoNull = new DashboardMemento(null, Date.now());
    dashboardCaretaker.guardar(mementoNull);
    render(<AuditorDashboard />);
    await waitFor(() => {
      expect(screen.getByText('Dashboard de Auditoría')).toBeDefined();
    });
    expect(mockObtenerDashboard).toHaveBeenCalledTimes(1);
  });
});
