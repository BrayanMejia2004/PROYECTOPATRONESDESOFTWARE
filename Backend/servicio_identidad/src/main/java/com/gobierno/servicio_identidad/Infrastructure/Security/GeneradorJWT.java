package com.gobierno.servicio_identidad.Infrastructure.Security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Infrastructure.Configuration.SeguridadConfig;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class GeneradorJWT {

    private final SeguridadConfig config;

    public GeneradorJWT(SeguridadConfig config) {
        this.config = config;
    }

    public String generarToken(Usuario usuario) {
        String secret = config.getJwtSecret();
        long expiracion = config.getJwtExpiracion();

        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + expiracion);

        return Jwts.builder()
                .setSubject(usuario.getUsername())
                .setIssuedAt(ahora)
                .setExpiration(fechaExpiracion)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
