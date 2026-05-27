import { describe, it, expect, vi, beforeEach } from 'vitest';

const { mockGet } = vi.hoisted(() => ({
  mockGet: vi.fn(),
}));

vi.mock('../../../../Api/axiosConfig', () => ({
  default: { get: mockGet },
}));

import { obtenerTimeline, obtenerResumenUsuario, obtenerActividadCalendario } from '../../../Services/timeline/timelineService';

describe('obtenerTimeline', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('llama al endpoint con usuarioId y limite por defecto', async () => {
    mockGet.mockResolvedValue({ data: [{ id: 1 }] });
    const res = await obtenerTimeline(5);
    expect(mockGet).toHaveBeenCalledWith('/api/auditoria/usuario/5/timeline?limite=50');
    expect(res.success).toBe(true);
    expect(res.data).toEqual([{ id: 1 }]);
  });

  it('usa limite personalizado', async () => {
    mockGet.mockResolvedValue({ data: [] });
    await obtenerTimeline(5, 100);
    expect(mockGet).toHaveBeenCalledWith('/api/auditoria/usuario/5/timeline?limite=100');
  });

  it('retorna success false cuando falla la peticion', async () => {
    mockGet.mockRejectedValue({ response: { data: 'Error' } });
    const res = await obtenerTimeline(5);
    expect(res.success).toBe(false);
    expect(res.data).toBeNull();
    expect(res.message).toBe('Error');
  });

  it('retorna mensaje generico si response.data no existe', async () => {
    mockGet.mockRejectedValue({ message: 'Network error' });
    const res = await obtenerTimeline(5);
    expect(res.success).toBe(false);
    expect(res.message).toBe('Error de conexión');
  });
});

describe('obtenerResumenUsuario', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('llama al endpoint con id de usuario', async () => {
    mockGet.mockResolvedValue({ data: { nombre: 'Juan', email: 'juan@test.com' } });
    const res = await obtenerResumenUsuario(5);
    expect(mockGet).toHaveBeenCalledWith('/api/usuarios/5/resumen');
    expect(res.success).toBe(true);
    expect(res.data.nombre).toBe('Juan');
  });

  it('retorna success false cuando falla', async () => {
    mockGet.mockRejectedValue({ response: { data: 'Not found' } });
    const res = await obtenerResumenUsuario(99);
    expect(res.success).toBe(false);
  });
});

describe('obtenerActividadCalendario', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('llama al endpoint con usuarioId y dias por defecto', async () => {
    mockGet.mockResolvedValue({ data: [{ fecha: '2026-05-01', total: 10 }] });
    const res = await obtenerActividadCalendario(5);
    expect(mockGet).toHaveBeenCalledWith('/api/auditoria/usuario/5/actividad-calendario?dias=28');
    expect(res.success).toBe(true);
  });

  it('usa dias personalizado', async () => {
    mockGet.mockResolvedValue({ data: [] });
    await obtenerActividadCalendario(5, 7);
    expect(mockGet).toHaveBeenCalledWith('/api/auditoria/usuario/5/actividad-calendario?dias=7');
  });

  it('retorna success false cuando falla', async () => {
    mockGet.mockRejectedValue({ response: { data: 'Error' } });
    const res = await obtenerActividadCalendario(5);
    expect(res.success).toBe(false);
  });
});
