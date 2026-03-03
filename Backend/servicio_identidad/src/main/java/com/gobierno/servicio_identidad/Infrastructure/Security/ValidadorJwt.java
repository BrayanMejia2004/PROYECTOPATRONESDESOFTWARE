package com.gobierno.servicio_identidad.Infrastructure.Security;

import java.nio.charset.StandardCharsets;

import com.gobierno.servicio_identidad.Infrastructure.Configuration.SeguridadConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Clase para validar tokens JWT
public class ValidadorJwt {

    // Método para validar un token JWT y obtener los claims
    public static Claims validarToken(String token) {
        
        String secret = SeguridadConfig.INSTANCE.getJwtSecret();

        // Validar el token JWT utilizando la clave secreta y devolver los claims si es válido, o lanzar una excepción si no lo es
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
}
