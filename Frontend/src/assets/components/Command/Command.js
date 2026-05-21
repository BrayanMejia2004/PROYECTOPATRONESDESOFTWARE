import axiosInstance from '../../../Api/axiosConfig';

export class Command {
  execute() { throw new Error('Debe implementar execute()'); }
  undo() { throw new Error('Debe implementar undo()'); }
  getDescripcion() { return ''; }
}

export class SimularEventoCommand extends Command {
  constructor(evento) {
    super();
    this.evento = evento;
    this.simulacionId = null;
  }

  async execute() {
    const result = await axiosInstance.post('/api/auditoria/simular/evento', this.evento);
    this.simulacionId = result.data.simulacionId;
    return result.data;
  }

  async undo() {
    if (this.simulacionId) {
      await axiosInstance.delete(`/api/auditoria/simular/${this.simulacionId}`);
    }
  }

  getDescripcion() {
    return `Simular: ${this.evento.accion}`;
  }
}

export class SimularEscenarioCommand extends Command {
  constructor(escenario) {
    super();
    this.escenario = escenario;
    this.simulacionId = null;
  }

  async execute() {
    const result = await axiosInstance.post('/api/auditoria/simular/escenario', this.escenario);
    this.simulacionId = result.data.simulacionId;
    return result.data;
  }

  async undo() {
    if (this.simulacionId) {
      await axiosInstance.delete(`/api/auditoria/simular/${this.simulacionId}`);
    }
  }

  getDescripcion() {
    return `Escenario: ${this.escenario.nombre}`;
  }
}
