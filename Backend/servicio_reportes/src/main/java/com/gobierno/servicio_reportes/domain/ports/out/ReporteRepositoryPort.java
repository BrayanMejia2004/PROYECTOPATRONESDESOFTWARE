package com.gobierno.servicio_reportes.domain.ports.out;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import java.util.List;
import java.util.Optional;

public interface ReporteRepositoryPort {
    
    Reporte guardar(Reporte reporte);
    
    List<Reporte> findAll();
    
    List<Reporte> findByTipoOrderByFechaGeneracionDesc(String tipo);
    
    Optional<Reporte> findById(Long id);
}
