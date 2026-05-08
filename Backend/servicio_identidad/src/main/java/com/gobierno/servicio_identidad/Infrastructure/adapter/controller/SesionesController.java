package com.gobierno.servicio_identidad.infrastructure.adapter.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_identidad.application.usecases.GestionSesionesUseCase;
import com.gobierno.servicio_identidad.application.usecases.HashUtil;
import com.gobierno.servicio_identidad.domain.entities.SesionActiva;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/usuarios/sesiones")
public class SesionesController {

    private final GestionSesionesUseCase gestionSesionesUseCase;
    private final AutorizacionClient autorizacionClient;

    public SesionesController(GestionSesionesUseCase gestionSesionesUseCase,
            AutorizacionClient autorizacionClient) {
        this.gestionSesionesUseCase = gestionSesionesUseCase;
        this.autorizacionClient = autorizacionClient;
    }

    @GetMapping("/activas")
    public ResponseEntity<?> obtenerSesionesActivas(Authentication authentication) {
        ResponseEntity<?> error = verificarAdmin(authentication);
        if (error != null) return error;

        List<SesionActiva> sesiones = gestionSesionesUseCase.obtenerSesionesActivas();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (SesionActiva s : sesiones) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", s.getId());
            item.put("username", s.getUsername());
            item.put("ipOrigen", s.getIpOrigen());
            item.put("fechaInicio", s.getFechaInicio().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime().toString());

            long minutos = Duration.between(
                    s.getFechaInicio().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    LocalDateTime.now()).toMinutes();
            item.put("minutosActivo", minutos);

            resultado.add(item);
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/metricas")
    public ResponseEntity<?> obtenerMetricas(Authentication authentication) {
        ResponseEntity<?> error = verificarAdmin(authentication);
        if (error != null) return error;

        Map<String, Object> metricas = new HashMap<>();
        metricas.put("revocacionesHoy", gestionSesionesUseCase.contarRevocacionesHoy());
        metricas.put("revocacionesSemana", gestionSesionesUseCase.contarRevocacionesSemana());
        metricas.put("revocacionesTotales", gestionSesionesUseCase.contarRevocacionesTotales());
        metricas.put("sesionesHoy", gestionSesionesUseCase.contarSesionesHoy());

        return ResponseEntity.ok(metricas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> revocarSesion(@PathVariable Long id, Authentication authentication) {
        ResponseEntity<?> error = verificarAdmin(authentication);
        if (error != null) return error;

        gestionSesionesUseCase.revocarSesion(id, authentication.getName());
        return ResponseEntity.ok(Map.of("mensaje", "Sesi\u00f3n revocada exitosamente"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication, HttpServletRequest request) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String tokenHash = HashUtil.sha256(token);

            List<SesionActiva> sesiones = gestionSesionesUseCase.obtenerSesionesActivas();
            for (SesionActiva sesion : sesiones) {
                if (sesion.getTokenHash().equals(tokenHash)) {
                    gestionSesionesUseCase.revocarSesion(sesion.getId(), "SISTEMA");
                    break;
                }
            }
        }

        return ResponseEntity.ok(Map.of("mensaje", "Sesi\u00f3n cerrada exitosamente"));
    }

    private ResponseEntity<?> verificarAdmin(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }

        List<String> roles = autorizacionClient.obtenerRolesDeUsuario(authentication.getName());
        if (!roles.contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Acceso denegado: se requiere rol ADMIN"));
        }

        return null;
    }
}
