import { describe, it, expect, beforeEach } from 'vitest';
import threatEventBus from '../ThreatEventBus';

describe('ThreatEventBus (Subject)', () => {
  beforeEach(() => {
    threatEventBus.observers = [];
    threatEventBus.threats = [];
  });

  const mockThreat = (id = 1) => ({
    id,
    tipo: 'FUERZA_BRUTA',
    severidad: 'ALTA',
    descripcion: 'Test threat',
    ipOrigen: '192.168.1.1',
    fecha: new Date().toISOString(),
    activa: true,
  });

  it('attach agrega un observer a la lista', () => {
    const observer = { update: () => {} };
    threatEventBus.attach(observer);
    expect(threatEventBus.observers).toHaveLength(1);
  });

  it('attach asigna _obsId y no duplica', () => {
    const observer = { update: () => {} };
    threatEventBus.attach(observer);
    threatEventBus.attach(observer);
    expect(threatEventBus.observers).toHaveLength(1);
    expect(observer._obsId).toBeDefined();
  });

  it('detach remueve un observer por _obsId', () => {
    const observer = { update: () => {} };
    threatEventBus.attach(observer);
    expect(threatEventBus.observers).toHaveLength(1);
    threatEventBus.detach(observer);
    expect(threatEventBus.observers).toHaveLength(0);
  });

  it('notify llama a update en todos los observers', () => {
    let called = 0;
    const observer = { update: () => { called++; } };
    threatEventBus.attach(observer);
    threatEventBus.notify(mockThreat(1));
    expect(called).toBe(1);
  });

  it('notify no duplica amenazas con mismo id', () => {
    const observer = { update: () => {} };
    threatEventBus.attach(observer);
    threatEventBus.notify(mockThreat(1));
    threatEventBus.notify(mockThreat(1));
    expect(threatEventBus.threats).toHaveLength(1);
  });

  it('notify permite amenazas con distinto id', () => {
    const observer = { update: () => {} };
    threatEventBus.attach(observer);
    threatEventBus.notify(mockThreat(1));
    threatEventBus.notify(mockThreat(2));
    expect(threatEventBus.threats).toHaveLength(2);
  });

  it('getActiveThreats solo retorna amenazas activas', () => {
    threatEventBus.notify(mockThreat(1));
    threatEventBus.notify({ ...mockThreat(2), activa: false });
    const active = threatEventBus.getActiveThreats();
    expect(active).toHaveLength(1);
    expect(active[0].id).toBe(1);
  });

  it('clearResolved marca amenaza como inactiva', () => {
    threatEventBus.notify(mockThreat(1));
    expect(threatEventBus.getActiveThreats()).toHaveLength(1);
    threatEventBus.clearResolved(1);
    expect(threatEventBus.getActiveThreats()).toHaveLength(0);
  });

  it('getAllThreats retorna todas las amenazas', () => {
    threatEventBus.notify(mockThreat(1));
    threatEventBus.notify(mockThreat(2));
    expect(threatEventBus.getAllThreats()).toHaveLength(2);
  });
});
