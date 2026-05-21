package com.gobierno.servicio_auditoria.infrastructure.adapter.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_auditoria.application.usecases.ObtenerAnalisisUseCase;
import com.gobierno.servicio_auditoria.domain.model.AnalisisRequestDTO;
import com.gobierno.servicio_auditoria.domain.model.AnalisisResponseDTO;

@RestController
@RequestMapping("/auditoria/analisis")
public class AnalisisController {

    private final ObtenerAnalisisUseCase obtenerAnalisisUseCase;

    public AnalisisController(ObtenerAnalisisUseCase obtenerAnalisisUseCase) {
        this.obtenerAnalisisUseCase = obtenerAnalisisUseCase;
    }

    @PostMapping
    public ResponseEntity<AnalisisResponseDTO> ejecutarAnalisis(@RequestBody AnalisisRequestDTO request) {
        AnalisisResponseDTO response = obtenerAnalisisUseCase.ejecutarAnalisis(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/estrategias")
    public ResponseEntity<List<Map<String, Object>>> listarEstrategias() {
        return ResponseEntity.ok(obtenerAnalisisUseCase.listarEstrategias());
    }

    @GetMapping("/insights")
    public ResponseEntity<List<String>> obtenerInsights() {
        return ResponseEntity.ok(obtenerAnalisisUseCase.obtenerInsights());
    }
}
