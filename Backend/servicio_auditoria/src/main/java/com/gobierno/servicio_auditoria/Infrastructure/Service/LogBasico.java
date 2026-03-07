package com.gobierno.servicio_auditoria.Infrastructure.Service;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Ports.Output.LogAuditoria;

public class LogBasico implements LogAuditoria{

    @Override
    public void generarLog(Auditoria auditoria) {
        
        auditoria.setDescripcion("Auditoria Basica: " + auditoria.getDescripcion());
    }
    
}
