
import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080", // API Gateway
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para agregar token JWT automáticamente
axiosInstance.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para redirigir al login si el token fue revocado o expiró
axiosInstance.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401 && !window.location.pathname.includes('/login')) {
      localStorage.removeItem('token');
      localStorage.removeItem('perfil');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;