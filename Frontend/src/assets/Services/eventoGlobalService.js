import axiosInstance from '../../Api/axiosConfig';
import { enriquecerEventosConGeo } from './ipGeolocationService';

export const obtenerEventosGlobales = async (filtros = {}) => {
  try {
    const params = {};
    if (filtros.tipo) params.tipo = filtros.tipo;
    if (filtros.fechaDesde) params.fechaDesde = filtros.fechaDesde;
    if (filtros.fechaHasta) params.fechaHasta = filtros.fechaHasta;
    if (filtros.usuarioId) params.usuarioId = filtros.usuarioId;

    const response = await axiosInstance.get('/api/auditoria/lista', { params });
    const data = (response.data || []).map(item => ({
      id: item.id,
      tipo: item.tipo,
      accion: item.accion,
      descripcion: item.descripcion,
      fecha: item.fecha,
      ipOrigen: item.ip_origen,
      usuarioId: item.usuario_id,
    }));
    const eventosEnriquecidos = await enriquecerEventosConGeo(data);
    return { success: true, data: eventosEnriquecidos };
  } catch (error) {
    return { success: false, data: [], message: error.response?.data || error.message };
  }
};

export const obtenerResumenGlobal = async () => {
  try {
    const [dashboard, ips] = await Promise.all([
      axiosInstance.get('/api/auditoria/dashboard'),
      axiosInstance.get('/api/auditoria/estadisticas/ips'),
    ]);

    const data = {
      totalEventos: dashboard.data?.eventosHoy || 0,
      usuariosActivos: 0,
      paisesDetectados: 0,
      ipsUnicas: (ips.data || []).length,
    };

    try {
      const estadisticas = await axiosInstance.get('/api/auditoria/estadisticas');
      data.usuariosActivos = (estadisticas.data?.top5UsuariosActivos || []).length;
    } catch {
    }

    return { success: true, data };
  } catch (error) {
    return { success: false, data: null, message: error.response?.data || error.message };
  }
};
