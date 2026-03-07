package com.gobierno.servicio_auditoria.Infrastructure.Service;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Ports.Output.NotificacionAuditoria;

public class NotificacionSeguridad implements NotificacionAuditoria {

    @Override
    public void enviarNotificacion(Auditoria auditoria) {
        
        auditoria.setDescripcion(auditoria.getDescripcion() + " | ALERTA DE SEGURIDAD");
    }
    
}
