package com.gobierno.servicio_auditoria.Domain.AbsFactory;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

// Interfaz para la fábrica abstracta de auditoría
public abstract class AuditoriaAbsFactory {

    public abstract Auditoria crearAuditoria(Auditoria auditoria);

    public abstract AuditoriaResponse crearRespuesta(Auditoria auditoria);
    
}
