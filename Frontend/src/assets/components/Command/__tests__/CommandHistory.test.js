import { describe, it, expect, beforeEach, vi } from 'vitest';
import { CommandHistory } from '../CommandHistory';

const crearMockCommand = (nombre) => ({
  execute: vi.fn(),
  undo: vi.fn(),
  getDescripcion: () => nombre,
});

describe('CommandHistory (Invoker)', () => {
  let history;
  beforeEach(() => { history = new CommandHistory(); });

  it('execute agrega comando a undoStack y limpia redoStack', async () => {
    const cmd = crearMockCommand('Cmd 1');
    await history.execute(cmd);
    expect(history.undoStack).toHaveLength(1);
    expect(history.undoStack[0]).toBe(cmd);
    expect(history.redoStack).toHaveLength(0);
  });

  it('undo mueve comando de undoStack a redoStack y llama a undo()', async () => {
    const cmd = crearMockCommand('Cmd 1');
    await history.execute(cmd);
    await history.undo();
    expect(history.undoStack).toHaveLength(0);
    expect(history.redoStack).toHaveLength(1);
    expect(history.redoStack[0]).toBe(cmd);
    expect(cmd.undo).toHaveBeenCalledOnce();
  });

  it('redo mueve comando de redoStack a undoStack y llama a execute()', async () => {
    const cmd = crearMockCommand('Cmd 1');
    await history.execute(cmd);
    await history.undo();
    await history.redo();
    expect(history.undoStack).toHaveLength(1);
    expect(history.redoStack).toHaveLength(0);
    expect(cmd.execute).toHaveBeenCalledTimes(2);
  });

  it('undo no hace nada si undoStack esta vacio', async () => {
    await history.undo();
    expect(history.undoStack).toHaveLength(0);
    expect(history.redoStack).toHaveLength(0);
  });

  it('redo no hace nada si redoStack esta vacio', async () => {
    await history.redo();
    expect(history.undoStack).toHaveLength(0);
    expect(history.redoStack).toHaveLength(0);
  });

  it('canUndo retorna true solo si hay comandos en undoStack', async () => {
    expect(history.canUndo()).toBe(false);
    await history.execute(crearMockCommand('X'));
    expect(history.canUndo()).toBe(true);
    await history.undo();
    expect(history.canUndo()).toBe(false);
  });

  it('canRedo retorna true solo si hay comandos en redoStack', async () => {
    expect(history.canRedo()).toBe(false);
    await history.execute(crearMockCommand('X'));
    expect(history.canRedo()).toBe(false);
    await history.undo();
    expect(history.canRedo()).toBe(true);
    await history.redo();
    expect(history.canRedo()).toBe(false);
  });

  it('getUndoDescription retorna descripcion del tope undoStack', async () => {
    expect(history.getUndoDescription()).toBeNull();
    await history.execute(crearMockCommand('Alpha'));
    expect(history.getUndoDescription()).toBe('Alpha');
  });

  it('getRedoDescription retorna descripcion del tope redoStack', async () => {
    expect(history.getRedoDescription()).toBeNull();
    await history.execute(crearMockCommand('Beta'));
    await history.undo();
    expect(history.getRedoDescription()).toBe('Beta');
  });

  it('execute nuevo comando limpia redoStack (descarta redo pendiente)', async () => {
    const cmd1 = crearMockCommand('A');
    const cmd2 = crearMockCommand('B');
    const cmd3 = crearMockCommand('C');
    await history.execute(cmd1);
    await history.execute(cmd2);
    await history.undo();
    expect(history.canRedo()).toBe(true);
    await history.execute(cmd3);
    expect(history.canRedo()).toBe(false);
    expect(history.undoStack).toHaveLength(2);
    expect(history.undoStack[0]).toBe(cmd1);
    expect(history.undoStack[1]).toBe(cmd3);
  });

  it('getHistorial retorna copia reversed de undoStack sin mutar original', async () => {
    const cmd1 = crearMockCommand('A');
    const cmd2 = crearMockCommand('B');
    const cmd3 = crearMockCommand('C');
    await history.execute(cmd1);
    await history.execute(cmd2);
    await history.execute(cmd3);
    const historial = history.getHistorial();
    expect(historial).toHaveLength(3);
    expect(historial[0].getDescripcion()).toBe('C');
    expect(historial[1].getDescripcion()).toBe('B');
    expect(historial[2].getDescripcion()).toBe('A');
    expect(history.undoStack).toHaveLength(3);
  });

  it('flujo completo: exec -> exec -> undo -> undo -> redo -> exec', async () => {
    const a = crearMockCommand('A');
    const b = crearMockCommand('B');
    const c = crearMockCommand('C');
    await history.execute(a);
    await history.execute(b);
    expect(history.undoStack).toHaveLength(2);
    expect(history.canUndo()).toBe(true);
    expect(history.canRedo()).toBe(false);
    await history.undo();
    expect(history.undoStack).toHaveLength(1);
    expect(history.redoStack).toHaveLength(1);
    expect(history.getUndoDescription()).toBe('A');
    expect(history.getRedoDescription()).toBe('B');
    await history.undo();
    expect(history.undoStack).toHaveLength(0);
    expect(history.redoStack).toHaveLength(2);
    await history.redo();
    expect(history.undoStack).toHaveLength(1);
    expect(history.redoStack).toHaveLength(1);
    expect(history.getUndoDescription()).toBe('A');
    await history.execute(c);
    expect(history.undoStack).toHaveLength(2);
    expect(history.redoStack).toHaveLength(0);
    expect(history.undoStack[0]).toBe(a);
    expect(history.undoStack[1]).toBe(c);
  });
});
