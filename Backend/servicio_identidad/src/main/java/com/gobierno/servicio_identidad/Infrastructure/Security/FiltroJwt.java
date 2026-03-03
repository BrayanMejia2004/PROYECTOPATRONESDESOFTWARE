package com.gobierno.servicio_identidad.Infrastructure.Security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// Filtro de seguridad para validar el token JWT en cada solicitud
public class FiltroJwt extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Obtener el token JWT del encabezado Authorization
        String header = request.getHeader("Authorization");

        // Validar el token JWT
        if (header != null && header.startsWith("Bearer ")) {

            // Extraer el token JWT del encabezado
            String token = header.substring(7);

            try {
                // Validar el token y obtener los claims
                Claims claims = ValidadorJwt.validarToken(token);

                // Obtener el username del token
                String username = claims.getSubject();

                // Crear una autenticación basada en el username y establecerla en el contexto de seguridad
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.emptyList());

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

}
