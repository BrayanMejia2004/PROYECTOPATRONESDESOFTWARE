package com.gobierno.servicio_identidad.Infrastructure.Security;

import java.nio.charset.StandardCharsets;

import com.gobierno.servicio_identidad.Infrastructure.Configuration.SeguridadConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class ValidadorJwt {

    public static Claims validarToken(String token) {
        String secret = SeguridadConfig.INSTANCE.getJwtSecret();

        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
}
