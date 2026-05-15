import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen, act } from '@testing-library/react';
import ThreatIndicator from '../ThreatIndicator';
import threatEventBus from '../ThreatEventBus';

describe('ThreatIndicator (ConcreteObserver 2)', () => {
  beforeEach(() => {
    threatEventBus.observers = [];
    threatEventBus.threats = [];
  });

  it('se suscribe al bus al montarse y se desuscribe al desmontarse', () => {
    const { unmount } = render(<ThreatIndicator />);
    expect(threatEventBus.observers).toHaveLength(1);
    unmount();
    expect(threatEventBus.observers).toHaveLength(0);
  });

  it('muestra titulo con 0 amenazas cuando no hay amenazas', () => {
    render(<ThreatIndicator />);
    const div = screen.getByTitle('0 amenaza(s) activa(s)');
    expect(div).toBeDefined();
  });

  it('update() incrementa el contador en el title', () => {
    render(<ThreatIndicator />);
    act(() => {
      threatEventBus.notify({
        id: 1, tipo: 'FUERZA_BRUTA', severidad: 'BAJA',
        descripcion: 'Test', ipOrigen: '1.1.1.1',
        fecha: new Date().toISOString(), activa: true,
      });
    });
    const div = screen.getByTitle('1 amenaza(s) activa(s)');
    expect(div).toBeDefined();
  });

  it('update() con severidad CRITICA muestra titulo correcto', () => {
    render(<ThreatIndicator />);
    act(() => {
      threatEventBus.notify({
        id: 1, tipo: 'RAFAGA', severidad: 'CRITICA',
        descripcion: 'Critica', ipOrigen: '1.1.1.1',
        fecha: new Date().toISOString(), activa: true,
      });
    });
    const div = screen.getByTitle('1 amenaza(s) activa(s)');
    expect(div).toBeDefined();
  });
});
