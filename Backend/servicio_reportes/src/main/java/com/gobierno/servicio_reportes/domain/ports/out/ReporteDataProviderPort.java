package com.gobierno.servicio_reportes.domain.ports.out;

import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

public interface ReporteDataProviderPort {
    
    ReporteData obtenerDatosAuditoria();
    
    ReporteData obtenerDatosUsuarios();
    
    ReporteData obtenerDatosRoles();
    
    ReporteData obtenerDatos(String tipo);
}
