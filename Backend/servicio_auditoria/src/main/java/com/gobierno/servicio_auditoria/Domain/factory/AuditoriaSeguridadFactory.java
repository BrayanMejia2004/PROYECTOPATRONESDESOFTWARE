package com.gobierno.servicio_auditoria.domain.factory;

import java.sql.Timestamp;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public class AuditoriaSeguridadFactory extends AuditoriaAbsFactory { // Concrete Factory para auditoría de seguridad

    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) { // Crea auditoría de tipo seguridad
        auditoria.setTipo("SEGURIDAD"); // Establece el tipo como SEGURIDAD
        auditoria.setFecha(new Timestamp((System.currentTimeMillis()))); // Asigna la fecha actual
        return auditoria; // Retorna la auditoría
    }

    @Override
    public AuditoriaResponse crearRespuesta(Auditoria auditoria) { // Crea respuesta para auditoría de seguridad
        return new AuditoriaResponse.Builder()
                .usuario(auditoria.getUsuario_id()) // Agrega el usuario
                .accion(auditoria.getAccion()) // Agrega la acción
                .descripcion(auditoria.getDescripcion()) // Agrega la descripción
                .fecha(auditoria.getFecha())
                .tipo(auditoria.getTipo()) // Agrega el tipo
                .build(); // Construye la respuesta
    }
}