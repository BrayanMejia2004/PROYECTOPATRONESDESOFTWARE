package com.gobierno.servicio_auditoria.domain.factory;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public abstract class AuditoriaAbsFactory {

    public abstract Auditoria crearAuditoria(Auditoria auditoria);
    
    public abstract AuditoriaResponse crearRespuesta(Auditoria auditoria);
}
