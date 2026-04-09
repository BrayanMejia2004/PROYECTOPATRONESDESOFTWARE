package com.gobierno.servicio_auditoria.Infrastructure.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gobierno.servicio_auditoria.Application.UseCase.RegistrarAuditoriaUseCase;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;
import jakarta.servlet.http.HttpServletRequest;

// Controlador REST para manejar las solicitudes relacionadas con las auditorias
@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    // Caso de uso para registrar auditorias
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    // Constructor que inyecta el caso de uso
    public AuditoriaController(RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
    }

    // Endpoint para registrar auditorias (BASICA, SEGURIDAD, COMPLETA)
    @PostMapping("/registrar/{tipo}")
    public ResponseEntity<AuditoriaResponse> registrarAuditoria(@PathVariable String tipo,
            @RequestBody Auditoria auditoria, HttpServletRequest request) {

        // Asigna el tipo del path al objeto auditoria
        auditoria.setTipo(tipo);
        
        // Obtiene la IP del cliente desde la peticion HTTP
        String ip_origen = request.getRemoteAddr();
        auditoria.setIp_origen(ip_origen);
        
        // Delega la logica al caso de uso y retorna 200 OK con la respuesta
        return ResponseEntity.ok(registrarAuditoriaUseCase.ejecutar(auditoria, tipo));
    }
}
