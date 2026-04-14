package com.gobierno.servicio_auditoria.domain.factory;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public class AuditoriaBasicaFactory extends AuditoriaAbsFactory {

    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {
        auditoria.setTipo("BASICA");
        return auditoria;
    }

    @Override
    public AuditoriaResponse crearRespuesta(Auditoria auditoria) {
        return new AuditoriaResponse.Builder()
                .usuario(auditoria.getUsuario_id())
                .accion(auditoria.getAccion())
                .descripcion(auditoria.getDescripcion())
                .tipo(auditoria.getTipo())
                .build();
    }
}
