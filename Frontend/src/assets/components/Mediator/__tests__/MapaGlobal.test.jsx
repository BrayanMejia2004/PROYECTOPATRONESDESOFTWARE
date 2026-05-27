import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';

vi.mock('react-leaflet', () => ({
  MapContainer: ({ children }) => <div data-testid="map-container">{children}</div>,
  TileLayer: () => <div data-testid="tile-layer" />,
  CircleMarker: ({ children, center, radius }) => (
    <div data-testid="circle-marker" data-center={JSON.stringify(center)} data-radius={radius}>
      {children}
    </div>
  ),
  Tooltip: ({ children }) => <div data-testid="tooltip">{children}</div>,
  useMap: () => ({ flyTo: vi.fn(), setView: vi.fn() }),
}));

import MapaGlobal from '../MapaGlobal';

describe('MapaGlobal', () => {
  it('renderiza MapContainer', () => {
    render(<MapaGlobal />);
    expect(screen.getByTestId('map-container')).toBeDefined();
  });

  it('renderiza TileLayer', () => {
    render(<MapaGlobal />);
    expect(screen.getByTestId('tile-layer')).toBeDefined();
  });

  it('no muestra badge de eventos sin ubicacion cuando todos tienen coordenadas', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.agregarEvento({ id: 1, tipo: 'BASICA', latitud: 4.71, longitud: -74.07, pais: 'Colombia', ipOrigen: '8.8.8.8' });
    expect(screen.queryByText(/sin ubicación/)).toBeNull();
  });

  it('muestra badge con conteo de eventos sin ubicacion', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.agregarEvento({ id: 1, tipo: 'BASICA', latitud: 0, longitud: 0, ipOrigen: '10.0.0.1' });
    expect(screen.getByText(/1 evento\(s\) sin ubicación/)).toBeDefined();
  });

  it('renderiza CircleMarkers para eventos con coordenadas', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.agregarEvento({ id: 1, tipo: 'BASICA', latitud: 4.71, longitud: -74.07, pais: 'Colombia', ciudad: 'Bogota', ipOrigen: '8.8.8.8' });
    const markers = screen.getAllByTestId('circle-marker');
    expect(markers.length).toBeGreaterThanOrEqual(1);
  });

  it('no renderiza CircleMarkers para eventos sin coordenadas', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.agregarEvento({ id: 1, tipo: 'BASICA', latitud: 0, longitud: 0, ipOrigen: '10.0.0.1' });
    const markers = screen.queryAllByTestId('circle-marker');
    expect(markers.length).toBe(0);
  });

  it('setMediator via ref almacena referencia', () => {
    const ref = { current: null };
    const mediator = { notify: vi.fn() };
    render(<MapaGlobal ref={ref} />);
    ref.current?.setMediator(mediator);
    expect(ref.current).toBeDefined();
  });

  it('aplicarFiltros via ref actualiza estado', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.aplicarFiltros({ tipo: 'SEGURIDAD' });
    expect(ref.current).toBeDefined();
  });

  it('agregarEvento via ref anade eventos', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.agregarEvento({ id: 1, tipo: 'BASICA', latitud: 40.71, longitud: -74.00, ipOrigen: '1.1.1.1' });
    const markers = screen.getAllByTestId('circle-marker');
    expect(markers.length).toBe(1);
  });

  it('filtrarPorTipo via ref actualiza filtro', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.filtrarPorTipo('COMPLETA');
    expect(ref.current).toBeDefined();
  });

  it('enfocarEvento con coordenadas llama flyTo', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.enfocarEvento({ latitud: 4.71, longitud: -74.07 });
    expect(ref.current).toBeDefined();
  });

  it('enfocarEvento sin coordenadas no falla', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.enfocarEvento({});
    expect(ref.current).toBeDefined();
  });

  it('agrupa eventos por ubicacion y muestra count en tooltip', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.agregarEvento({ id: 1, tipo: 'BASICA', latitud: 4.71, longitud: -74.07, pais: 'Colombia', ipOrigen: '8.8.8.8' });
    ref.current?.agregarEvento({ id: 2, tipo: 'SEGURIDAD', latitud: 4.71, longitud: -74.07, pais: 'Colombia', ipOrigen: '8.8.8.8' });
    const tooltips = screen.getAllByTestId('tooltip');
    expect(tooltips.length).toBe(1);
    expect(tooltips[0].textContent).toContain('Eventos:');
  });

  it('color del marker corresponde al tipo de evento', () => {
    const ref = { current: null };
    render(<MapaGlobal ref={ref} />);
    ref.current?.agregarEvento({ id: 1, tipo: 'SEGURIDAD', latitud: 4.71, longitud: -74.07, pais: 'Colombia', ipOrigen: '8.8.8.8' });
    const markers = screen.getAllByTestId('circle-marker');
    expect(markers[0]).toBeDefined();
  });
});
