package com.gobierno.servicio_auditoria.Domain.FactoryConcret;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

// Factory para auditorias basicas - solo campos esenciales
public class AuditoriaBasicaFactory extends AuditoriaAbsFactory {

    // Establece el tipo BASICA y retorna la auditoria
    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {
        auditoria.setTipo("BASICA");
        return auditoria;
    }

    // Crea respuesta con campos basicos (usuario, accion, descripcion, tipo)
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
