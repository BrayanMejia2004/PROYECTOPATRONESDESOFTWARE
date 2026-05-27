import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

const { mockObtenerMapaCalor, mockObtenerDetalleIp, mockObtenerDashboard, IPS_MOCK, ACTIVIDAD_MOCK } = vi.hoisted(() => {
  const IPS_MOCK = Array.from({ length: 3 }, (_, i) => ({
    ipOrigen: `10.0.0.${i + 1}`,
    totalEventos: (i + 1) * 50,
    totalUsuariosDistintos: i + 1,
    primeraVez: '2026-05-01T10:00:00Z',
    ultimaVez: '2026-05-27T15:00:00Z',
    nivelIntensidad: (i + 1) * 3,
    esSospechosa: i % 2 === 0,
  }));
  const ACTIVIDAD_MOCK = [
    { fecha: '2026-05-01', total: 5 },
    { fecha: '2026-05-15', total: 10 },
    { fecha: '2026-06-01', total: 8 },
  ];
  return {
    mockObtenerMapaCalor: vi.fn(),
    mockObtenerDetalleIp: vi.fn(),
    mockObtenerDashboard: vi.fn(),
    IPS_MOCK, ACTIVIDAD_MOCK,
  };
});

vi.mock('../../../Services/mapaCalor/ipEstadisticasService', () => ({
  default: {
    obtenerMapaCalor: mockObtenerMapaCalor,
    obtenerDetalleIp: mockObtenerDetalleIp,
  },
}));

vi.mock('../../../Services/dashboard/auditorDashboardService', () => ({
  obtenerDashboard: mockObtenerDashboard,
}));

vi.mock('recharts', () => ({
  AreaChart: ({ children }) => <div data-testid="area-chart">{children}</div>,
  Area: () => <div data-testid="area" />,
  XAxis: () => <div data-testid="x-axis" />,
  YAxis: () => <div data-testid="y-axis" />,
  Tooltip: () => <div data-testid="tooltip" />,
  ResponsiveContainer: ({ children }) => <div data-testid="responsive-container">{children}</div>,
  Brush: () => <div data-testid="brush" />,
  CartesianGrid: () => <div data-testid="cartesian-grid" />,
}));

import MapaCalorIP from '../MapaCalorIP';

const setupMocks = () => {
  mockObtenerMapaCalor.mockResolvedValue({ success: true, data: IPS_MOCK });
  mockObtenerDashboard.mockResolvedValue({
    success: true,
    data: { actividadDiaria: ACTIVIDAD_MOCK },
  });
};

describe('MapaCalorIP', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    setupMocks();
  });

  it('renderiza loading al montar', () => {
    mockObtenerMapaCalor.mockImplementation(() => new Promise(() => {}));
    render(<MapaCalorIP />);
    expect(screen.getByText('Cargando mapa de calor...')).toBeDefined();
  });

  it('renderiza error cuando falla la carga', async () => {
    mockObtenerMapaCalor.mockResolvedValue({ success: false, message: 'Error de servidor' });
    mockObtenerDashboard.mockResolvedValue({ success: true, data: { actividadDiaria: [] } });
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByText('Error de servidor')).toBeDefined();
    });
  });

  it('muestra mensaje vacio cuando no hay IPs', async () => {
    mockObtenerMapaCalor.mockResolvedValue({ success: true, data: [] });
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByText('No hay datos de IP disponibles')).toBeDefined();
    });
  });

  it('renderiza tabla con datos de IP enriquecidos por FilaVisitante', async () => {
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByText('10.0.0.1')).toBeDefined();
    });
    expect(screen.getByText('10.0.0.2')).toBeDefined();
    expect(screen.getByText('10.0.0.3')).toBeDefined();
    expect(screen.getByText('⚠ Sospechosa')).toBeDefined();
    expect(screen.getByText('✓ Normal')).toBeDefined();
  });

  it('muestra metricas del MetricasVisitante en el header', async () => {
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByText(/3 IPs/)).toBeDefined();
    });
    expect(screen.getByText(/2 sospechosas/)).toBeDefined();
  });

  it('renderiza timeline chart con actividad diaria', async () => {
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByTestId('area-chart')).toBeDefined();
    });
  });

  it('cambia a vista gráfico al hacer clic en boton', async () => {
    const user = userEvent.setup();
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByText('10.0.0.1')).toBeDefined();
    });

    const graficoBtn = screen.getByText('Gráfico');
    await user.click(graficoBtn);
    expect(screen.getByText('TimeLapse')).toBeDefined();
  });

  it('cambia a vista timelapse y renderiza reproductor', async () => {
    const user = userEvent.setup();
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByText('10.0.0.1')).toBeDefined();
    });

    await user.click(screen.getByText('TimeLapse'));
    expect(screen.getByText('1×')).toBeDefined();
    expect(screen.getByText('5×')).toBeDefined();
    expect(screen.getByText('10×')).toBeDefined();
    expect(screen.getByText('50×')).toBeDefined();
  });

  it('muestra paginacion cuando hay mas de 10 IPs', async () => {
    const muchasIps = Array.from({ length: 25 }, (_, i) => ({
      ipOrigen: `10.0.0.${i + 1}`,
      totalEventos: 10,
      totalUsuariosDistintos: 1,
      primeraVez: '2026-05-01T10:00:00Z',
      ultimaVez: '2026-05-27T15:00:00Z',
      nivelIntensidad: 3,
      esSospechosa: false,
    }));
    mockObtenerMapaCalor.mockResolvedValue({ success: true, data: muchasIps });
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByText('Página 1 de 3')).toBeDefined();
    });
  });

  it('expande detalle de IP al hacer clic en fila', async () => {
    mockObtenerDetalleIp.mockResolvedValue({
      success: true,
      data: [{ usuarioId: 1, accion: 'LOGIN', descripcion: 'Inicio', fecha: '2026-05-27T10:00:00Z', tipo: 'BASICA' }],
    });
    const user = userEvent.setup();
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByText('10.0.0.1')).toBeDefined();
    });
    const fila = screen.getByText('10.0.0.1').closest('tr');
    await user.click(fila);
    await waitFor(() => {
      expect(screen.getByText(/Últimos eventos de:/)).toBeDefined();
    });
  });

  it('filtra y recarga datos al hacer clic en Filtrar', async () => {
    const user = userEvent.setup();
    render(<MapaCalorIP />);
    await waitFor(() => {
      expect(screen.getByText('10.0.0.1')).toBeDefined();
    });

    mockObtenerMapaCalor.mockClear();
    mockObtenerMapaCalor.mockResolvedValue({ success: true, data: IPS_MOCK });
    await user.click(screen.getByText('Filtrar'));
    await waitFor(() => {
      expect(mockObtenerMapaCalor).toHaveBeenCalled();
    });
  });

  it('llama a onMetricasUpdate con las metricas calculadas', async () => {
    const onMetricasUpdate = vi.fn();
    render(<MapaCalorIP onMetricasUpdate={onMetricasUpdate} />);
    await waitFor(() => {
      expect(onMetricasUpdate).toHaveBeenCalledWith(
        expect.objectContaining({
          totalEventos: expect.any(Number),
          totalSospechosas: expect.any(Number),
        })
      );
    });
  });
});
