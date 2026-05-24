export class TimelineIterador {
  constructor(usuarioId, obtenerFn) {
    this.usuarioId = usuarioId;
    this.obtenerFn = obtenerFn;
    this._eventos = [];
    this._limite = 50;
  }

  get eventos() {
    return this._eventos;
  }

  get limite() {
    return this._limite;
  }

  async cargar() {
    const result = await this.obtenerFn(this.usuarioId, this._limite);
    if (result.success) {
      this._eventos = result.data || [];
    }
    return result;
  }

  async cargarMas() {
    this._limite += 50;
    return this.cargar();
  }

  hayMas() {
    return this._eventos.length >= this._limite;
  }

  reiniciar() {
    this._limite = 50;
    this._eventos = [];
  }
}

export class CronologicoIterador extends TimelineIterador {}

export class InversoIterador extends TimelineIterador {
  get eventos() {
    return [...this._eventos].reverse();
  }
}

export class FiltroIterador extends TimelineIterador {
  constructor(usuarioId, obtenerFn, filtroFn) {
    super(usuarioId, obtenerFn);
    this.filtroFn = filtroFn;
  }

  get eventos() {
    if (!this.filtroFn) return this._eventos;
    return this._eventos.filter(this.filtroFn);
  }
}
