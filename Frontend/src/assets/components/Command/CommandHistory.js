export class CommandHistory {
  constructor() {
    this.undoStack = [];
    this.redoStack = [];
  }

  async execute(command) {
    await command.execute();
    this.undoStack.push(command);
    this.redoStack = [];
  }

  async undo() {
    if (this.undoStack.length > 0) {
      const command = this.undoStack.pop();
      await command.undo();
      this.redoStack.push(command);
    }
  }

  async redo() {
    if (this.redoStack.length > 0) {
      const command = this.redoStack.pop();
      await command.execute();
      this.undoStack.push(command);
    }
  }

  getUndoDescription() {
    return this.undoStack.length > 0
      ? this.undoStack[this.undoStack.length - 1].getDescripcion()
      : null;
  }

  getRedoDescription() {
    return this.redoStack.length > 0
      ? this.redoStack[this.redoStack.length - 1].getDescripcion()
      : null;
  }

  canUndo() {
    return this.undoStack.length > 0;
  }

  canRedo() {
    return this.redoStack.length > 0;
  }

  getHistorial() {
    return [...this.undoStack].reverse();
  }
}
