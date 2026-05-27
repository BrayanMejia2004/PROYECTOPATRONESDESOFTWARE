export class DashboardComponent {
  setMediator(mediator) {
    this.mediator = mediator;
  }

  notifyMediator(event, data) {
    if (this.mediator) {
      this.mediator.notify(this, event, data);
    }
  }
}

class DashboardMediator {
  constructor() {
    this.components = {};
  }

  register(name, component) {
    this.components[name] = component;
    if (component?.setMediator) {
      component.setMediator(this);
    }
  }

  notify(sender, event, data) {
    switch (event) {
      case 'FILTROS_CAMBIADOS':
        this.components.mapaGlobal?.aplicarFiltros(data);
        this.components.lineaTiempo?.aplicarFiltros(data);
        this.components.contadores?.aplicarFiltros(data);
        break;

      case 'EVENTO_SELECCIONADO':
        this.components.mapaGlobal?.enfocarEvento(data);
        break;

      case 'PAIS_SELECCIONADO':
        this.components.lineaTiempo?.filtrarPorPais(data);
        this.components.contadores?.filtrarPorPais(data);
        break;

      case 'WEBSOCKET_EVENTO':
        this.components.mapaGlobal?.agregarEvento(data);
        this.components.lineaTiempo?.agregarEvento(data);
        this.components.contadores?.incrementar(data);
        break;

      case 'TIPO_CAMBIADO':
        this.components.mapaGlobal?.filtrarPorTipo(data);
        this.components.lineaTiempo?.filtrarPorTipo(data);
        break;
    }
  }
}

export default DashboardMediator;
