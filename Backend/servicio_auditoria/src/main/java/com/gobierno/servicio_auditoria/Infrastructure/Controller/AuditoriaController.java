package com.gobierno.servicio_auditoria.Infrastructure.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_auditoria.Application.UseCase.RegistrarAuditoriaUseCase;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Infrastructure.DTO.AuditoriaResponse;


@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    public AuditoriaController(RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
    }

    @PostMapping("/registrar/{tipo}")
    public AuditoriaResponse registrarAuditoria(@PathVariable String tipo, @RequestBody Auditoria auditoria) {
        
        return registrarAuditoriaUseCase.ejecutar(auditoria, tipo);
    }
}
