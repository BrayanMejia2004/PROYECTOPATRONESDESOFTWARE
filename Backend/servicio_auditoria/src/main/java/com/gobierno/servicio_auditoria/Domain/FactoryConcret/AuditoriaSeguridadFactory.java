package com.gobierno.servicio_auditoria.Domain.FactoryConcret;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

//Fábrica concreta que implementa la creación de auditorías de tipo SEGURIDAD
public class AuditoriaSeguridadFactory extends AuditoriaAbsFactory {

    // Construye una Auditoria con campos básicos más la ip (sin fecha)
    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {

        return new Auditoria.Builder()
                .usuario(auditoria.getUsuario_id())
                .accion(auditoria.getAccion())
                .descripcion(auditoria.getDescripcion())
                .ip(auditoria.getIp_origen())
                .tipo("SEGURIDAD")
                .build();
    }

    // Construccion de la respuesta con todos los campos
    @Override
    public AuditoriaResponse crearRespuesta(Auditoria auditoria) {

        return new AuditoriaResponse.Builder()
                .usuario(auditoria.getUsuario_id())
                .accion(auditoria.getAccion())
                .descripcion(auditoria.getDescripcion())
                .ip(auditoria.getIp_origen())
                .tipo(auditoria.getTipo())
                .build();
    }

}
