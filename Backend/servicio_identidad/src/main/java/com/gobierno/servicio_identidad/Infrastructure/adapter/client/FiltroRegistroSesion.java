package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.gobierno.servicio_identidad.application.usecases.GestionSesionesUseCase;
import com.gobierno.servicio_identidad.application.usecases.HashUtil;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.UsuarioJpaRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class FiltroRegistroSesion extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/usuarios/login";

    private final GestionSesionesUseCase gestionSesionesUseCase;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final ValidadorJwtAdapter validadorJwtAdapter;

    public FiltroRegistroSesion(GestionSesionesUseCase gestionSesionesUseCase,
            UsuarioJpaRepository usuarioJpaRepository,
            ValidadorJwtAdapter validadorJwtAdapter) {
        this.gestionSesionesUseCase = gestionSesionesUseCase;
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.validadorJwtAdapter = validadorJwtAdapter;
    }

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request,
            @SuppressWarnings("null") HttpServletResponse response,
            @SuppressWarnings("null") FilterChain chain)
            throws ServletException, IOException {

        if (!isLoginRequest(request)) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        chain.doFilter(request, responseWrapper);

        if (response.getStatus() == 200) {
            try {
                String token = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                if (!token.isEmpty()) {
                    Claims claims = validadorJwtAdapter.validarToken(token);
                    String username = claims.getSubject();
                    Usuario usuario = usuarioJpaRepository.findByUsername(username).orElse(null);
                    if (usuario != null) {
                        String tokenHash = HashUtil.sha256(token);
                        String ip = request.getRemoteAddr();
                        gestionSesionesUseCase.registrarSesion(
                                usuario.getId(), usuario.getUsername(), ip, tokenHash);
                    }
                }
            } catch (Exception e) {
                logger.error("Error registrando sesión en login", e);
            }
        }

        responseWrapper.copyBodyToResponse();
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && LOGIN_PATH.equals(request.getRequestURI());
    }
}
