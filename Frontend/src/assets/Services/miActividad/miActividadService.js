import axiosInstance from '../../../Api/axiosConfig';

export const obtenerMiActividad = async () => {
  try {
    const response = await axiosInstance.get('/api/usuarios/mi-actividad');
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: null, message: error.response?.data || error.message };
  }
};
