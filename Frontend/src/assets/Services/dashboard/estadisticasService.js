import axiosInstance from '../../../Api/axiosConfig';

export const obtenerEstadisticas = async () => {
  try {
    const response = await axiosInstance.get('/api/auditoria/estadisticas');
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: null, message: error.response?.data || error.message };
  }
};
