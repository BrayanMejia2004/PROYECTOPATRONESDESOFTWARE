package com.gobierno.servicio_identidad.Infrastructure.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeguridadConfigBean {

    @Bean
    public SeguridadConfig seguridadConfig() {
        return SeguridadConfig.INSTANCE;
    }
}
