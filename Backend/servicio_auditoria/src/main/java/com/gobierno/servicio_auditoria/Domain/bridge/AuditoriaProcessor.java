package com.gobierno.servicio_auditoria.domain.bridge;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.factory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public abstract class AuditoriaProcessor {  // Clase abstracta (Abstraction) del patrón Bridge

    protected AuditoriaAbsFactory factory;  // Referencia a la implementación (factory)

    public AuditoriaProcessor(AuditoriaAbsFactory factory) {  // Constructor que recibe la factory
        this.factory = factory;
    }

    public abstract AuditoriaResponse procesar(Auditoria auditoria);  // Método abstracto para procesar auditoría
}