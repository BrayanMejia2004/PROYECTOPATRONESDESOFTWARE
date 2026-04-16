import axiosInstance from '../../Api/axiosConfig';

const reportesService = {
  generarReporte: async (tipo, formato, filtros, token, secciones) => {
    try {
      const params = new URLSearchParams();
      params.append('formato', formato);
      params.append('usuario', filtros?.usuario || 'SYSTEM');
      
      if (filtros) {
        if (filtros.usuarioId) params.append('usuarioId', filtros.usuarioId);
        if (filtros.fechaDesde) params.append('fechaDesde', filtros.fechaDesde);
        if (filtros.fechaHasta) params.append('fechaHasta', filtros.fechaHasta);
        if (filtros.tipo) params.append('tipoAuditoria', filtros.tipo);
        if (filtros.accion) params.append('accion', filtros.accion);
      }
      
      if (secciones) {
        params.append('secciones', secciones);
      }
      
      const response = await axiosInstance.get(
        `/api/reportes/${tipo}?${params.toString()}`,
        {
          headers: { Authorization: `Bearer ${token}` },
          responseType: 'blob',
        }
      );
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, message: error.message };
    }
  },

  obtenerHistorial: async (token, tipo) => {
    try {
      const params = tipo ? `?tipo=${tipo}` : '';
      const response = await axiosInstance.get(`/api/reportes/historial${params}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, message: error.message };
    }
  },

  descargarBlob: (blobData, nombreArchivo) => {
    const url = window.URL.createObjectURL(new Blob([blobData]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', nombreArchivo);
    document.body.appendChild(link);
    link.click();
    link.parentNode.removeChild(link);
    window.URL.revokeObjectURL(url);
  },
};

export default reportesService;
