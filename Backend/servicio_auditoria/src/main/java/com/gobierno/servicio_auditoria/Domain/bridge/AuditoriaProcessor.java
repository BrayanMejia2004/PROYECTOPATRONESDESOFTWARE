package com.gobierno.servicio_auditoria.domain.bridge;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.factory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public abstract class AuditoriaProcessor {

    protected AuditoriaAbsFactory factory;

    public AuditoriaProcessor(AuditoriaAbsFactory factory) {
        this.factory = factory;
    }

    public abstract AuditoriaResponse procesar(Auditoria auditoria);
}
