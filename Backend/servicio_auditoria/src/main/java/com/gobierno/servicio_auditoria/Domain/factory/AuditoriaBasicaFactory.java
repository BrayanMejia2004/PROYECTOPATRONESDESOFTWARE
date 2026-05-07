package com.gobierno.servicio_auditoria.domain.factory;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public class AuditoriaBasicaFactory extends AuditoriaAbsFactory { // Concrete Factory para auditoría básica

    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) { // Crea auditoría de tipo básico
        auditoria.setTipo("BASICA"); // Establece el tipo como BASICO
        return auditoria; // Retorna la auditoría
    }

    @Override
    public AuditoriaResponse crearRespuesta(Auditoria auditoria) { // Crea respuesta para auditoría básica
        return new AuditoriaResponse.Builder()
                .usuario(auditoria.getUsuario_id()) // Agrega el usuario
                .accion(auditoria.getAccion()) // Agrega la acción
                .descripcion(auditoria.getDescripcion()) // Agrega la descripción
                .build(); // Construye la respuesta
    }
}