import axiosInstance from '../../Api/axiosConfig';

const perfilService = {
  getPerfil: async (token) => {
    try {
      const response = await axiosInstance.get('/api/usuarios/perfil', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return {
        success: true,
        data: response.data,
        message: 'Perfil obtenido exitosamente',
      };
    } catch (error) {
      if (error.response?.status === 404) {
        return {
          success: true,
          data: null,
          message: 'Perfil no encontrado',
          notFound: true,
        };
      }
      return {
        success: false,
        data: null,
        message: error.response?.data?.message || 'Error al obtener perfil',
      };
    }
  },

  crearPerfil: async (perfilData, token) => {
    try {
      const response = await axiosInstance.post('/api/usuarios/perfil', perfilData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return {
        success: true,
        data: response.data,
        message: 'Perfil creado exitosamente',
      };
    } catch (error) {
      const message =
        error.response?.data?.message ||
        error.response?.data ||
        'Error al crear perfil';
      return {
        success: false,
        data: null,
        message: Array.isArray(message) ? message.join(', ') : message,
      };
    }
  },

  actualizarPerfil: async (perfilData, token) => {
    try {
      const response = await axiosInstance.put('/api/usuarios/perfil', perfilData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return {
        success: true,
        data: response.data,
        message: 'Perfil actualizado exitosamente',
      };
    } catch (error) {
      const message =
        error.response?.data?.message ||
        error.response?.data ||
        'Error al actualizar perfil';
      return {
        success: false,
        data: null,
        message: Array.isArray(message) ? message.join(', ') : message,
      };
    }
  },
};

export default perfilService;
