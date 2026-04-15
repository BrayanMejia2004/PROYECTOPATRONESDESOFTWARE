package com.gobierno.servicio_identidad.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gobierno.servicio_identidad.infrastructure.adapter.client.FiltroJwtAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final FiltroJwtAdapter filtroJwtAdapter;

    public SecurityConfig(FiltroJwtAdapter filtroJwtAdapter) {
        this.filtroJwtAdapter = filtroJwtAdapter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/registro").permitAll()
                        .requestMatchers("/usuarios/login").permitAll()
                        .requestMatchers("/usuarios/lista").permitAll()
                        .requestMatchers("/usuarios/*/id").permitAll()
                        .anyRequest().authenticated())

                .addFilterBefore(filtroJwtAdapter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
