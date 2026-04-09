package com.gobierno.servicio_auditoria.Domain.FactoryConcret;

import java.sql.Timestamp;
import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;

// Factory para auditorias completas - incluye IP y fecha completa
public class AuditoriaCompletaFactory extends AuditoriaAbsFactory {

    // Establece el tipo COMPLETA y genera timestamp actual
    @Override
    public Auditoria crearAuditoria(Auditoria auditoria) {
        auditoria.setTipo("COMPLETA");
        auditoria.setFecha(new Timestamp((System.currentTimeMillis())));
        return auditoria;
    }

    // Crea respuesta con todos los campos (incluye IP y fecha)
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
