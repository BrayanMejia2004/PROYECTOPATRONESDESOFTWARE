package com.gobierno.servicio_auditoria.domain.factory;

import java.sql.Timestamp;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public class AuditoriaCompletaFactory extends AuditoriaAbsFactory {

    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {
        auditoria.setTipo("COMPLETA");
        auditoria.setFecha(new Timestamp((System.currentTimeMillis())));
        return auditoria;
    }

    @Override
    public AuditoriaResponse crearRespuesta(Auditoria auditoria) {
        return new AuditoriaResponse.Builder()
                .usuario(auditoria.getUsuario_id())
                .accion(auditoria.getAccion())
                .descripcion(auditoria.getDescripcion())
                .ip(auditoria.getIp_origen())
                .fecha(auditoria.getFecha())
                .tipo(auditoria.getTipo())
                .build();
    }
}
