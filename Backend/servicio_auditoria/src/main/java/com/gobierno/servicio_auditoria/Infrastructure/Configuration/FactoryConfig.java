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
public class FactoryConfig {

    @Bean
    public AuditoriaBasicaFactory basicaFactory() {
        return new AuditoriaBasicaFactory();
    }

    @Bean
    public AuditoriaSeguridadFactory seguridadFactory() {
        return new AuditoriaSeguridadFactory();
    }

    @Bean
    public AuditoriaCompletaFactory completaFactory() {
        return new AuditoriaCompletaFactory();
    }

    @Bean
    public Map<String, AuditoriaAbsFactory> factories(
            AuditoriaBasicaFactory basicaFactory,
            AuditoriaSeguridadFactory seguridadFactory,
            AuditoriaCompletaFactory completaFactory) {
        
        Map<String, AuditoriaAbsFactory> factories = new LinkedHashMap<>();
        factories.put("BASICA", basicaFactory);
        factories.put("SEGURIDAD", seguridadFactory);
        factories.put("COMPLETA", completaFactory);
        return factories;
    }
}
