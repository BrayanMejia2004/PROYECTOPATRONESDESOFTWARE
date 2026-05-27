import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

const { mockObtenerEstadisticas, mockGenerarMatriz, mockGenerarAnomalias, mockGenerarBurbujas, mockDatosBackend } = vi.hoisted(() => {
  const mockDatosBackend = {
    actividadPorHora: Object.fromEntries(
      Array.from({ length: 24 }, (_, i) => [i, Math.floor(Math.random() * 30) + 5])
    ),
    eventosPorTipo: { BASICA: 45, COMPLETA: 30, SEGURIDAD: 12 },
    top5UsuariosActivos: [
      { usuarioId: 1, username: 'admin', totalAcciones: 150 },
      { usuarioId: 2, username: 'jperez', totalAcciones: 98 },
    ],
    usuariosSinActividad: [
      { id: 5, username: 'inactivo', roles: ['AUDITOR'] },
    ],
  };

  const mockMatrizSemanal = [
    { dia: 'Lun', fecha: '2026-05-25', horas: Array.from({ length: 24 }, () => 5) },
    { dia: 'Mar', fecha: '2026-05-26', horas: Array.from({ length: 24 }, () => 8) },
    { dia: 'Mié', fecha: '2026-05-27', horas: Array.from({ length: 24 }, () => 3) },
  ];

  const mockAnomalias = {
    serie: [
      { fecha: '2026-04-27', valor: 22, anomalia: false },
      { fecha: '2026-04-28', valor: 48, anomalia: true },
    ],
    media: 18.5,
    desv: 6.2,
    totalAnomalias: 1,
    prediccion: [
      { fecha: '2026-05-28', valor: 20 },
      { fecha: '2026-05-29', valor: 22 },
    ],
  };

  const mockBurbujas = [
    { key: 'BASICA', label: 'Básica', color: '#d4a853', total: 80, usuarios: [{ id: 1, nombre: 'admin', acciones: 20 }] },
    { key: 'COMPLETA', label: 'Completa', color: '#00ba7c', total: 50, usuarios: [] },
  ];

  return {
  mockObtenerEstadisticas: vi.fn().mockResolvedValue({ success: true, data: mockDatosBackend }),
  mockGenerarMatriz: vi.fn(() => mockMatrizSemanal),
  mockGenerarAnomalias: vi.fn(() => mockAnomalias),
  mockGenerarBurbujas: vi.fn(() => mockBurbujas),
  mockDatosBackend,
  };
});

vi.mock('../../../Services/dashboard/estadisticasService', () => ({
  obtenerEstadisticas: mockObtenerEstadisticas,
}));

vi.mock('../../../Utils/datosSimulados', () => ({
  generarMatrizSemanal: mockGenerarMatriz,
  generarAnomalias: mockGenerarAnomalias,
  generarDatosBurbuja: mockGenerarBurbujas,
}));

import EstadisticasPanel from '../EstadisticasPanel';

describe('EstadisticasPanel (Client - Template Method)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockObtenerEstadisticas.mockResolvedValue({ success: true, data: mockDatosBackend });
  });

  it('muestra loading al iniciar', () => {
    render(<EstadisticasPanel />);
    expect(screen.getByText('Cargando estadísticas...')).toBeDefined();
  });

  it('muestra error cuando falla la carga', async () => {
    mockObtenerEstadisticas.mockResolvedValue({ success: false, message: 'Error de conexión' });
    render(<EstadisticasPanel />);
    await waitFor(() => {
      expect(screen.getByText('Error de conexión')).toBeDefined();
    });
  });

  it('renderiza las 4 secciones cuando hay datos', async () => {
    render(<EstadisticasPanel />);
    await waitFor(() => {
      expect(screen.getByText('Panel de Estadísticas')).toBeDefined();
    });
    expect(screen.getByText('Mapa de Calor Circular 24h × 7d')).toBeDefined();
    expect(screen.getByText('Actividad por Hora')).toBeDefined();
    expect(screen.getByText('Timeline de Anomalías y Predicción')).toBeDefined();
    expect(screen.getByText('Ecosistema de Roles')).toBeDefined();
  });

  it('renderiza el badge de anomalias cuando hay anomalias', async () => {
    const { container } = render(<EstadisticasPanel />);
    await waitFor(() => {
      const badge = container.querySelector('.badge-warning');
      expect(badge).toBeDefined();
      expect(badge.textContent).toBe('1');
    });
  });

  it('renderiza 4 tarjetas .estadisticas-card', async () => {
    const { container } = render(<EstadisticasPanel />);
    await waitFor(() => {
      expect(screen.getByText('Mapa de Calor Circular 24h × 7d')).toBeDefined();
    });
    const cards = container.querySelectorAll('.estadisticas-card');
    expect(cards).toHaveLength(4);
  });

  it('llama a los 3 generadores de datos simulados', async () => {
    render(<EstadisticasPanel />);
    await waitFor(() => {
      expect(screen.getByText('Mapa de Calor Circular 24h × 7d')).toBeDefined();
    });
    expect(mockGenerarMatriz).toHaveBeenCalled();
    expect(mockGenerarAnomalias).toHaveBeenCalled();
    expect(mockGenerarBurbujas).toHaveBeenCalled();
  });

  it('cambia periodo al hacer clic en otra pill', async () => {
    const user = userEvent.setup();
    render(<EstadisticasPanel />);
    await waitFor(() => {
      expect(screen.getByText('7d')).toBeDefined();
    });
    const pill7d = screen.getByText('7d');
    await user.click(pill7d);
    expect(pill7d.classList.contains('period-pill-active')).toBe(true);
    expect(screen.getByText('30d').classList.contains('period-pill-active')).toBe(false);
  });

  it('llama a los generadores de nuevo al cambiar periodo', async () => {
    const user = userEvent.setup();
    render(<EstadisticasPanel />);
    await waitFor(() => {
      expect(screen.getByText('7d')).toBeDefined();
    });
    const llamadasAntes = mockGenerarMatriz.mock.calls.length;
    await user.click(screen.getByText('7d'));
    expect(mockGenerarMatriz.mock.calls.length).toBeGreaterThan(llamadasAntes);
  });

  it('tolera error en el servicio y muestra mensaje', async () => {
    const onRejection = vi.fn();
    process.on('unhandledRejection', onRejection);
    mockObtenerEstadisticas.mockRejectedValue(new Error('Network error'));
    render(<EstadisticasPanel />);
    await waitFor(() => {
      const alertEl = document.querySelector('.alert-error');
      expect(alertEl).toBeDefined();
    });
    process.off('unhandledRejection', onRejection);
  });
});
