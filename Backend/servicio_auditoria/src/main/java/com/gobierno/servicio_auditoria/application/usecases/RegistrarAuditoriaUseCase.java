package com.gobierno.servicio_auditoria.application.usecases;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_auditoria.domain.bridge.AuditoriaProcessor;
import com.gobierno.servicio_auditoria.domain.bridge.RegistrarAuditoriaProcessor;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.factory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.domain.ports.out.RegistroAuditoriaPort;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;

@Service
public class RegistrarAuditoriaUseCase {

    private final Map<String, AuditoriaProcessor> processors;

    public RegistrarAuditoriaUseCase(Map<String, AuditoriaAbsFactory> factories, 
        RegistroAuditoriaPort registroAuditoriaPort) {
        this.processors = new HashMap<>();

        factories.forEach((tipo, factory) ->
                processors.put(tipo, new RegistrarAuditoriaProcessor(factory, registroAuditoriaPort))
        );
    }

    public AuditoriaResponse ejecutar(Auditoria auditoria, String tipo) {
        
        AuditoriaProcessor processor = processors.get(tipo.toUpperCase());

        if (processor == null) {
            throw new IllegalArgumentException("Tipo de auditoria invalido");
        }

        return processor.procesar(auditoria);
    }
}
