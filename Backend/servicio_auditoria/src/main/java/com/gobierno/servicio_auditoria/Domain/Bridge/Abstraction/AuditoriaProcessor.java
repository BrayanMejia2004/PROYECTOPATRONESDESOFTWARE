package com.gobierno.servicio_auditoria.Domain.Bridge.Abstraction;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

// Clase abstracta base para processors de auditoria (Bridge Pattern)
public abstract class AuditoriaProcessor {

    // Referencia a la implementacion (Abstract Factory)
    protected AuditoriaAbsFactory factory;

    // Constructor que recibe la factory
    public AuditoriaProcessor(AuditoriaAbsFactory factory) {
        this.factory = factory;
    }

    // Metodo abstracto que cada processor debe implementar
    public abstract AuditoriaResponse procesar(Auditoria auditoria);
}
