package com.gobierno.servicio_auditoria.Infrastructure.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gobierno.servicio_auditoria.Application.UseCase.RegistrarAuditoriaUseCase;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;
import com.gobierno.servicio_auditoria.Infrastructure.Persistence.AuditoriaJpaRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;
    private final AuditoriaJpaRepository auditoriaRepository;

    public AuditoriaController(RegistrarAuditoriaUseCase registrarAuditoriaUseCase,
            AuditoriaJpaRepository auditoriaRepository) {
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
        this.auditoriaRepository = auditoriaRepository;
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
        return ResponseEntity.ok(auditoriaRepository.findAll());
    }
}
