import axiosInstance from '../../../Api/axiosConfig';

const adminService = {
  obtenerUsuarios: async (token, page = 0, size = 5) => {
    try {
      const response = await axiosInstance.get(`/api/usuarios/lista?page=${page}&size=${size}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return {
        success: true,
        data: response.data.content,
        totalPages: response.data.totalPages,
        totalElements: response.data.totalElements,
        currentPage: response.data.number,
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || 'Error al obtener usuarios',
      };
    }
  },

  actualizarUsuario: async (username, nuevoUsername, email, password, token) => {
    try {
      const response = await axiosInstance.put(
        `/api/usuarios/${username}`,
        {
          username: nuevoUsername,
          email: email,
          password: password || null
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      return {
        success: true,
        data: response.data,
        message: 'Usuario actualizado exitosamente',
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || 'Error al actualizar usuario',
      };
    }
  },

  eliminarUsuario: async (username, token) => {
    try {
      const response = await axiosInstance.delete(`/api/usuarios/${username}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return {
        success: true,
        data: response.data,
        message: 'Usuario eliminado exitosamente',
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || 'Error al eliminar usuario',
      };
    }
  },

  obtenerRoles: async (token) => {
    try {
      const response = await axiosInstance.get('/api/roles/lista', {
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
        message: error.response?.data || 'Error al obtener roles',
      };
    }
  },

  crearRol: async (nombreRol, descripcion, permisos, token, username) => {
    try {
      const response = await axiosInstance.post(
        `/api/roles/crear/${nombreRol}`,
        { 
          descripcion: descripcion,
          permisos: permisos || []
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'X-Usuario': username,
          },
        }
      );
      return {
        success: true,
        data: response.data,
        message: `Rol ${nombreRol} creado exitosamente`,
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || `Error al crear rol ${nombreRol}`,
      };
    }
  },

  actualizarRol: async (nombreActual, nuevoNombre, descripcion, token, username) => {
    try {
      const response = await axiosInstance.put(
        `/api/roles/${nombreActual}`,
        {
          nuevoNombre: nuevoNombre || nombreActual,
          descripcion: descripcion
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'X-Usuario': username,
          },
        }
      );
      return {
        success: true,
        data: response.data,
        message: 'Rol actualizado exitosamente',
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || 'Error al actualizar rol',
      };
    }
  },

  eliminarRol: async (nombreRol, token, username) => {
    try {
      const response = await axiosInstance.delete(`/api/roles/${nombreRol}`, {
        headers: {
          Authorization: `Bearer ${token}`,
          'X-Usuario': username,
        },
      });
      return {
        success: true,
        data: response.data,
        message: `Rol ${nombreRol} eliminado exitosamente`,
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || `Error al eliminar rol ${nombreRol}`,
      };
    }
  },

  obtenerRolesDeUsuario: async (username, token) => {
    try {
      const response = await axiosInstance.get(`/api/usuarios/${username}/roles`, {
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
        message: error.response?.data || 'Error al obtener roles del usuario',
      };
    }
  },

  asignarRolAUsuario: async (username, rol, token) => {
    try {
      const response = await axiosInstance.post(
        `/api/usuarios/${username}/roles/${rol}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      return {
        success: true,
        data: response.data,
        message: `Rol ${rol} asignado exitosamente`,
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || `Error al asignar rol ${rol}`,
      };
    }
  },

  quitarRolAUsuario: async (username, rol, token) => {
    try {
      const response = await axiosInstance.delete(`/api/usuarios/${username}/roles/${rol}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return {
        success: true,
        data: response.data,
        message: `Rol ${rol} removido exitosamente`,
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || `Error al quitar rol ${rol}`,
      };
    }
  },

  obtenerPermisosDeRol: async (nombreRol, token) => {
    try {
      const response = await axiosInstance.get(`/api/roles/${nombreRol}/permisos`, {
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
        message: error.response?.data || 'Error al obtener permisos del rol',
      };
    }
  },

  asignarPermisosARol: async (nombreRol, permisos, token, username) => {
    try {
      const response = await axiosInstance.post(
        `/api/roles/${nombreRol}/permisos`,
        { permisos: permisos },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'X-Usuario': username,
          },
        }
      );
      return {
        success: true,
        data: response.data,
        message: 'Permisos asignados exitosamente',
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || 'Error al asignar permisos',
      };
    }
  },

  crearPermiso: async (nombrePermiso, token) => {
    try {
      const response = await axiosInstance.post(
        '/api/permisos',
        { nombre: nombrePermiso },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      return {
        success: true,
        data: response.data,
        message: 'Permiso creado exitosamente',
      };
    } catch (error) {
      return {
        success: false,
        data: null,
        message: error.response?.data || 'Error al crear permiso',
      };
    }
  },

  obtenerPermisos: async (token) => {
    try {
      const response = await axiosInstance.get('/api/permisos', {
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
        message: error.response?.data || 'Error al obtener permisos',
      };
    }
  },
};

export default adminService;
