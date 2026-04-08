package com.gobierno.servicio_auditoria.Domain.FactoryConcret;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

@Component("BASICA")
public class AuditoriaBasicaFactory extends AuditoriaAbsFactory {

    // Construye una Auditoria solo con los campos esenciales
    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {

        auditoria.setTipo("BASICA");
        return auditoria;
    }

    // Construccion de la respuesta con todos los campos
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
