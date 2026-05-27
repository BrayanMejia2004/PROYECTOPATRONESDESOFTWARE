import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  buscarUbicacionPorIP,
  enriquecerEventosConGeo,
  limpiarCacheGeo,
} from '../../../Services/ipGeolocationService';

beforeEach(() => {
  limpiarCacheGeo();
});

describe('buscarUbicacionPorIP', () => {
  it('retorna fallback para IP privada 10.x.x.x', async () => {
    const res = await buscarUbicacionPorIP('10.0.0.1');
    expect(res.lat).toBe(0);
    expect(res.lon).toBe(0);
    expect(res.pais).toBe('Desconocido');
  });

  it('retorna fallback para IP privada 192.168.x.x', async () => {
    const res = await buscarUbicacionPorIP('192.168.1.1');
    expect(res.lat).toBe(0);
  });

  it('retorna fallback para localhost', async () => {
    const res = await buscarUbicacionPorIP('127.0.0.1');
    expect(res.lat).toBe(0);
  });

  it('retorna fallback para ::1', async () => {
    const res = await buscarUbicacionPorIP('::1');
    expect(res.lat).toBe(0);
  });

  it('retorna fallback para IP vacia', async () => {
    const res = await buscarUbicacionPorIP('');
    expect(res.lat).toBe(0);
  });

  it('retorna fallback para IP null', async () => {
    const res = await buscarUbicacionPorIP(null);
    expect(res.lat).toBe(0);
  });

  it('retorna fallback para IP con formato invalido', async () => {
    const res = await buscarUbicacionPorIP('no-es-ip');
    expect(res.lat).toBe(0);
  });

  it('retorna fallback para IP 169.254.x.x (link-local)', async () => {
    const res = await buscarUbicacionPorIP('169.254.1.1');
    expect(res.lat).toBe(0);
  });

  it('retorna fallback para IP 172.16-31.x.x', async () => {
    const res = await buscarUbicacionPorIP('172.20.0.1');
    expect(res.lat).toBe(0);
  });

  it('hace fetch para IP publica y retorna datos reales', async () => {
    const mockGeo = { lat: 4.71, lon: -74.07, pais: 'Colombia', ciudad: 'Bogota' };
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ status: 'success', ...mockGeo }),
    });
    const res = await buscarUbicacionPorIP('8.8.8.8');
    expect(res.lat).toBe(4.71);
    expect(res.lon).toBe(-74.07);
    expect(res.pais).toBe('Colombia');
    expect(res.ciudad).toBe('Bogota');
    expect(global.fetch).toHaveBeenCalledWith(
      expect.stringContaining('ip-api.com/json/8.8.8.8')
    );
  });

  it('cachea resultado y no repite fetch', async () => {
    const mockGeo = { lat: 40.71, lon: -74.00, pais: 'United States', ciudad: 'New York' };
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ status: 'success', ...mockGeo }),
    });
    const res1 = await buscarUbicacionPorIP('1.1.1.1');
    const res2 = await buscarUbicacionPorIP('1.1.1.1');
    expect(global.fetch).toHaveBeenCalledTimes(1);
    expect(res1).toEqual(res2);
  });

  it('retorna fallback si fetch falla', async () => {
    global.fetch = vi.fn().mockRejectedValue(new Error('Network error'));
    const res = await buscarUbicacionPorIP('8.8.4.4');
    expect(res.lat).toBe(0);
    expect(res.lon).toBe(0);
  });

  it('retorna fallback si API devuelve status fail', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ status: 'fail' }),
    });
    const res = await buscarUbicacionPorIP('8.8.8.8');
    expect(res.lat).toBe(0);
  });
});

describe('enriquecerEventosConGeo', () => {
  it('retorna array vacio si recibe array vacio', async () => {
    const res = await enriquecerEventosConGeo([]);
    expect(res).toEqual([]);
  });

  it('retorna array original si recibe null', async () => {
    const res = await enriquecerEventosConGeo(null);
    expect(res).toBeNull();
  });

  it('asigna fallback a eventos sin ipOrigen', async () => {
    const eventos = [{ id: 1, tipo: 'BASICA' }];
    const res = await enriquecerEventosConGeo(eventos);
    expect(res[0].latitud).toBe(0);
    expect(res[0].longitud).toBe(0);
    expect(res[0].pais).toBe('Desconocido');
  });

  it('asigna fallback a eventos con IP privada', async () => {
    const eventos = [{ id: 1, tipo: 'BASICA', ipOrigen: '10.0.0.5' }];
    const res = await enriquecerEventosConGeo(eventos);
    expect(res[0].latitud).toBe(0);
  });

  it('enriquece eventos con IP publica usando fetch', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ status: 'success', lat: 48.85, lon: 2.35, country: 'France', city: 'Paris' }),
    });
    const eventos = [{ id: 1, tipo: 'BASICA', ipOrigen: '2.2.2.2' }];
    const res = await enriquecerEventosConGeo(eventos);
    expect(res[0].latitud).toBe(48.85);
    expect(res[0].longitud).toBe(2.35);
    expect(res[0].pais).toBe('France');
    expect(res[0].ciudad).toBe('Paris');
  });

  it('agrupa IPs unicas y cachea (solo un fetch por IP)', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ status: 'success', lat: 0, lon: 0, country: 'Test', city: '' }),
    });
    const eventos = [
      { id: 1, ipOrigen: '3.3.3.3' },
      { id: 2, ipOrigen: '3.3.3.3' },
      { id: 3, ipOrigen: '4.4.4.4' },
    ];
    await enriquecerEventosConGeo(eventos);
    expect(global.fetch).toHaveBeenCalledTimes(2);
  });

  it('no pierde campos originales del evento', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve({ status: 'success', lat: 51.5, lon: -0.12, country: 'UK', city: 'London' }),
    });
    const eventos = [{ id: 5, tipo: 'SEGURIDAD', accion: 'LOGIN', ipOrigen: '5.5.5.5' }];
    const res = await enriquecerEventosConGeo(eventos);
    expect(res[0].id).toBe(5);
    expect(res[0].tipo).toBe('SEGURIDAD');
    expect(res[0].accion).toBe('LOGIN');
  });
});
