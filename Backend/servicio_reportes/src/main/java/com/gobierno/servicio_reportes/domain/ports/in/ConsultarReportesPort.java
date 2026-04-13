package com.gobierno.servicio_reportes.domain.ports.in;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import java.util.List;
import java.util.Optional;

public interface ConsultarReportesPort {
    
    List<Reporte> obtenerHistorial();
    
    List<Reporte> obtenerHistorialPorTipo(String tipo);
    
    Optional<Reporte> obtenerReportePorId(Long id);
}
