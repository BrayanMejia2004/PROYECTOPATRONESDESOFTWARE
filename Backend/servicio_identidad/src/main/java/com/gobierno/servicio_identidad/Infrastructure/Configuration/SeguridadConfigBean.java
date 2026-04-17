package com.gobierno.servicio_identidad.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeguridadConfigBean { // Clase de configuración para inyectar el enum SeguridadConfig

    @Bean // Define este método como un bean de Spring
    public SeguridadConfig seguridadConfig() { // Método que retorna la instancia singleton de SeguridadConfig
        return SeguridadConfig.INSTANCE; // Retorna la instancia única del enum
    }
}