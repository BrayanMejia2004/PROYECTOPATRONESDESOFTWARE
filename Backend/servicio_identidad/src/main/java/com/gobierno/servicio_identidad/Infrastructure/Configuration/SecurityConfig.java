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
public class SecurityConfig { // Clase de configuración de seguridad

    private final FiltroJwtAdapter filtroJwtAdapter; // Filtro JWT personalizado

    public SecurityConfig(FiltroJwtAdapter filtroJwtAdapter) { // Constructor con inyección
        this.filtroJwtAdapter = filtroJwtAdapter; // Asigna el filtro JWT
    }

    @Bean // Define este método como un bean de Spring
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // Configura la cadena de
                                                                                         // filtros de seguridad
        http // Configura HttpSecurity
                .csrf(csrf -> csrf.disable()) // Deshabilita protección CSRF (no necesario para APIs REST)
                .cors(cors -> cors.disable()) // Deshabilita CORS (se configura en el gateway)
                .authorizeHttpRequests(auth -> auth // Configura autorización de peticiones
                        .requestMatchers("/usuarios/registro").permitAll() // Permite acceso público a registro
                        .requestMatchers("/usuarios/login").permitAll() // Permite acceso público a login
                        .requestMatchers("/usuarios/lista").permitAll() // Permite acceso público a lista de usuarios
                        .requestMatchers("/usuarios/*/id").permitAll() // Permite acceso público a obtener ID por
                                                                       // username
                        .anyRequest().authenticated()) // Requiere autenticación para cualquier otra ruta

                .addFilterBefore(filtroJwtAdapter, UsernamePasswordAuthenticationFilter.class); // Agrega el filtro JWT
                                                                                                // antes del filtro de
                                                                                                // autenticación por
                                                                                                // defecto

        return http.build(); // Construye y retorna la cadena de filtros
    }

    @Bean // Define este método como un bean de Spring
    public PasswordEncoder passwordEncoder() { // Bean para encriptar contraseñas
        return new BCryptPasswordEncoder(); // Retorna un encriptador BCrypt
    }
}