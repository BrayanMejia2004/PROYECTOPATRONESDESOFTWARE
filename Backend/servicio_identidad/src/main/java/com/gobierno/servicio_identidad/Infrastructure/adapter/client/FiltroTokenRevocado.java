package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gobierno.servicio_identidad.application.usecases.GestionSesionesUseCase;
import com.gobierno.servicio_identidad.application.usecases.HashUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FiltroTokenRevocado extends OncePerRequestFilter {

    private final GestionSesionesUseCase gestionSesionesUseCase;

    public FiltroTokenRevocado(GestionSesionesUseCase gestionSesionesUseCase) {
        this.gestionSesionesUseCase = gestionSesionesUseCase;
    }

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request,
            @SuppressWarnings("null") HttpServletResponse response,
            @SuppressWarnings("null") FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String tokenHash = HashUtil.sha256(token);

            if (gestionSesionesUseCase.esTokenRevocado(tokenHash)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("text/plain");
                response.getWriter().write("Token revocado");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
