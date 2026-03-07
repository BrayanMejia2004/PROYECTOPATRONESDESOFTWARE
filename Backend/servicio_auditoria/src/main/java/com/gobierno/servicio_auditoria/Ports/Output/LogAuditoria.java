package com.gobierno.servicio_auditoria.Ports.Output;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

// Interfaz para el servicio de Log de Auditoría
public interface LogAuditoria {
    
    // Método para generar un log de auditoría a partir de una entidad Auditoria
    void generarLog(Auditoria auditoria);
}
