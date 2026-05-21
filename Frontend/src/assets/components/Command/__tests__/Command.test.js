import { describe, it, expect, beforeEach, vi } from 'vitest';
import { Command, SimularEventoCommand, SimularEscenarioCommand } from '../Command';

const { mockPost, mockDelete } = vi.hoisted(() => ({
  mockPost: vi.fn(),
  mockDelete: vi.fn(),
}));

vi.mock('../../../../Api/axiosConfig', () => ({
  default: { post: mockPost, delete: mockDelete }
}));

describe('Command (base class)', () => {
  it('execute lanza error por defecto', () => {
    const cmd = new Command();
    expect(() => cmd.execute()).toThrow('Debe implementar execute()');
  });

  it('undo lanza error por defecto', () => {
    const cmd = new Command();
    expect(() => cmd.undo()).toThrow('Debe implementar undo()');
  });

  it('getDescripcion retorna string vacio por defecto', () => {
    expect(new Command().getDescripcion()).toBe('');
  });
});

describe('SimularEventoCommand', () => {
  beforeEach(() => { vi.clearAllMocks(); });
  const evento = { accion: 'LOGIN', tipo: 'BASICA', descripcion: 'Test login' };

  it('constructor guarda evento e inicializa simulacionId en null', () => {
    const cmd = new SimularEventoCommand(evento);
    expect(cmd.evento).toEqual(evento);
    expect(cmd.simulacionId).toBeNull();
  });

  it('execute hace POST a /simular/evento y guarda simulacionId', async () => {
    mockPost.mockResolvedValue({ data: { simulacionId: 'abc-123' } });
    const cmd = new SimularEventoCommand(evento);
    const result = await cmd.execute();
    expect(mockPost).toHaveBeenCalledWith('/api/auditoria/simular/evento', evento);
    expect(cmd.simulacionId).toBe('abc-123');
    expect(result).toEqual({ simulacionId: 'abc-123' });
  });

  it('undo hace DELETE con el simulacionId almacenado', async () => {
    mockPost.mockResolvedValue({ data: { simulacionId: 'abc-123' } });
    const cmd = new SimularEventoCommand(evento);
    await cmd.execute();
    await cmd.undo();
    expect(mockDelete).toHaveBeenCalledWith('/api/auditoria/simular/abc-123');
  });

  it('undo no hace nada si simulacionId es null', async () => {
    await new SimularEventoCommand(evento).undo();
    expect(mockDelete).not.toHaveBeenCalled();
  });

  it('getDescripcion retorna string con la accion', () => {
    expect(new SimularEventoCommand(evento).getDescripcion()).toBe('Simular: LOGIN');
  });
});

describe('SimularEscenarioCommand', () => {
  beforeEach(() => { vi.clearAllMocks(); });
  const escenario = { nombre: 'Ataque Fuerza Bruta', descripcion: '30 LOGIN fallidos', eventos: [{ accion: 'LOGIN_FALLIDO', tipo: 'SEGURIDAD' }], batchSize: 10 };

  it('constructor guarda escenario e inicializa simulacionId en null', () => {
    const cmd = new SimularEscenarioCommand(escenario);
    expect(cmd.escenario).toEqual(escenario);
    expect(cmd.simulacionId).toBeNull();
  });

  it('execute hace POST a /simular/escenario y guarda simulacionId', async () => {
    mockPost.mockResolvedValue({ data: { simulacionId: 'esc-789' } });
    await new SimularEscenarioCommand(escenario).execute();
    expect(mockPost).toHaveBeenCalledWith('/api/auditoria/simular/escenario', escenario);
  });

  it('undo hace DELETE con el simulacionId almacenado', async () => {
    mockPost.mockResolvedValue({ data: { simulacionId: 'esc-789' } });
    const cmd = new SimularEscenarioCommand(escenario);
    await cmd.execute();
    await cmd.undo();
    expect(mockDelete).toHaveBeenCalledWith('/api/auditoria/simular/esc-789');
  });

  it('getDescripcion retorna string con nombre del escenario', () => {
    expect(new SimularEscenarioCommand(escenario).getDescripcion()).toBe('Escenario: Ataque Fuerza Bruta');
  });
});
