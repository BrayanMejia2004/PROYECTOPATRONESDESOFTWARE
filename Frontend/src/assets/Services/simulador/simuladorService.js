import axiosInstance from '../../../Api/axiosConfig';

export const simularEvento = (evento) =>
  axiosInstance.post('/api/auditoria/simular/evento', evento);

export const simularLote = (eventos) =>
  axiosInstance.post('/api/auditoria/simular/lote', eventos);

export const ejecutarEscenario = (escenario) =>
  axiosInstance.post('/api/auditoria/simular/escenario', escenario);

export const deshacerSimulacion = (simulacionId) =>
  axiosInstance.delete(`/api/auditoria/simular/${simulacionId}`);

export const obtenerEscenarios = () =>
  axiosInstance.get('/api/auditoria/simular/escenarios');
