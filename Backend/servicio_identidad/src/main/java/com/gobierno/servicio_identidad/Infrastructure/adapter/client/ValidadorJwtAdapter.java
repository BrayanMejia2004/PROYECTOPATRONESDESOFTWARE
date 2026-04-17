package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.infrastructure.configuration.SeguridadConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class ValidadorJwtAdapter { // Adapter para validar tokens JWT

    private final SeguridadConfig config; // Configuración de seguridad (secret)

    public ValidadorJwtAdapter(SeguridadConfig config) { // Constructor con inyección de dependencias
        this.config = config; // Asigna la configuración
    }

    public Claims validarToken(String token) { // Método para validar un token JWT
        String secret = config.getJwtSecret(); // Obtiene la clave secreta del config

        return Jwts.parserBuilder() // Constructor del parser de JWT
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))) // Establece la clave de
                                                                                            // verificación
                .build() // Construye el parser
                .parseClaimsJws(token) // Valida y parsea el token
                .getBody(); // Retorna los claims (datos) del token
    }
}