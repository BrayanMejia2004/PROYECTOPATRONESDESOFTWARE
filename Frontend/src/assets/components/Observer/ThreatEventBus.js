class ThreatEventBus {
  static _nextId = 0;

  constructor() {
    this.observers = [];
    this.threats = [];
  }

  attach(observer) {
    if (!observer._obsId) observer._obsId = ++ThreatEventBus._nextId;
    if (!this.observers.some(o => o._obsId === observer._obsId)) {
      this.observers.push(observer);
    }
  }

  detach(observer) {
    this.observers = this.observers.filter(o => o._obsId !== observer._obsId);
  }

  notify(threat) {
    if (this.threats.some(t => t.id === threat.id)) return;
    this.threats.push(threat);
    for (const observer of this.observers) {
      if (typeof observer.update === 'function') {
        observer.update(threat);
      }
    }
  }

  getActiveThreats() {
    return this.threats.filter(t => t.activa !== false);
  }

  clearResolved(id) {
    const threat = this.threats.find(t => t.id === id);
    if (threat) threat.activa = false;
  }

  getAllThreats() {
    return this.threats;
  }
}

const threatEventBus = new ThreatEventBus();
export default threatEventBus;
