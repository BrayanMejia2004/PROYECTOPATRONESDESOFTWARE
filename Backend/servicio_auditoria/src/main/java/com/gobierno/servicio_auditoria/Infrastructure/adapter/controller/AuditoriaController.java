package com.gobierno.servicio_auditoria.infrastructure.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_auditoria.application.usecases.RegistrarAuditoriaUseCase;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.AuditoriaJpaRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;
    private final AuditoriaJpaRepository auditoriaJpaRepository;

    public AuditoriaController(RegistrarAuditoriaUseCase registrarAuditoriaUseCase,
            AuditoriaJpaRepository auditoriaJpaRepository) {
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    @PostMapping("/registrar/{tipo}")
    public ResponseEntity<AuditoriaResponse> registrarAuditoria(@PathVariable String tipo,
            @RequestBody Auditoria auditoria, HttpServletRequest request) {
        auditoria.setTipo(tipo);
        String ip_origen = request.getRemoteAddr();
        auditoria.setIp_origen(ip_origen);
        return ResponseEntity.ok(registrarAuditoriaUseCase.ejecutar(auditoria, tipo));
    }

    @GetMapping("/lista")
    public ResponseEntity<?> obtenerListaAuditorias() {
        return ResponseEntity.ok(auditoriaJpaRepository.findAll());
    }
}
