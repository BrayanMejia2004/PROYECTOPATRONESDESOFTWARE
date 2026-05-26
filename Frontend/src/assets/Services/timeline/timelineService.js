import axiosInstance from '../../../Api/axiosConfig';

export const obtenerTimeline = async (usuarioId, limite = 50) => {
  try {
    const response = await axiosInstance.get(`/api/auditoria/usuario/${usuarioId}/timeline?limite=${limite}`);
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: null, message: error.response?.data || 'Error de conexión' };
  }
};

export const obtenerResumenUsuario = async (id) => {
  try {
    const response = await axiosInstance.get(`/api/usuarios/${id}/resumen`);
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: null, message: error.response?.data || 'Error de conexión' };
  }
};

export const obtenerActividadCalendario = async (usuarioId, dias = 28) => {
  try {
    const response = await axiosInstance.get(`/api/auditoria/usuario/${usuarioId}/actividad-calendario?dias=${dias}`);
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: null, message: error.response?.data || 'Error de conexión' };
  }
};
