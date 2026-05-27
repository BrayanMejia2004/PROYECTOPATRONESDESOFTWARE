import { describe, it, expect, vi, beforeEach } from 'vitest';

const mockGet = vi.fn();

vi.mock('../../../../Api/axiosConfig', () => ({
  default: { get: mockGet },
}));

vi.mock('../../../Services/ipGeolocationService', () => ({
  enriquecerEventosConGeo: vi.fn((eventos) =>
    Promise.resolve(
      (eventos || []).map((e) => ({
        ...e,
        latitud: 4.71,
        longitud: -74.07,
        pais: 'Colombia',
        ciudad: 'Bogota',
      }))
    )
  ),
}));

import { obtenerEventosGlobales, obtenerResumenGlobal } from '../../../Services/eventoGlobalService';

describe('obtenerEventosGlobales', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('retorna datos enriquecidos con geolocalizacion', async () => {
    mockGet.mockResolvedValue({
      data: [
        { id: 1, tipo: 'BASICA', accion: 'LOGIN', descripcion: 'Inicio', fecha: '2026-05-27', ip_origen: '8.8.8.8', usuario_id: 1 },
      ],
    });
    const res = await obtenerEventosGlobales();
    expect(res.success).toBe(true);
    expect(res.data).toHaveLength(1);
    expect(res.data[0].id).toBe(1);
    expect(res.data[0].latitud).toBe(4.71);
    expect(res.data[0].pais).toBe('Colombia');
  });

  it('pasa parametros de filtro a la API', async () => {
    mockGet.mockResolvedValue({ data: [] });
    await obtenerEventosGlobales({ tipo: 'SEGURIDAD', fechaDesde: '2026-05-01' });
    expect(mockGet).toHaveBeenCalledWith('/api/auditoria/lista', {
      params: { tipo: 'SEGURIDAD', fechaDesde: '2026-05-01' },
    });
  });

  it('retorna array vacio en success false si falla la API', async () => {
    mockGet.mockRejectedValue({ response: { data: 'Error' }, message: 'Network error' });
    const res = await obtenerEventosGlobales();
    expect(res.success).toBe(false);
    expect(res.data).toEqual([]);
  });

  it('mapea correctamente los campos del backend', async () => {
    mockGet.mockResolvedValue({
      data: [
        { id: 10, tipo: 'COMPLETA', accion: 'LOGOUT', descripcion: 'Salida', fecha: '2026-05-26', ip_origen: '10.0.0.1', usuario_id: 3 },
      ],
    });
    const res = await obtenerEventosGlobales();
    expect(res.data[0].id).toBe(10);
    expect(res.data[0].tipo).toBe('COMPLETA');
    expect(res.data[0].ipOrigen).toBe('10.0.0.1');
    expect(res.data[0].usuarioId).toBe(3);
  });

  it('tolera response.data null', async () => {
    mockGet.mockResolvedValue({ data: null });
    const res = await obtenerEventosGlobales();
    expect(res.success).toBe(true);
    expect(res.data).toEqual([]);
  });
});

describe('obtenerResumenGlobal', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('retorna resumen con datos de dashboard e ips', async () => {
    mockGet
      .mockResolvedValueOnce({ data: { eventosHoy: 50 } })
      .mockResolvedValueOnce({ data: [{ ip: '1.1.1.1' }, { ip: '2.2.2.2' }] })
      .mockResolvedValueOnce({ data: { top5UsuariosActivos: [{ id: 1 }, { id: 2 }, { id: 3 }] } });
    const res = await obtenerResumenGlobal();
    expect(res.success).toBe(true);
    expect(res.data.totalEventos).toBe(50);
    expect(res.data.ipsUnicas).toBe(2);
    expect(res.data.usuariosActivos).toBe(3);
  });

  it('tolera error en estadisticas (fallback usuariosActivos=0)', async () => {
    mockGet
      .mockResolvedValueOnce({ data: { eventosHoy: 10 } })
      .mockResolvedValueOnce({ data: [] })
      .mockRejectedValueOnce({});
    const res = await obtenerResumenGlobal();
    expect(res.data.usuariosActivos).toBe(0);
  });

  it('retorna success false si falla dashboard', async () => {
    mockGet
      .mockRejectedValueOnce({ message: 'Error' });
    const res = await obtenerResumenGlobal();
    expect(res.success).toBe(false);
  });

  it('retorta campos con valores por defecto si faltan datos', async () => {
    mockGet
      .mockResolvedValueOnce({ data: {} })
      .mockResolvedValueOnce({ data: null });
    const res = await obtenerResumenGlobal();
    expect(res.data.totalEventos).toBe(0);
    expect(res.data.ipsUnicas).toBe(0);
  });
});
