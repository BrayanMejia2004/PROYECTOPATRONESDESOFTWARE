package com.gobierno.servicio_auditoria.domain.factory;

import java.sql.Timestamp;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public class AuditoriaCompletaFactory extends AuditoriaAbsFactory {  // Concrete Factory para auditoría completa

    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {  // Crea auditoría de tipo completo
        auditoria.setTipo("COMPLETA");  // Establece el tipo como COMPLETO
        auditoria.setFecha(new Timestamp((System.currentTimeMillis())));  // Asigna la fecha actual
        return auditoria;  // Retorna la auditoría
    }

    @Override
    public AuditoriaResponse crearRespuesta(Auditoria auditoria) {  // Crea respuesta para auditoría completa
        return new AuditoriaResponse.Builder()
                .usuario(auditoria.getUsuario_id())  // Agrega el usuario
                .accion(auditoria.getAccion())  // Agrega la acción
                .descripcion(auditoria.getDescripcion())  // Agrega la descripción
                .ip(auditoria.getIp_origen())  // Agrega la IP de origen
                .fecha(auditoria.getFecha())  // Agrega la fecha
                .tipo(auditoria.getTipo())  // Agrega el tipo
                .build();  // Construye la respuesta
    }
}