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

    // Registra una auditoria segun su tipo
    public AuditoriaResponse ejecutar(Auditoria auditoria, String tipo) {

        // Referencia a la clase abstracta
        AuditoriaAbsFactory Absfactory;

        // Selección de la fábrica concreta según el tipo recibido
        switch (tipo.toUpperCase()) {

            case "BASICA":
                Absfactory = new AuditoriaBasicaFactory();
                break;

            case "SEGURIDAD":
                Absfactory = new AuditoriaSeguridadFactory();
                break;

            case "COMPLETA":
                Absfactory = new AuditoriaCompletaFactory();
                break;

            default:
                throw new IllegalArgumentException("Tipo inválido");
        }

        // La fábrica construye y transforma el objeto Auditoria según las reglas del tipo elegido
        Auditoria auditoriaProcesada = Absfactory.crearAuditoria(auditoria);

        // Guardar auditoría en base de datos
        registroAuditoria.registrarAccion(auditoriaProcesada);

        //Crear auditoria usando el abstract factory
        return Absfactory.crearRespuesta(auditoriaProcesada);
    }

}
