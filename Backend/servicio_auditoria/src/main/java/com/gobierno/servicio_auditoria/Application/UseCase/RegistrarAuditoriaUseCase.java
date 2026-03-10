package com.gobierno.servicio_auditoria.Application.UseCase;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.FactoryConcret.AuditoriaBasicaFactory;
import com.gobierno.servicio_auditoria.Domain.FactoryConcret.AuditoriaCompletaFactory;
import com.gobierno.servicio_auditoria.Domain.FactoryConcret.AuditoriaSeguridadFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;
import com.gobierno.servicio_auditoria.Ports.Output.RegistroAuditoria;

@Service
public class RegistrarAuditoriaUseCase {

    private final RegistroAuditoria registroAuditoria;

    public RegistrarAuditoriaUseCase(RegistroAuditoria registroAuditoria) {
        this.registroAuditoria = registroAuditoria;
    }

    public AuditoriaResponse ejecutar(Auditoria auditoria, String tipo) {

        AuditoriaAbsFactory factory;

        switch (tipo.toUpperCase()) {

            case "BASICA":
                factory = new AuditoriaBasicaFactory();
                break;

            case "SEGURIDAD":
                factory = new AuditoriaSeguridadFactory();
                break;

            case "COMPLETA":
                factory = new AuditoriaCompletaFactory();
                break;

            default:
                throw new IllegalArgumentException("Tipo inválido");
        }

        // Crear auditoría usando la factory
        Auditoria auditoriaProcesada = factory.creAuditoria(auditoria);

        // Guardar auditoría en base de datos
        registroAuditoria.registrarAccion(auditoriaProcesada);

        return factory.crearRespuesta(auditoriaProcesada);
    }

}
