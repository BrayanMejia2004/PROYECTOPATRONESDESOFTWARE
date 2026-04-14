package com.gobierno.servicio_auditoria.domain.bridge;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.factory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.domain.ports.out.RegistroAuditoriaPort;
import com.gobierno.servicio_auditoria.domain.prototype.AuditoriaPrototypeRegistry;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

public class RegistrarAuditoriaProcessor extends AuditoriaProcessor {

    private final RegistroAuditoriaPort registroAuditoriaPort;

    public RegistrarAuditoriaProcessor(AuditoriaAbsFactory factory,
            RegistroAuditoriaPort registroAuditoriaPort) {
        super(factory);
        this.registroAuditoriaPort = registroAuditoriaPort;
    }

    @Override
    public AuditoriaResponse procesar(Auditoria auditoria) {

        Auditoria auditoriaBase = AuditoriaPrototypeRegistry.obtenerPrototipo(auditoria.getTipo());

        auditoriaBase.setUsuario_id(auditoria.getUsuario_id());
        auditoriaBase.setAccion(auditoria.getAccion());
        auditoriaBase.setDescripcion(auditoria.getDescripcion());
        auditoriaBase.setIp_origen(auditoria.getIp_origen());

        Auditoria procesada = factory.crearAuditoria(auditoriaBase);
        
        registroAuditoriaPort.registrarAccion(procesada);

        return factory.crearRespuesta(procesada);
    }
}
