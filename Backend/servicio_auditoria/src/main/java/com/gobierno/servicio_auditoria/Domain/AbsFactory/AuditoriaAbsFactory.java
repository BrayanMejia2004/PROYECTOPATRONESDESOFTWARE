package com.gobierno.servicio_auditoria.Domain.AbsFactory;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

// Clase abstracta base para factories de auditoria
public abstract class AuditoriaAbsFactory {

    // Metodo abstracto para crear un objeto Auditoria segun tipo
    public abstract Auditoria crearAuditoria(Auditoria auditoria);
    
    // Metodo abstracto para crear una respuesta segun tipo
    public abstract AuditoriaResponse crearRespuesta(Auditoria auditoria);
}
