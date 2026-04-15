package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.infrastructure.configuration.SeguridadConfig;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class GeneradorJwtAdapter {

    private final SeguridadConfig config;

    public GeneradorJwtAdapter(SeguridadConfig config) {
        this.config = config;
    }

    public String generarToken(Usuario usuario, List<String> roles) {
        String secret = config.getJwtSecret();
        long expiracion = config.getJwtExpiracion();

        java.util.Date ahora = new java.util.Date();
        java.util.Date fechaExpiracion = new java.util.Date(ahora.getTime() + expiracion);

        var builder = Jwts.builder()
                .setSubject(usuario.getUsername())
                .claim("roles", roles)
                .setIssuedAt(ahora)
                .setExpiration(fechaExpiracion)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256);

        return builder.compact();
    }

    public String generarToken(Usuario usuario) {
        return generarToken(usuario, List.of());
    }
}
