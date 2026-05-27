import { describe, it, expect, vi, beforeEach } from 'vitest';

const { mockGet } = vi.hoisted(() => ({
  mockGet: vi.fn(),
}));

vi.mock('../../../../Api/axiosConfig', () => ({
  default: { get: mockGet },
}));

import ipEstadisticasService from '../../../Services/mapaCalor/ipEstadisticasService';

describe('ipEstadisticasService.obtenerMapaCalor', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('llama al endpoint sin filtros', async () => {
    mockGet.mockResolvedValue({ data: [{ ipOrigen: '10.0.0.1' }] });
    const res = await ipEstadisticasService.obtenerMapaCalor();
    expect(mockGet).toHaveBeenCalledWith('/api/auditoria/estadisticas/ips', { params: {} });
    expect(res.success).toBe(true);
    expect(res.data).toEqual([{ ipOrigen: '10.0.0.1' }]);
  });

  it('pasa parametros desde/hasta', async () => {
    mockGet.mockResolvedValue({ data: [] });
    await ipEstadisticasService.obtenerMapaCalor('2026-05-01', '2026-05-31');
    expect(mockGet).toHaveBeenCalledWith('/api/auditoria/estadisticas/ips', {
      params: { desde: '2026-05-01', hasta: '2026-05-31' },
    });
  });

  it('retorna success false cuando falla', async () => {
    mockGet.mockRejectedValue({ response: { data: 'Error' } });
    const res = await ipEstadisticasService.obtenerMapaCalor();
    expect(res.success).toBe(false);
  });

  it('extrae mensaje de error de response.data string', async () => {
    mockGet.mockRejectedValue({ response: { data: 'Token expirado' } });
    const res = await ipEstadisticasService.obtenerMapaCalor();
    expect(res.message).toBe('Token expirado');
  });

  it('extrae mensaje de error de response.data.error', async () => {
    mockGet.mockRejectedValue({ response: { data: { error: 'Error interno' } } });
    const res = await ipEstadisticasService.obtenerMapaCalor();
    expect(res.message).toBe('Error interno');
  });

  it('usa mensaje generico si no hay response.data', async () => {
    mockGet.mockRejectedValue({ message: 'Network error' });
    const res = await ipEstadisticasService.obtenerMapaCalor();
    expect(res.message).toBe('Error al cargar mapa de calor');
  });
});

describe('ipEstadisticasService.obtenerDetalleIp', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('llama al endpoint con ip y limite por defecto', async () => {
    mockGet.mockResolvedValue({ data: [{ id: 1 }] });
    const res = await ipEstadisticasService.obtenerDetalleIp('10.0.0.1');
    expect(mockGet).toHaveBeenCalledWith(
      '/api/auditoria/estadisticas/ips/10.0.0.1/detalle',
      { params: { limite: 20 } }
    );
    expect(res.success).toBe(true);
  });

  it('usa limite personalizado', async () => {
    mockGet.mockResolvedValue({ data: [] });
    await ipEstadisticasService.obtenerDetalleIp('10.0.0.1', 50);
    expect(mockGet).toHaveBeenCalledWith(
      '/api/auditoria/estadisticas/ips/10.0.0.1/detalle',
      { params: { limite: 50 } }
    );
  });

  it('retorna success false cuando falla', async () => {
    mockGet.mockRejectedValue({ response: { data: 'Not found' } });
    const res = await ipEstadisticasService.obtenerDetalleIp('10.0.0.99');
    expect(res.success).toBe(false);
  });
});
