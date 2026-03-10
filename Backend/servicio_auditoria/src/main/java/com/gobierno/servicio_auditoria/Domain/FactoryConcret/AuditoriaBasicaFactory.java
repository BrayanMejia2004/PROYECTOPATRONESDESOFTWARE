package com.gobierno.servicio_auditoria.Domain.FactoryConcret;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

public class AuditoriaBasicaFactory extends AuditoriaAbsFactory {

    @Override
    public Auditoria creAuditoria(Auditoria auditoria) {

        auditoria.setTipo("BASICA");
        return auditoria;
    }

    @Override
    public AuditoriaResponse crearRespuesta(Auditoria auditoria) {

        AuditoriaResponse response = new AuditoriaResponse();

        response.setUsuario(auditoria.getUsuario_id());
        response.setAccion(auditoria.getAccion());
        response.setDescripcion(auditoria.getDescripcion());
        response.setTipo(auditoria.getTipo());

        return response;
    }
    
}
