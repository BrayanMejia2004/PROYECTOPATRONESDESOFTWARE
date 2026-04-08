package com.gobierno.servicio_auditoria.Application.UseCase;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Domain.Prototype.AuditoriaPrototypeRegistry;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;
import com.gobierno.servicio_auditoria.Ports.Output.RegistroAuditoria;

@Service
public class RegistrarAuditoriaUseCase {

    private final RegistroAuditoria registroAuditoria;
    private final Map<String, AuditoriaAbsFactory> factories;

    public RegistrarAuditoriaUseCase(RegistroAuditoria registroAuditoria,
            Map<String, AuditoriaAbsFactory> factories) {
        this.registroAuditoria = registroAuditoria;
        this.factories = factories;
    }

    // Registra una auditoria segun su tipo
    public AuditoriaResponse ejecutar(Auditoria auditoria, String tipo) {

        // Se obtiene una copia del prototipo en lugar de crear un objeto nuevo
        Auditoria auditoriaBase = AuditoriaPrototypeRegistry.obtenerPrototipo(tipo);

        // Se complementan los datos sobre el objeto clonado
        auditoriaBase.setUsuario_id(auditoria.getUsuario_id());
        auditoriaBase.setAccion(auditoria.getAccion());
        auditoriaBase.setDescripcion(auditoria.getDescripcion());
        auditoriaBase.setIp_origen(auditoria.getIp_origen());

        // Obtener la factory del Map inyectado
        AuditoriaAbsFactory factory = factories.get(tipo.toUpperCase());

        if (factory == null) {
            throw new IllegalArgumentException("Tipo de auditoria invalido");
        }

        // La fábrica construye y transforma el objeto Auditoria según las reglas del tipo elegido
        Auditoria auditoriaProcesada = factory.crearAuditoria(auditoriaBase);

        // Guardar auditoría en base de datos
        registroAuditoria.registrarAccion(auditoriaProcesada);

        // Crear respuesta de la auditoria usando el abstract factory
        return factory.crearRespuesta(auditoriaProcesada);
    }

}
