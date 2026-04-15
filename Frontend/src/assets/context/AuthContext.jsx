import { createContext, useContext, useState, useEffect } from 'react';
import authService from '../Services/authService';
import perfilService from '../Services/perfilService';
import { getUserFromToken, isTokenExpired } from '../Utils/decodeToken';

const AuthContext = createContext(null);

const PERMISSIONS = {
  ADMIN: ['*'],
  USER: ['VER_DASHBOARD'],
  AUDITOR: ['VER_DASHBOARD', 'GENERAR_REPORTE', 'DESCARGAR_REPORTE'],
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(() => {
    const savedToken = localStorage.getItem('token');
    return savedToken || null;
  });
  const [perfil, setPerfil] = useState(null);
  const [loading, setLoading] = useState(true);
  const [perfilLoading, setPerfilLoading] = useState(false);

  useEffect(() => {
    const initAuth = async () => {
      if (token) {
        if (isTokenExpired(token)) {
          logout();
        } else {
          const userData = getUserFromToken(token);
          setUser(userData);
          await checkPerfil();
        }
      }
      setLoading(false);
    };
    initAuth();
  }, [token]);

  const checkPerfil = async () => {
    if (!token) return;
    
    setPerfilLoading(true);
    const result = await perfilService.getPerfil(token);
    setPerfilLoading(false);
    
    if (result.success) {
      setPerfil(result.data);
    } else {
      setPerfil(null);
    }
    
    return result.data;
  };

  const crearPerfil = async (perfilData) => {
    if (!token) return { success: false, message: 'No autenticado' };
    
    const result = await perfilService.crearPerfil(perfilData, token);
    
    if (result.success) {
      setPerfil(result.data);
    }
    
    return result;
  };

  const actualizarPerfil = async (perfilData) => {
    if (!token) return { success: false, message: 'No autenticado' };
    
    const result = await perfilService.actualizarPerfil(perfilData, token);
    
    if (result.success) {
      setPerfil(result.data);
    }
    
    return result;
  };

  const login = async (username, password) => {
    const result = await authService.login(username, password);
    
    if (result.success) {
      localStorage.setItem('token', result.token);
      setToken(result.token);
      const userData = getUserFromToken(result.token);
      setUser(userData);
      await checkPerfil();
    }
    
    return result;
  };

  const registro = async (username, email, password) => {
    return await authService.registro(username, email, password);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
    setPerfil(null);
  };

  const hasPerfilCompleto = !!perfil && !!perfil.nombre && !!perfil.apellido;

  const hasRole = (role) => {
    if (!user?.roles) return false;
    return user.roles.includes(role);
  };

  const hasPermission = (permission) => {
    if (!user?.roles) return false;
    return user.roles.some(role => {
      const rolePermissions = PERMISSIONS[role] || [];
      return rolePermissions.includes('*') || rolePermissions.includes(permission);
    });
  };

  const isAdmin = () => hasRole('ADMIN');
  const isAuditor = () => hasRole('AUDITOR');
  const isUser = () => hasRole('USER');

  const value = {
    user,
    token,
    perfil,
    loading,
    perfilLoading,
    isAuthenticated: !!user && !!token,
    hasPerfilCompleto,
    hasRole,
    hasPermission,
    isAdmin,
    isAuditor,
    isUser,
    login,
    registro,
    logout,
    checkPerfil,
    crearPerfil,
    actualizarPerfil,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;
