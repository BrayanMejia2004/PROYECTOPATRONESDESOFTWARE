package com.gobierno.servicio_auditoria.infrastructure.configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.gobierno.servicio_auditoria.domain.factory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.domain.factory.AuditoriaBasicaFactory;
import com.gobierno.servicio_auditoria.domain.factory.AuditoriaCompletaFactory;
import com.gobierno.servicio_auditoria.domain.factory.AuditoriaSeguridadFactory;

@Configuration
public class FactoryConfig {  // Configuración de las factories del Abstract Factory

    @Bean
    public AuditoriaBasicaFactory basicaFactory() {  // Bean para factory básica
        return new AuditoriaBasicaFactory();
    }

    @Bean
    public AuditoriaSeguridadFactory seguridadFactory() {  // Bean para factory de seguridad
        return new AuditoriaSeguridadFactory();
    }

    @Bean
    public AuditoriaCompletaFactory completaFactory() {  // Bean para factory completa
        return new AuditoriaCompletaFactory();
    }

    @Bean
    public Map<String, AuditoriaAbsFactory> factories(  // Bean que crea un mapa de factories
            AuditoriaBasicaFactory basicaFactory,
            AuditoriaSeguridadFactory seguridadFactory,
            AuditoriaCompletaFactory completaFactory) {
        
        Map<String, AuditoriaAbsFactory> factories = new LinkedHashMap<>();
        factories.put("BASICA", basicaFactory);  // Registra la factory básica
        factories.put("SEGURIDAD", seguridadFactory);  // Registra la factory de seguridad
        factories.put("COMPLETA", completaFactory);  // Registra la factory completa
        return factories;  // Retorna el mapa de factories
    }
}