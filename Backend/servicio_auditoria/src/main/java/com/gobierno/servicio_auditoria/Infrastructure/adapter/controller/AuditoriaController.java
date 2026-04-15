package com.gobierno.servicio_auditoria.infrastructure.adapter.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<AuditoriaResponse> registrarAuditoria(
            @PathVariable String tipo,
            @RequestBody Auditoria auditoria,
            HttpServletRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor) {
        auditoria.setTipo(tipo);
        String ip_origen = (forwardedFor != null && !forwardedFor.isEmpty())
                ? forwardedFor.split(",")[0].trim()
                : request.getRemoteAddr();
        auditoria.setIp_origen(ip_origen);
        return ResponseEntity.ok(registrarAuditoriaUseCase.ejecutar(auditoria, tipo));
    }
    private Timestamp parsearFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isBlank()) {
            return null;
        }
        try {
            LocalDateTime fecha = LocalDateTime.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return Timestamp.valueOf(fecha);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/lista")
    public ResponseEntity<List<Auditoria>> obtenerListaAuditorias(
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String accion) {
        
        Timestamp fechaDesdeParsed = parsearFecha(fechaDesde);
        Timestamp fechaHastaParsed = parsearFecha(fechaHasta);
        
        boolean tieneFiltros = usuarioId != null || fechaDesdeParsed != null || 
                               fechaHastaParsed != null || (tipo != null && !tipo.isBlank()) || 
                               (accion != null && !accion.isBlank());
        
        List<Auditoria> auditorias;
        
        if (!tieneFiltros) {
            auditorias = auditoriaJpaRepository.findAll();
        } else {
            auditorias = auditoriaJpaRepository.findAll().stream()
                .filter(a -> usuarioId == null || 
                            (a.getUsuario_id() != null && a.getUsuario_id().equals(usuarioId)))
                .filter(a -> fechaDesdeParsed == null || 
                            (a.getFecha() != null && !a.getFecha().before(fechaDesdeParsed)))
                .filter(a -> fechaHastaParsed == null || 
                            (a.getFecha() != null && !a.getFecha().after(fechaHastaParsed)))
                .filter(a -> tipo == null || tipo.isBlank() || 
                            (a.getTipo() != null && a.getTipo().equalsIgnoreCase(tipo)))
                .filter(a -> accion == null || accion.isBlank() || 
                            (a.getAccion() != null && a.getAccion().equalsIgnoreCase(accion)))
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(auditorias);
    }
}
