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
public class GeneradorJwtAdapter { // Adapter para generar tokens JWT

    private final SeguridadConfig config; // Configuración de seguridad (secret y expiración)

    public GeneradorJwtAdapter(SeguridadConfig config) { // Constructor con inyección de dependencias
        this.config = config; // Asigna la configuración
    }

    public String generarToken(Usuario usuario, List<String> roles) { // Método para generar token JWT
        String secret = config.getJwtSecret(); // Obtiene la clave secreta del config
        long expiracion = config.getJwtExpiracion(); // Obtiene el tiempo de expiración del config

        java.util.Date ahora = new java.util.Date(); // Fecha actual
        java.util.Date fechaExpiracion = new java.util.Date(ahora.getTime() + expiracion); // Fecha de expiración

        var builder = Jwts.builder() // Constructor del token JWT
                .setSubject(usuario.getUsername()) // Establece el subject (username)
                .claim("roles", roles) // Agrega los roles como claim
                .setIssuedAt(ahora) // Establece la fecha de emisión
                .setExpiration(fechaExpiracion) // Establece la fecha de expiración
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256); // Firma
                                                                                                                  // el
                                                                                                                  // token

        return builder.compact(); // Genera el token como string
    }

    public String generarToken(Usuario usuario) { // Sobrecarga del método sin roles
        return generarToken(usuario, List.of()); // Llama al método principal con lista vacía
    }
}