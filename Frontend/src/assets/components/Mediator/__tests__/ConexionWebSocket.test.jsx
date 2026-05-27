import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

class MockWebSocket {
  constructor(url) {
    this.url = url;
    this.readyState = 0;
    this.onopen = null;
    this.onmessage = null;
    this.onclose = null;
    this.onerror = null;
  }
  send(data) { this._sent = data; }
  close() { this.readyState = 3; this.onclose?.(); }
}

const { mockWebSocket } = vi.hoisted(() => ({
  mockWebSocket: vi.fn(() => new MockWebSocket('ws://localhost/ws/eventos-globales')),
}));

vi.stubGlobal('WebSocket', mockWebSocket);

import { render } from '@testing-library/react';
import ConexionWebSocket from '../ConexionWebSocket';

describe('ConexionWebSocket', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    mockWebSocket.mockClear();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('crea conexion WebSocket al montar', () => {
    render(<ConexionWebSocket mediator={null} />);
    expect(mockWebSocket).toHaveBeenCalled();
  });

  it('construye URL del WS con ws://', () => {
    render(<ConexionWebSocket mediator={null} />);
    expect(mockWebSocket).toHaveBeenCalledWith(
      expect.stringContaining('ws://localhost/ws/eventos-globales')
    );
  });

  it('notifica al mediator al recibir un evento', () => {
    const mediator = { notify: vi.fn() };
    render(<ConexionWebSocket mediator={mediator} />);
    const ws = mockWebSocket.mock.results[0]?.value;
    expect(ws).toBeDefined();
    ws.onopen?.();
    ws.onmessage?.({ data: JSON.stringify({ type: 'EVENTO', evento: { id: 1, tipo: 'BASICA' } }) });
    expect(mediator.notify).toHaveBeenCalledWith(null, 'WEBSOCKET_EVENTO', { id: 1, tipo: 'BASICA' });
  });

  it('ignora mensajes no JSON', () => {
    const mediator = { notify: vi.fn() };
    render(<ConexionWebSocket mediator={mediator} />);
    const ws = mockWebSocket.mock.results[0]?.value;
    ws.onopen?.();
    ws.onmessage?.({ data: 'texto-plano' });
    expect(mediator.notify).not.toHaveBeenCalled();
  });

  it('ignora mensajes JSON sin type EVENTO', () => {
    const mediator = { notify: vi.fn() };
    render(<ConexionWebSocket mediator={mediator} />);
    const ws = mockWebSocket.mock.results[0]?.value;
    ws.onopen?.();
    ws.onmessage?.({ data: JSON.stringify({ type: 'HEARTBEAT' }) });
    expect(mediator.notify).not.toHaveBeenCalled();
  });

  it('ignora mensajes JSON sin campo evento', () => {
    const mediator = { notify: vi.fn() };
    render(<ConexionWebSocket mediator={mediator} />);
    const ws = mockWebSocket.mock.results[0]?.value;
    ws.onopen?.();
    ws.onmessage?.({ data: JSON.stringify({ type: 'EVENTO' }) });
    expect(mediator.notify).not.toHaveBeenCalled();
  });

  it('cierra WebSocket y timers al desmontar', () => {
    const { unmount } = render(<ConexionWebSocket mediator={null} />);
    const ws = mockWebSocket.mock.results[0]?.value;
    const wsClose = vi.spyOn(ws, 'close');
    unmount();
    expect(wsClose).toHaveBeenCalled();
  });

  it('no reconecta si el componente se desmonto antes de cerrar WS', () => {
    const mediator = { notify: vi.fn() };
    const { unmount } = render(<ConexionWebSocket mediator={mediator} />);
    const ws = mockWebSocket.mock.results[0]?.value;
    unmount();
    vi.advanceTimersByTime(6000);
    expect(mockWebSocket.mock.calls.length).toBe(1);
  });
});
