import axiosInstance from '../../Api/axiosConfig';

const authService = {
  login: async (username, password) => {
    try {
      const response = await axiosInstance.post('/api/usuarios/login', {
        username,
        password,
      });
      return {
        success: true,
        token: response.data,
        message: 'Login exitoso',
      };
    } catch (error) {
      const message =
        error.response?.data?.message ||
        error.response?.data ||
        'Credenciales inválidas';
      return {
        success: false,
        token: null,
        message: Array.isArray(message) ? message.join(', ') : message,
      };
    }
  },

  registro: async (username, email, password) => {
    try {
      const response = await axiosInstance.post('/api/usuarios/registro', {
        username,
        email,
        password,
      });
      return {
        success: true,
        data: response.data,
        message: 'Usuario registrado exitosamente',
      };
    } catch (error) {
      const message =
        error.response?.data?.message ||
        error.response?.data ||
        'Error al registrar usuario';
      return {
        success: false,
        data: null,
        message: Array.isArray(message) ? message.join(', ') : message,
      };
    }
  },

  validarToken: async (token) => {
    try {
      const response = await axiosInstance.get('/api/usuarios/validar', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        data: null,
      };
    }
  },
};

export default authService;
