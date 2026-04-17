package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class FiltroJwtAdapter extends OncePerRequestFilter { // Filtro para validar JWT en cada petición

    private final ValidadorJwtAdapter validadorJwtAdapter; // Adapter para validar tokens

    public FiltroJwtAdapter(ValidadorJwtAdapter validadorJwtAdapter) { // Constructor con inyección
        this.validadorJwtAdapter = validadorJwtAdapter; // Asigna el validador
    }

    @Override // Sobrescribe el método de la clase padre
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, // Petición HTTP
            @SuppressWarnings("null") HttpServletResponse response, // Respuesta HTTP
            @SuppressWarnings("null") FilterChain filterChain) // Cadena de filtros
            throws ServletException, IOException { // Excepciones de Servlet

        String header = request.getHeader("Authorization"); // Obtiene el header Authorization

        if (header != null && header.startsWith("Bearer ")) { // Si existe el header y empieza con "Bearer "
            String token = header.substring(7); // Extrae el token (sin "Bearer ")

            try { // Try-catch para manejar errores de validación
                Claims claims = validadorJwtAdapter.validarToken(token); // Valida el token

                String username = claims.getSubject(); // Obtiene el username del token

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken( // Crea
                                                                                                              // token
                                                                                                              // de
                                                                                                              // autenticación
                        username, // Principal (username)
                        null, // Credentials (no hay password)
                        Collections.emptyList()); // Authorities (roles vacíos)

                SecurityContextHolder.getContext().setAuthentication(authentication); // Establece la autenticación en
                                                                                      // el contexto

            } catch (Exception e) { // Si el token es inválido
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Retorna 401 Unauthorized
                return; // Termina la ejecución del filtro
            }
        }

        filterChain.doFilter(request, response); // Continúa con la cadena de filtros
    }
}