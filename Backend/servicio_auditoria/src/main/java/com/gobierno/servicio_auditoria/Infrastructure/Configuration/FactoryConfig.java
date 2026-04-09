package com.gobierno.servicio_auditoria.Infrastructure.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gobierno.servicio_auditoria.Domain.AbsFactory.AuditoriaAbsFactory;
import com.gobierno.servicio_auditoria.Domain.FactoryConcret.AuditoriaBasicaFactory;
import com.gobierno.servicio_auditoria.Domain.FactoryConcret.AuditoriaCompletaFactory;
import com.gobierno.servicio_auditoria.Domain.FactoryConcret.AuditoriaSeguridadFactory;

// Configuracion de beans para las factories del Abstract Factory
@Configuration
public class FactoryConfig {

    // Bean individual para auditoria basica
    @Bean
    public AuditoriaBasicaFactory basicaFactory() {
        return new AuditoriaBasicaFactory();
    }

    // Bean individual para auditoria de seguridad
    @Bean
    public AuditoriaSeguridadFactory seguridadFactory() {
        return new AuditoriaSeguridadFactory();
    }

    // Bean individual para auditoria completa
    @Bean
    public AuditoriaCompletaFactory completaFactory() {
        return new AuditoriaCompletaFactory();
    }

    // Bean mapa de factories con claves correctas (BASICA, SEGURIDAD, COMPLETA)
    // Spring inyecta este bean en el constructor de RegistrarAuditoriaUseCase
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
