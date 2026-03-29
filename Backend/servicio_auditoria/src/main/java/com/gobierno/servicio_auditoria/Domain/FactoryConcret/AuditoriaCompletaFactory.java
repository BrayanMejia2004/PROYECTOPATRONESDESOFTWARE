package com.gobierno.servicio_auditoria.Domain.FactoryConcret;

import java.sql.Timestamp;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

//Fábrica concreta que implementa la creación de auditorías de tipo COMPLETA
public class AuditoriaCompletaFactory extends AuditoriaAbsFactory {

    // Construye una nueva Auditoria con todos los campos disponibles
    // usando el patrón Builder
    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {

        auditoria.setTipo("COMPLETA");
        auditoria.setFecha(new Timestamp((System.currentTimeMillis())));
        return auditoria;
    }

    // Construccion de la respuesta con todos los campos
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
