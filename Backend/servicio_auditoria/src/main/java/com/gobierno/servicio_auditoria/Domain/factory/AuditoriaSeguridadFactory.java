package com.gobierno.servicio_auditoria.domain.factory;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public class AuditoriaSeguridadFactory extends AuditoriaAbsFactory {  // Concrete Factory para auditoría de seguridad

    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {  // Crea auditoría de tipo seguridad
        auditoria.setTipo("SEGURIDAD");  // Establece el tipo como SEGURIDAD
        return auditoria;  // Retorna la auditoría
    }

    @Override
    public AuditoriaResponse crearRespuesta(Auditoria auditoria) {  // Crea respuesta para auditoría de seguridad
        return new AuditoriaResponse.Builder()
                .usuario(auditoria.getUsuario_id())  // Agrega el usuario
                .accion(auditoria.getAccion())  // Agrega la acción
                .descripcion(auditoria.getDescripcion())  // Agrega la descripción
                .ip(auditoria.getIp_origen())  // Agrega la IP de origen
                .tipo(auditoria.getTipo())  // Agrega el tipo
                .build();  // Construye la respuesta
    }
}