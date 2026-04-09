package com.gobierno.servicio_auditoria.Application.UseCase;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.Bridge.Abstraction.AuditoriaProcessor;
import com.gobierno.servicio_auditoria.Domain.Bridge.Abstraction.RegistrarAuditoriaProcessor;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;
import com.gobierno.servicio_auditoria.Ports.Output.RegistroAuditoria;

// Caso de uso que coordina el flujo de auditoria
@Service
public class RegistrarAuditoriaUseCase {

    // Mapa de processors por tipo de auditoria
    private final Map<String, AuditoriaProcessor> processors;

    // Constructor que recibe factories de Spring y crea processors asociados
    public RegistrarAuditoriaUseCase(Map<String, AuditoriaAbsFactory> factories, 
        RegistroAuditoria registroAuditoria) {
        this.processors = new HashMap<>();

        // Por cada factory crea un processor asociado
        factories.forEach((tipo, factory) ->
                processors.put(tipo, new RegistrarAuditoriaProcessor(factory, registroAuditoria))
        );
    }

    // Ejecuta el caso de uso - selecciona y ejecuta el processor correcto
    public AuditoriaResponse ejecutar(Auditoria auditoria, String tipo) {
        
        // Obtiene el processor segun el tipo
        AuditoriaProcessor processor = processors.get(tipo.toUpperCase());

        // Lanza excepcion si el tipo no existe
        if (processor == null) {
            throw new IllegalArgumentException("Tipo de auditoria invalido");
        }

        // Delega el procesamiento al processor
        return processor.procesar(auditoria);
    }
}
