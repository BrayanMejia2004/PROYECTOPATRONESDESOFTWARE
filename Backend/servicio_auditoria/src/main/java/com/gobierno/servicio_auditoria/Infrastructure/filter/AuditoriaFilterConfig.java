package com.gobierno.servicio_auditoria.infrastructure.filter;

import com.gobierno.servicio_auditoria.domain.chain.AuditoriaFilter;
import com.gobierno.servicio_auditoria.domain.chain.UserExistsFilter;
import com.gobierno.servicio_auditoria.domain.chain.IpValidationFilter;
import com.gobierno.servicio_auditoria.domain.chain.ActionAllowedFilter;
import com.gobierno.servicio_auditoria.domain.chain.DuplicateCheckFilter;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.AuditoriaJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuditoriaFilterConfig {

    @Autowired
    private AuditoriaJpaRepository auditoriaJpaRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${servicio.identidad.url:http://localhost:8082}")
    private String servicioIdentidadUrl;

    @Bean
    public AuditoriaFilter auditoriaFilterChain() {
        UserExistsFilter userExistsFilter = new UserExistsFilter(restTemplate, servicioIdentidadUrl);
        IpValidationFilter ipValidationFilter = new IpValidationFilter();
        ActionAllowedFilter actionAllowedFilter = new ActionAllowedFilter();
        DuplicateCheckFilter duplicateCheckFilter = new DuplicateCheckFilter(auditoriaJpaRepository);

        userExistsFilter.setNext(ipValidationFilter);
        ipValidationFilter.setNext(actionAllowedFilter);
        actionAllowedFilter.setNext(duplicateCheckFilter);

        return userExistsFilter;
    }

    @Bean
    public FilterRegistrationBean<AuditoriaValidationFilter> auditoriaFilterRegistration(
            AuditoriaFilter auditoriaFilterChain) {
        FilterRegistrationBean<AuditoriaValidationFilter> registrationBean = new FilterRegistrationBean<>();

        AuditoriaValidationFilter filter = new AuditoriaValidationFilter();
        filter.setAuditoriaFilterChain(auditoriaFilterChain);
        registrationBean.setFilter(filter);

        registrationBean.addUrlPatterns("/auditoria/*");

        return registrationBean;
    }
}
