import { EstrategiaVolumen } from './EstrategiaVolumen.jsx';
import { EstrategiaSeveridad } from './EstrategiaSeveridad.jsx';
import { EstrategiaTendencia } from './EstrategiaTendencia.jsx';
import { EstrategiaComparativa } from './EstrategiaComparativa.jsx';
import { obtenerAnalisis } from '../../Services/analisis/analisisService';

const ESTRATEGIAS_REGISTRY = {
  VOLUMEN: new EstrategiaVolumen(),
  SEVERIDAD: new EstrategiaSeveridad(),
  TENDENCIA: new EstrategiaTendencia(),
  COMPARATIVA: new EstrategiaComparativa()
};

const ESTRATEGIAS_INFO = Object.values(ESTRATEGIAS_REGISTRY).map(e => ({
  tipo: e.getTipo(),
  nombre: e.getNombre(),
  descripcion: e.getDescripcion(),
  icono: e.getIcono()
}));

export const obtenerEstrategiasInfo = () => ESTRATEGIAS_INFO;

export class AnalizadorContext {
  constructor() {
    this.estrategiaActiva = null;
    this.listeners = [];
    this.datosBrutos = null;
    this.resultadoAnalisis = null;
  }

  setEstrategia(tipo) {
    const estrategia = ESTRATEGIAS_REGISTRY[tipo];
    if (!estrategia) return;
    this.estrategiaActiva = estrategia;
    this.notifyListeners();
    if (this.datosBrutos) {
      this.ejecutarAnalisis(this.datosBrutos);
    }
  }

  getEstrategia() {
    return this.estrategiaActiva;
  }

  getEstrategiaTipo() {
    return this.estrategiaActiva ? this.estrategiaActiva.getTipo() : null;
  }

  async ejecutarAnalisis(dataBruta) {
    if (!this.estrategiaActiva) return null;
    this.datosBrutos = dataBruta;
    const datosProcesados = this.estrategiaActiva.procesarDatos(dataBruta);
    this.resultadoAnalisis = {
      estrategia: this.estrategiaActiva,
      datos: datosProcesados,
      vista: this.estrategiaActiva.renderizar(datosProcesados),
      insights: datosProcesados.insights || []
    };
    return this.resultadoAnalisis;
  }

  async cargarYAnalizar(filtros) {
    try {
      const response = await obtenerAnalisis(
        this.getEstrategiaTipo(),
        filtros.fechaDesde,
        filtros.fechaHasta,
        filtros.filtros
      );
      return await this.ejecutarAnalisis(response.data);
    } catch (err) {
      throw err;
    }
  }

  getResultado() {
    return this.resultadoAnalisis;
  }

  subscribe(listener) {
    this.listeners.push(listener);
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener);
    };
  }

  notifyListeners() {
    this.listeners.forEach(l => l(this.estrategiaActiva));
  }
}
