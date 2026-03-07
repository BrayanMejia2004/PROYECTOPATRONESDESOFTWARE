package com.gobierno.servicio_auditoria.Ports.Output;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

// Interfaz para la Notificación de Auditoría
public interface NotificacionAuditoria {
    
    // Método para enviar una notificación de auditoría
    void enviarNotificacion(Auditoria auditoria);
}
