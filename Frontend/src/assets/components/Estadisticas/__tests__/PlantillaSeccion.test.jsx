import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import PlantillaSeccion from '../PlantillaSeccion';

describe('PlantillaSeccion (Template Method)', () => {
  const mockExtractor = vi.fn((d) => d.items);
  const mockTransformador = vi.fn((raw) => raw.map((x) => ({ ...x, transformed: true })));
  const mockRender = vi.fn((data) => <div data-testid="render-output">{data?.length} items</div>);
  const datos = { items: [{ id: 1 }, { id: 2 }, { id: 3 }] };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renderiza el titulo en el header', () => {
    render(
      <PlantillaSeccion
        titulo="Mi Seccion"
        datos={datos}
        extractor={mockExtractor}
        render={mockRender}
      />
    );
    expect(screen.getByText('Mi Seccion')).toBeDefined();
  });

  it('renderiza .estadisticas-card con .card-header y .card-body', () => {
    const { container } = render(
      <PlantillaSeccion
        titulo="Test"
        datos={datos}
        extractor={mockExtractor}
        render={mockRender}
      />
    );
    const card = container.querySelector('.estadisticas-card');
    expect(card).toBeDefined();
    expect(card.querySelector('.card-header')).toBeDefined();
    expect(card.querySelector('.card-body')).toBeDefined();
  });

  it('muestra badge-warning cuando badge > 0', () => {
    render(
      <PlantillaSeccion
        titulo="Con Badge"
        badge={5}
        datos={datos}
        extractor={mockExtractor}
        render={mockRender}
      />
    );
    const badge = screen.getByText('5');
    expect(badge.classList.contains('badge-warning')).toBe(true);
  });

  it('no renderiza badge cuando badge es null', () => {
    const { container } = render(
      <PlantillaSeccion
        titulo="Sin Badge"
        badge={null}
        datos={datos}
        extractor={mockExtractor}
        render={mockRender}
      />
    );
    expect(container.querySelector('.badge-warning')).toBeNull();
  });

  it('no renderiza badge cuando badge es 0', () => {
    const { container } = render(
      <PlantillaSeccion
        titulo="Cero"
        badge={0}
        datos={datos}
        extractor={mockExtractor}
        render={mockRender}
      />
    );
    expect(container.querySelector('.badge-warning')).toBeNull();
  });

  it('no renderiza badge cuando badge es undefined', () => {
    const { container } = render(
      <PlantillaSeccion
        titulo="Sin Prop"
        datos={datos}
        extractor={mockExtractor}
        render={mockRender}
      />
    );
    expect(container.querySelector('.badge-warning')).toBeNull();
  });

  it('llama a extractor con los datos', () => {
    render(
      <PlantillaSeccion
        titulo="Extractor Test"
        datos={datos}
        extractor={mockExtractor}
        render={mockRender}
      />
    );
    expect(mockExtractor).toHaveBeenCalledWith(datos);
  });

  it('llama a transformador con el resultado de extractor', () => {
    render(
      <PlantillaSeccion
        titulo="Transform Test"
        datos={datos}
        extractor={mockExtractor}
        transformador={mockTransformador}
        render={mockRender}
      />
    );
    expect(mockTransformador).toHaveBeenCalledWith(datos.items);
  });

  it('llama a render con el resultado de transformador', () => {
    render(
      <PlantillaSeccion
        titulo="Render Test"
        datos={datos}
        extractor={mockExtractor}
        transformador={mockTransformador}
        render={mockRender}
      />
    );
    const transformed = datos.items.map((x) => ({ ...x, transformed: true }));
    expect(mockRender).toHaveBeenCalledWith(transformed);
  });

  it('pasa raw directo a render cuando no hay transformador', () => {
    render(
      <PlantillaSeccion
        titulo="Identity Test"
        datos={datos}
        extractor={mockExtractor}
        render={mockRender}
      />
    );
    expect(mockRender).toHaveBeenCalledWith(datos.items);
  });

  it('renderiza el JSX devuelto por render dentro de .card-body', () => {
    render(
      <PlantillaSeccion
        titulo="Output Test"
        datos={datos}
        extractor={mockExtractor}
        render={mockRender}
      />
    );
    const output = screen.getByTestId('render-output');
    expect(output.textContent).toBe('3 items');
  });

  it('tolera extractor que devuelve null', () => {
    const extractorNull = vi.fn(() => null);
    const renderNull = vi.fn(() => <div data-testid="empty">Sin datos</div>);
    render(
      <PlantillaSeccion
        titulo="Null Data"
        datos={{}}
        extractor={extractorNull}
        render={renderNull}
      />
    );
    expect(renderNull).toHaveBeenCalledWith(null);
    expect(screen.getByTestId('empty')).toBeDefined();
  });
});
