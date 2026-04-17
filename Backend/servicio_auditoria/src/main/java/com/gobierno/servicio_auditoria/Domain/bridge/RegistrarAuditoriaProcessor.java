package com.gobierno.servicio_auditoria.domain.bridge;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.factory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.domain.ports.out.RegistroAuditoriaPort;
import com.gobierno.servicio_auditoria.domain.prototype.AuditoriaPrototypeRegistry;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public class RegistrarAuditoriaProcessor extends AuditoriaProcessor {  // Implementación concreta (Refined Abstraction) del Bridge

    private final RegistroAuditoriaPort registroAuditoriaPort;  // Puerto para persistir auditoría

    public RegistrarAuditoriaProcessor(AuditoriaAbsFactory factory,
            RegistroAuditoriaPort registroAuditoriaPort) {  // Constructor con inyección
        super(factory);  // Llama al constructor padre
        this.registroAuditoriaPort = registroAuditoriaPort;  // Asigna el puerto
    }

    @Override
    public AuditoriaResponse procesar(Auditoria auditoria) {  // Procesa la auditoría

        Auditoria auditoriaBase = AuditoriaPrototypeRegistry.obtenerPrototipo(auditoria.getTipo());  // Obtiene un prototipo según el tipo

        auditoriaBase.setUsuario_id(auditoria.getUsuario_id());  // Asigna el usuario
        auditoriaBase.setAccion(auditoria.getAccion());  // Asigna la acción
        auditoriaBase.setDescripcion(auditoria.getDescripcion());  // Asigna la descripción
        auditoriaBase.setIp_origen(auditoria.getIp_origen());  // Asigna la IP

        Auditoria procesada = factory.crearAuditoria(auditoriaBase);  // Crea la auditoría usando la factory
        
        registroAuditoriaPort.registrarAccion(procesada);  // Persiste la auditoría

        return factory.crearRespuesta(procesada);  // Retorna la respuesta creada por la factory
    }
}