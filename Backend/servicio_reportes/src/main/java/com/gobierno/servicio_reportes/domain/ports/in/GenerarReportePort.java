package com.gobierno.servicio_reportes.domain.ports.in;

import com.gobierno.servicio_reportes.domain.entities.Reporte;

public interface GenerarReportePort {
    
    byte[] generarReporte(String tipo, String formato, String usuarioSolicitante);
    
    Reporte guardarReporte(String tipo, String titulo, String descripcion, 
                           byte[] contenido, String formato, String usuarioSolicitante);
}
