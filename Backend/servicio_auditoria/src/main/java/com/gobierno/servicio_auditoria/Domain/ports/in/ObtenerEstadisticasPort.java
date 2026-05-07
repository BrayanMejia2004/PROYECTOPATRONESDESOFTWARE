package com.gobierno.servicio_auditoria.domain.ports.in;

import com.gobierno.servicio_auditoria.domain.model.EstadisticasDTO;

public interface ObtenerEstadisticasPort {
    EstadisticasDTO obtenerEstadisticas();
}
