package com.gobierno.servicio_auditoria.Domain.FactoryConcret;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

// Factory para auditorias de seguridad - incluye IP de origen
public class AuditoriaSeguridadFactory extends AuditoriaAbsFactory {

    // Establece el tipo SEGURIDAD y retorna la auditoria
    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {
        auditoria.setTipo("SEGURIDAD");
        return auditoria;
    }

    // Crea respuesta con campos de seguridad (incluye IP)
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
