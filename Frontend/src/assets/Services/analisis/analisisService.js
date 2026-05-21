import axiosInstance from '../../../Api/axiosConfig';

export const obtenerAnalisis = (estrategia, fechaDesde, fechaHasta, filtros) =>
  axiosInstance.post('/api/auditoria/analisis', {
    estrategia, fechaDesde, fechaHasta, filtros
  });

export const obtenerEstrategias = () =>
  axiosInstance.get('/api/auditoria/analisis/estrategias');

export const obtenerInsights = () =>
  axiosInstance.get('/api/auditoria/analisis/insights');
