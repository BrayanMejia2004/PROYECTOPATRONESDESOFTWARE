package com.gobierno.servicio_identidad.Infrastructure.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gobierno.servicio_identidad.Infrastructure.Security.FiltroJwt;

// Configuración de seguridad para la aplicación, definiendo las reglas de acceso y los filtros de autenticación.
@Configuration
@EnableWebSecurity
public class SecurityWebConfig {

    // Configura la cadena de filtros de seguridad, permitiendo el acceso a las rutas de registro y login sin autenticación, 
    // y requiriendo autenticación para cualquier otra ruta.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/registro").permitAll()
                        .requestMatchers("/usuarios/login").permitAll()
                        .anyRequest().authenticated())

                .addFilterBefore(new FiltroJwt(),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para el codificador de contraseñas, utilizando BCrypt para asegurar las contraseñas almacenadas en la base de datos.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
