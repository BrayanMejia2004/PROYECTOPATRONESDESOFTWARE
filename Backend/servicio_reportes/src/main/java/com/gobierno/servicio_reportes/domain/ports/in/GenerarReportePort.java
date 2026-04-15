package com.gobierno.servicio_reportes.domain.ports.in;

import java.sql.Timestamp;

import com.gobierno.servicio_reportes.domain.entities.Reporte;

public interface GenerarReportePort {
    
    byte[] generarReporte(String tipo, String formato, String usuarioSolicitante);
    
    byte[] generarReporte(String tipo, String formato, String usuarioSolicitante,
                          Integer usuarioId, Timestamp fechaDesde, Timestamp fechaHasta,
                          String accion, String tipoAuditoria);
    
    Reporte guardarReporte(String tipo, String titulo, String descripcion, 
                           byte[] contenido, String formato, String usuarioSolicitante);
}
