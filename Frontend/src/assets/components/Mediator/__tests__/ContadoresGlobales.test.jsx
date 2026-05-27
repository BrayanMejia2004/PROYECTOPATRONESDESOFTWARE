import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';

const mockObtenerResumenGlobal = vi.fn();

vi.mock('../../../Services/eventoGlobalService', () => ({
  obtenerResumenGlobal: mockObtenerResumenGlobal,
}));

import ContadoresGlobales from '../ContadoresGlobales';

describe('ContadoresGlobales', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renderiza las 4 cards con labels', () => {
    render(<ContadoresGlobales />);
    expect(screen.getByText('Eventos hoy')).toBeDefined();
    expect(screen.getByText('Países activos')).toBeDefined();
    expect(screen.getByText('Usuarios activos')).toBeDefined();
    expect(screen.getByText('IPs únicas')).toBeDefined();
  });

  it('muestra valores desde la prop resumen', () => {
    const resumen = { totalEventos: 100, paisesDetectados: 5, usuariosActivos: 20, ipsUnicas: 45 };
    render(<ContadoresGlobales resumen={resumen} />);
    expect(screen.getByText('100')).toBeDefined();
    expect(screen.getByText('5')).toBeDefined();
    expect(screen.getByText('20')).toBeDefined();
    expect(screen.getByText('45')).toBeDefined();
  });

  it('muestra 0 por defecto si no hay resumen ni datos cargados', () => {
    mockObtenerResumenGlobal.mockResolvedValue({ success: true, data: null });
    render(<ContadoresGlobales />);
    expect(screen.getByText('0')).toBeDefined();
  });

  it('carga resumen propio si no recibe prop resumen', async () => {
    mockObtenerResumenGlobal.mockResolvedValue({
      success: true,
      data: { totalEventos: 50, paisesDetectados: 3, usuariosActivos: 8, ipsUnicas: 15 },
    });
    render(<ContadoresGlobales />);
    await vi.waitFor(() => {
      expect(mockObtenerResumenGlobal).toHaveBeenCalled();
    });
  });

  it('no llama a obtenerResumenGlobal si recibe prop resumen', () => {
    render(<ContadoresGlobales resumen={{ totalEventos: 10, paisesDetectados: 1, usuariosActivos: 2, ipsUnicas: 3 }} />);
    expect(mockObtenerResumenGlobal).not.toHaveBeenCalled();
  });

  it('tolera error en carga propia', async () => {
    mockObtenerResumenGlobal.mockRejectedValue(new Error('fail'));
    render(<ContadoresGlobales />);
    await vi.waitFor(() => {
      expect(mockObtenerResumenGlobal).toHaveBeenCalled();
    });
  });
});
