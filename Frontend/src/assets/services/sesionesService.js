import axiosInstance from '../../Api/axiosConfig';

export const obtenerSesionesActivas = async () => {
  try {
    const response = await axiosInstance.get('/api/usuarios/sesiones/activas');
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: null, message: error.response?.data || error.message };
  }
};

export const revocarSesion = async (id) => {
  try {
    const response = await axiosInstance.delete(`/api/usuarios/sesiones/${id}`);
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: null, message: error.response?.data || error.message };
  }
};

export const obtenerMetricas = async () => {
  try {
    const response = await axiosInstance.get('/api/usuarios/sesiones/metricas');
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: null, message: error.response?.data || error.message };
  }
};
