import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';

const mockObtenerEventos = vi.fn();
const mockObtenerResumen = vi.fn();

vi.mock('../../../Services/eventoGlobalService', () => ({
  obtenerEventosGlobales: mockObtenerEventos,
  obtenerResumenGlobal: mockObtenerResumen,
}));

vi.mock('react-leaflet', () => ({
  MapContainer: ({ children }) => <div data-testid="map-container">{children}</div>,
  TileLayer: () => <div data-testid="tile-layer" />,
  CircleMarker: ({ children }) => <div data-testid="circle-marker">{children}</div>,
  Tooltip: ({ children }) => <div data-testid="tooltip">{children}</div>,
  useMap: () => ({ flyTo: vi.fn(), setView: vi.fn() }),
}));

import VisorGlobalPage from '../../../Pages/VisorGlobal/VisorGlobalPage';

const MOCK_EVENTOS = [
  { id: 1, tipo: 'BASICA', accion: 'LOGIN', descripcion: 'Inicio sesion', fecha: '2026-05-27T10:00:00Z', ipOrigen: '8.8.8.8', usuarioId: 1, latitud: 4.71, longitud: -74.07, pais: 'Colombia', ciudad: 'Bogota' },
  { id: 2, tipo: 'SEGURIDAD', accion: 'ALERTA', descripcion: 'Intento', fecha: '2026-05-26T15:00:00Z', ipOrigen: '8.8.4.4', usuarioId: 2, latitud: 40.71, longitud: -74.00, pais: 'US', ciudad: 'New York' },
];

const MOCK_RESUMEN = { totalEventos: 50, paisesDetectados: 3, usuariosActivos: 10, ipsUnicas: 20 };

describe('VisorGlobalPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockObtenerEventos.mockResolvedValue({ success: true, data: MOCK_EVENTOS });
    mockObtenerResumen.mockResolvedValue({ success: true, data: MOCK_RESUMEN });
  });

  it('muestra el titulo Visor Global', async () => {
    render(<VisorGlobalPage />);
    expect(screen.getByText('Visor Global')).toBeDefined();
  });

  it('muestra loading al iniciar', () => {
    render(<VisorGlobalPage />);
    expect(screen.getByText('Monitoreo en vivo de eventos de auditoría en todo el mundo')).toBeDefined();
  });

  it('llama a obtenerEventosGlobales y obtenerResumenGlobal al montar', async () => {
    render(<VisorGlobalPage />);
    await waitFor(() => {
      expect(mockObtenerEventos).toHaveBeenCalledTimes(1);
    });
    expect(mockObtenerResumen).toHaveBeenCalledTimes(1);
  });

  it('renderiza el mapa y la timeline cuando los datos se cargan', async () => {
    render(<VisorGlobalPage />);
    await waitFor(() => {
      expect(screen.getByText('Mapa Global')).toBeDefined();
    });
    expect(screen.getByText('Línea de Tiempo')).toBeDefined();
    expect(screen.getByText('2 eventos')).toBeDefined();
  });

  it('renderiza contadores con datos del resumen', async () => {
    render(<VisorGlobalPage />);
    await waitFor(() => {
      expect(screen.getByText('50')).toBeDefined();
    });
    expect(screen.getByText('3')).toBeDefined();
    expect(screen.getByText('10')).toBeDefined();
    expect(screen.getByText('20')).toBeDefined();
  });

  it('renderiza FiltrosPanel con botones', async () => {
    render(<VisorGlobalPage />);
    await waitFor(() => {
      expect(screen.getByText('Aplicar')).toBeDefined();
    });
    expect(screen.getByText('Limpiar')).toBeDefined();
  });

  it('tolera error en eventos globales', async () => {
    mockObtenerEventos.mockResolvedValue({ success: false, data: [] });
    render(<VisorGlobalPage />);
    await waitFor(() => {
      expect(screen.getByText('Mapa Global')).toBeDefined();
    });
    expect(screen.getByText('No hay eventos para mostrar')).toBeDefined();
  });

  it('tolera error en resumen global', async () => {
    mockObtenerResumen.mockResolvedValue({ success: false, data: null });
    render(<VisorGlobalPage />);
    await waitFor(() => {
      expect(screen.getByText('Mapa Global')).toBeDefined();
    });
  });
});
