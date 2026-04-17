package com.gobierno.servicio_auditoria.domain.factory;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public abstract class AuditoriaAbsFactory {  // Clase abstracta (Abstract Factory) del patrón

    public abstract Auditoria crearAuditoria(Auditoria auditoria);  // Método abstracto para crear auditoría
    
    public abstract AuditoriaResponse crearRespuesta(Auditoria auditoria);  // Método abstracto para crear respuesta
}