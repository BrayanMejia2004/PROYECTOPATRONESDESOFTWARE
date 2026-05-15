import axiosInstance from '../../Api/axiosConfig';

export const obtenerAmenazasActivas = async () => {
  try {
    const response = await axiosInstance.get('/api/auditoria/threats/activas');
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: [], message: error.response?.data || error.message };
  }
};

export const obtenerHistorialAmenazas = async (desde, hasta) => {
  try {
    const params = {};
    if (desde) params.desde = desde;
    if (hasta) params.hasta = hasta;
    const response = await axiosInstance.get('/api/auditoria/threats/historial', { params });
    return { success: true, data: response.data };
  } catch (error) {
    return { success: false, data: [], message: error.response?.data || error.message };
  }
};

export const conectarStreamAmenazas = (onThreat) => {
  const token = localStorage.getItem('token');
  const eventSource = new EventSource(`http://localhost:8080/api/auditoria/threats/stream`);
  eventSource.addEventListener('threat', (event) => {
    try {
      const threat = JSON.parse(event.data);
      onThreat(threat);
    } catch (e) {
      console.error('Error parsing SSE threat data:', e);
    }
  });
  eventSource.onerror = () => {
    console.warn('SSE connection error, will retry...');
  };
  return eventSource;
};

export const resolverAmenaza = async (id) => {
  try {
    await axiosInstance.put(`/api/auditoria/threats/${id}/resolver`);
    return { success: true };
  } catch (error) {
    return { success: false, message: error.response?.data || error.message };
  }
};
