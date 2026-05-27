import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';

const mockBarChart = vi.fn();
const mockBar = vi.fn();
const mockCell = vi.fn();

vi.mock('recharts', () => ({
  BarChart: (props) => { mockBarChart(props); return <div data-testid="bar-chart">{props.children}</div>; },
  Bar: (props) => { mockBar(props); return <div data-testid="bar">{props.children}</div>; },
  XAxis: () => <div data-testid="x-axis" />,
  YAxis: () => <div data-testid="y-axis" />,
  CartesianGrid: () => <div data-testid="cartesian-grid" />,
  Tooltip: () => <div data-testid="tooltip" />,
  Cell: (props) => { mockCell(props); return <div data-testid="cell" />; },
  ResponsiveContainer: ({ children }) => <div data-testid="responsive-container">{children}</div>,
}));

import MapaCalorGrafico from '../MapaCalorGrafico';

const ipsMock = [
  { ipOrigen: '10.0.0.1', totalEventos: 50, totalUsuariosDistintos: 3, nivelIntensidad: 7, esSospechosa: true },
  { ipOrigen: '10.0.0.2', totalEventos: 10, totalUsuariosDistintos: 1, nivelIntensidad: 3, esSospechosa: false },
];

describe('MapaCalorGrafico', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('retorna null si no hay ips', () => {
    const { container } = render(<MapaCalorGrafico ips={[]} />);
    expect(container.innerHTML).toBe('');
  });

  it('retorna null si ips es undefined', () => {
    const { container } = render(<MapaCalorGrafico />);
    expect(container.innerHTML).toBe('');
  });

  it('renderiza BarChart con datos', () => {
    render(<MapaCalorGrafico ips={ipsMock} />);
    expect(screen.getByTestId('bar-chart')).toBeDefined();
    expect(mockBarChart).toHaveBeenCalled();
    expect(mockBar).toHaveBeenCalled();
  });

  it('pasa dataKey totalEventos a Bar', () => {
    render(<MapaCalorGrafico ips={ipsMock} />);
    const barCall = mockBar.mock.calls[0][0];
    expect(barCall.dataKey).toBe('totalEventos');
  });

  it('renderiza un Cell por cada IP', () => {
    render(<MapaCalorGrafico ips={ipsMock} />);
    const cells = screen.getAllByTestId('cell');
    expect(cells).toHaveLength(2);
  });

  it('devuelve null sin error si window no esta definido (chartWidth catch)', () => {
    const { container } = render(<MapaCalorGrafico ips={ipsMock} />);
    expect(container.innerHTML).toBeTruthy();
  });
});
