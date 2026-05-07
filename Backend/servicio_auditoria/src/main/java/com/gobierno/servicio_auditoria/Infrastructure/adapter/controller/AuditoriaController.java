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
import com.gobierno.servicio_auditoria.application.usecases.ObtenerTimelineUseCase;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.AuditoriaResponse;
import com.gobierno.servicio_auditoria.infrastructure.adapter.dto.TimelineEventoDTO;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.AuditoriaJpaRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {  // Controlador REST para gestionar auditorías
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;  // Caso de uso para registrar auditorías
    private final AuditoriaJpaRepository auditoriaJpaRepository;  // Repositorio JPA de auditorías
    private final ObtenerTimelineUseCase obtenerTimelineUseCase;  // Caso de uso para timeline
    
    public AuditoriaController(RegistrarAuditoriaUseCase registrarAuditoriaUseCase,
            AuditoriaJpaRepository auditoriaJpaRepository,
            ObtenerTimelineUseCase obtenerTimelineUseCase) {
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
        this.auditoriaJpaRepository = auditoriaJpaRepository;
        this.obtenerTimelineUseCase = obtenerTimelineUseCase;
    }
    
    @PostMapping("/registrar/{tipo}")  // POST /auditoria/registrar/{tipo}
    public ResponseEntity<AuditoriaResponse> registrarAuditoria(  // Registra una nueva auditoría
            @PathVariable String tipo,  // Tipo de auditoría (BASICA, COMPLETA, SEGURIDAD)
            @RequestBody Auditoria auditoria,  // Datos de la auditoría
            HttpServletRequest request,  // Request HTTP para obtener IP
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor) {  // Header con IP original
        auditoria.setTipo(tipo);  // Asigna el tipo a la auditoría
        String ip_origen = (forwardedFor != null && !forwardedFor.isEmpty())  // Obtiene la IP de origen
                ? forwardedFor.split(",")[0].trim()  // Si viene de proxy, toma la primera IP
                : request.getRemoteAddr();  // Si no, toma la IP del request
        auditoria.setIp_origen(ip_origen);  // Asigna la IP de origen
        return ResponseEntity.ok(registrarAuditoriaUseCase.ejecutar(auditoria, tipo));  // Ejecuta el caso de uso y retorna la respuesta
    }
    
    private Timestamp parsearFecha(String fechaStr) {  // Convierte string a Timestamp
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

    @GetMapping("/lista")  // GET /auditoria/lista
    public ResponseEntity<List<Auditoria>> obtenerListaAuditorias(  // Lista auditorías con filtros
            @RequestParam(required = false) Integer usuarioId,  // Filtro por ID de usuario
            @RequestParam(required = false) String fechaDesde,  // Filtro por fecha desde
            @RequestParam(required = false) String fechaHasta,  // Filtro por fecha hasta
            @RequestParam(required = false) String tipo,  // Filtro por tipo
            @RequestParam(required = false) String accion) {  // Filtro por acción
        
        Timestamp fechaDesdeParsed = parsearFecha(fechaDesde);  // Convierte fecha desde a Timestamp
        Timestamp fechaHastaParsed = parsearFecha(fechaHasta);  // Convierte fecha hasta a Timestamp
        
        boolean tieneFiltros = usuarioId != null || fechaDesdeParsed != null ||  // Verifica si hay filtros
                               fechaHastaParsed != null || (tipo != null && !tipo.isBlank()) || 
                               (accion != null && !accion.isBlank());
        
        List<Auditoria> auditorias;
        
        if (!tieneFiltros) {  // Si no hay filtros
            auditorias = auditoriaJpaRepository.findAll();  // Retorna todas las auditorías
        } else {  // Si hay filtros
            auditorias = auditoriaJpaRepository.findAll().stream()  // Filtra las auditorías
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
        
        return ResponseEntity.ok(auditorias);  // Retorna la lista de auditorías
    }

    @GetMapping("/usuario/{usuarioId}/timeline")  // GET /auditoria/usuario/{usuarioId}/timeline
    public ResponseEntity<List<TimelineEventoDTO>> obtenerTimelineUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Integer limite) {
        
        List<TimelineEventoDTO> timeline = obtenerTimelineUseCase.obtenerTimelinePorUsuario(usuarioId, limite);
        return ResponseEntity.ok(timeline);
    }
}