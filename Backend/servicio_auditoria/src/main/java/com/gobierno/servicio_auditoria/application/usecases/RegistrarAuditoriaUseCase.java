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
public class RegistrarAuditoriaUseCase {  // Caso de uso para registrar auditorías

    private final Map<String, AuditoriaProcessor> processors;  // Mapa de procesadores por tipo

    public RegistrarAuditoriaUseCase(Map<String, AuditoriaAbsFactory> factories,  // Constructor - Spring inyecta las factories
        RegistroAuditoriaPort registroAuditoriaPort) {  // Puerto de registro
        this.processors = new HashMap<>();

        factories.forEach((tipo, factory) ->  // Itera sobre las factories inyectadas
                processors.put(tipo, new RegistrarAuditoriaProcessor(factory, registroAuditoriaPort))  // Crea un procesador por tipo
        );
    }

    public AuditoriaResponse ejecutar(Auditoria auditoria, String tipo) {  // Método principal
        
        AuditoriaProcessor processor = processors.get(tipo.toUpperCase());  // Obtiene el procesador según el tipo

        if (processor == null) {  // Si el tipo no existe
            throw new IllegalArgumentException("Tipo de auditoria invalido");  // Lanza excepción
        }

        return processor.procesar(auditoria);  // Procesa la auditoría
    }
}