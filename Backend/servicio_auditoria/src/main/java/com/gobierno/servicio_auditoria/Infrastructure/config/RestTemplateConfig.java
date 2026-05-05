package com.gobierno.servicio_auditoria.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {  // Configuración para RestTemplate
    
    @Bean
    public RestTemplate restTemplate() {  // Crea un bean de RestTemplate para llamadas REST
        return new RestTemplate();
    }
}
