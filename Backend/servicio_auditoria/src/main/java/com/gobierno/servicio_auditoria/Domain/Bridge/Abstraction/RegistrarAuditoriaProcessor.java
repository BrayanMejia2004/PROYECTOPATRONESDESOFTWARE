package com.gobierno.servicio_auditoria.Domain.Bridge.Abstraction;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Domain.Prototype.AuditoriaPrototypeRegistry;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;
import com.gobierno.servicio_auditoria.Ports.Output.RegistroAuditoria;

// Processor concreto que orquesta el registro de auditoria
public class RegistrarAuditoriaProcessor extends AuditoriaProcessor {

    // Puerto de salida para persistir la auditoria
    private final RegistroAuditoria registroAuditoria;

    // Constructor que recibe factory y puerto de registro
    public RegistrarAuditoriaProcessor(AuditoriaAbsFactory factory,
            RegistroAuditoria registroAuditoria) {
        super(factory);
        this.registroAuditoria = registroAuditoria;
    }

    // Flujo de procesamiento de auditoria
    @Override
    public AuditoriaResponse procesar(Auditoria auditoria) {

        // Obtiene prototipo base segun tipo de auditoria
        Auditoria auditoriaBase = AuditoriaPrototypeRegistry.obtenerPrototipo(auditoria.getTipo());

        // Completa datos del request en el prototipo
        auditoriaBase.setUsuario_id(auditoria.getUsuario_id());
        auditoriaBase.setAccion(auditoria.getAccion());
        auditoriaBase.setDescripcion(auditoria.getDescripcion());
        auditoriaBase.setIp_origen(auditoria.getIp_origen());

        // Crea auditoria procesada usando la factory
        Auditoria procesada = factory.crearAuditoria(auditoriaBase);
        
        // Registra la auditoria en base de datos
        registroAuditoria.registrarAccion(procesada);

        // Crea y retorna la respuesta usando la factory
        return factory.crearRespuesta(procesada);
    }
}
