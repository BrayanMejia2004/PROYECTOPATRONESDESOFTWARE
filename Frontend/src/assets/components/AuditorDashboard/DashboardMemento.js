export class DashboardMemento {
  constructor(data, timestamp) {
    this.data = data;
    this.timestamp = timestamp;
  }

  getData() {
    return this.data;
  }

  getTimestamp() {
    return this.timestamp;
  }
}

export class DashboardOriginator {
  guardar(data) {
    return new DashboardMemento(
      JSON.parse(JSON.stringify(data)),
      Date.now()
    );
  }

  restaurar(memento) {
    if (!memento) return null;
    return memento.getData();
  }
}

class DashboardCaretaker {
  constructor() {
    this.memento = null;
  }

  guardar(memento) {
    this.memento = memento;
  }

  recuperar() {
    return this.memento;
  }

  hayMementoValido(segundos = 30) {
    if (!this.memento) return false;
    const edad = (Date.now() - this.memento.getTimestamp()) / 1000;
    return edad < segundos;
  }

  limpiar() {
    this.memento = null;
  }
}

export const dashboardCaretaker = new DashboardCaretaker();
