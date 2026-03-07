package com.gobierno.servicio_auditoria.Ports.Output;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

// Interfaz para el servicio de registro de auditoría
public interface RegistroAuditoria {

    // Método para registrar una acción de auditoría
    void registrarAccion(Auditoria auditoria);
    
}
