package com.gobierno.servicio_reportes.domain.ports.out;

import java.sql.Timestamp;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

public interface ReporteDataProviderPort {
    
    ReporteData obtenerDatosAuditoria();
    
    ReporteData obtenerDatosAuditoriaFiltrado(
        Integer usuarioId,
        Timestamp fechaDesde,
        Timestamp fechaHasta,
        String tipo,
        String accion
    );
    
    ReporteData obtenerDatosUsuarios();
    
    ReporteData obtenerDatosRoles();
    
    ReporteData obtenerDatos(String tipo);
}
