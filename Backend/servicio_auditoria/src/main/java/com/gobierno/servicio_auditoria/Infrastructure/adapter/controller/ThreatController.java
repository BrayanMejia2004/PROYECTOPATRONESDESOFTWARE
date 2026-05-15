package com.gobierno.servicio_auditoria.infrastructure.adapter.controller;

import com.gobierno.servicio_auditoria.application.usecases.DetectarAmenazasUseCase;
import com.gobierno.servicio_auditoria.domain.model.ThreatEventDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/auditoria/threats")
public class ThreatController {

    private final DetectarAmenazasUseCase detectarAmenazasUseCase;
    private final ThreatSseService threatSseService;

    public ThreatController(DetectarAmenazasUseCase detectarAmenazasUseCase,
                            ThreatSseService threatSseService) {
        this.detectarAmenazasUseCase = detectarAmenazasUseCase;
        this.threatSseService = threatSseService;
    }

    @GetMapping("/activas")
    public ResponseEntity<List<ThreatEventDTO>> obtenerAmenazasActivas() {
        return ResponseEntity.ok(detectarAmenazasUseCase.obtenerAmenazasActivas());
    }

    @GetMapping("/historial")
    public ResponseEntity<List<ThreatEventDTO>> obtenerHistorialAmenazas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(detectarAmenazasUseCase.obtenerHistorialAmenazas(desde, hasta));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAmenazas() {
        return threatSseService.createEmitter();
    }

    @PutMapping("/{id}/resolver")
    public ResponseEntity<Void> resolverAmenaza(@PathVariable Long id) {
        detectarAmenazasUseCase.resolverAmenaza(id);
        return ResponseEntity.ok().build();
    }
}