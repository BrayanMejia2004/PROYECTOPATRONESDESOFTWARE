import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, act } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ThreatAlertPanel from '../ThreatAlertPanel';
import threatEventBus from '../ThreatEventBus';
import * as threatService from '../../../Services/amenazas/threatService';

vi.mock('../../../Services/amenazas/threatService', () => ({
  resolverAmenaza: vi.fn().mockResolvedValue({ success: true }),
}));

describe('ThreatAlertPanel (ConcreteObserver 1)', () => {
  beforeEach(() => {
    threatEventBus.observers = [];
    threatEventBus.threats = [];
  });

  it('renderiza mensaje vacio cuando no hay amenazas', () => {
    render(<ThreatAlertPanel />);
    expect(screen.getByText('No hay amenazas activas')).toBeDefined();
  });

  it('se suscribe al bus al montarse y se desuscribe al desmontarse', () => {
    const { unmount } = render(<ThreatAlertPanel />);
    expect(threatEventBus.observers).toHaveLength(1);
    unmount();
    expect(threatEventBus.observers).toHaveLength(0);
  });

  it('recibe update() con una amenaza y muestra tarjeta', () => {
    render(<ThreatAlertPanel />);
    const threat = {
      id: 1,
      tipo: 'FUERZA_BRUTA',
      severidad: 'ALTA',
      descripcion: 'Intento de fuerza bruta detectado',
      ipOrigen: '192.168.1.1',
      fecha: new Date().toISOString(),
      activa: true,
    };
    act(() => { threatEventBus.notify(threat); });
    expect(screen.getByText('Intento de fuerza bruta detectado')).toBeDefined();
    expect(screen.getByText('IP: 192.168.1.1')).toBeDefined();
  });

  it('no duplica amenazas con mismo id via update()', () => {
    render(<ThreatAlertPanel />);
    const threat = {
      id: 1, tipo: 'FUERZA_BRUTA', severidad: 'ALTA',
      descripcion: 'Duplicado', ipOrigen: '1.1.1.1',
      fecha: new Date().toISOString(), activa: true,
    };
    act(() => { threatEventBus.notify(threat); });
    act(() => { threatEventBus.notify(threat); });
    const descs = screen.getAllByText('Duplicado');
    expect(descs).toHaveLength(1);
  });

  it('boton resolver llama al servicio y remueve la tarjeta', async () => {
    const user = userEvent.setup();
    render(<ThreatAlertPanel />);
    const threat = {
      id: 1, tipo: 'RAFAGA', severidad: 'CRITICA',
      descripcion: 'Rafaga de actividad',
      ipOrigen: '10.0.0.1',
      fecha: new Date().toISOString(), activa: true,
    };
    act(() => { threatEventBus.notify(threat); });
    expect(screen.getByText('Rafaga de actividad')).toBeDefined();
    await user.click(screen.getByText('Resolver'));
    expect(threatService.resolverAmenaza).toHaveBeenCalledWith(1);
  });

  it('muestra severidad ALTA con etiqueta Alta', () => {
    render(<ThreatAlertPanel />);
    act(() => {
      threatEventBus.notify({
        id: 1, tipo: 'FUERZA_BRUTA', severidad: 'ALTA',
        descripcion: 'Test', ipOrigen: '1.1.1.1',
        fecha: new Date().toISOString(), activa: true,
      });
    });
    expect(screen.getByText('Alta')).toBeDefined();
  });
});
