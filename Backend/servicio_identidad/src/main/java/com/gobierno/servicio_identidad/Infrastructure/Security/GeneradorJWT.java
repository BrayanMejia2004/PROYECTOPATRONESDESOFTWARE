package com.gobierno.servicio_identidad.Infrastructure.Security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Infrastructure.Configuration.SeguridadConfig;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

// Clase para generar tokens JWT
public class GeneradorJWT {

    // Método para generar un token JWT a partir de un usuario
    public static String generarToken(Usuario usuario) {

        // Obtener la clave secreta y el tiempo de expiración desde la configuración
        String secret = SeguridadConfig.INSTANCE.getJwtSecret();
        long expiracion = SeguridadConfig.INSTANCE.getJwtExpiracion();

        // Crear la fecha de expiración del token
        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + expiracion);

        // Construir el token JWT con el username como subject, la fecha de emisión, la fecha de expiración y firmarlo con la clave secreta
        return Jwts.builder()
                .setSubject(usuario.getUsername())
                .setIssuedAt(ahora)
                .setExpiration(fechaExpiracion)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

    }
}
