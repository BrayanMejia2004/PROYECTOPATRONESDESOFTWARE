package com.gobierno.servicio_identidad.Infrastructure.Security;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.Infrastructure.Configuration.SeguridadConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class ValidadorJwt {

    private final SeguridadConfig config;

    public ValidadorJwt(SeguridadConfig config) {
        this.config = config;
    }

    public Claims validarToken(String token) {
        String secret = config.getJwtSecret();

        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
