import axiosInstance from '../../Api/axiosConfig';

const ipEstadisticasService = {
  obtenerMapaCalor: async (desde, hasta) => {
    try {
      const params = {};
      if (desde) params.desde = desde;
      if (hasta) params.hasta = hasta;
      const response = await axiosInstance.get('/api/auditoria/estadisticas/ips', { params });
      return { success: true, data: response.data };
    } catch (error) {
      const msg = error.response?.data;
      return { success: false, data: null, message: typeof msg === 'string' ? msg : (msg?.error || 'Error al cargar mapa de calor') };
    }
  },

  obtenerDetalleIp: async (ip, limite = 20) => {
    try {
      const response = await axiosInstance.get(`/api/auditoria/estadisticas/ips/${encodeURIComponent(ip)}/detalle`, {
        params: { limite }
      });
      return { success: true, data: response.data };
    } catch (error) {
      const msg = error.response?.data;
      return { success: false, data: null, message: typeof msg === 'string' ? msg : (msg?.error || 'Error al obtener detalle de IP') };
    }
  }
};

export default ipEstadisticasService;
