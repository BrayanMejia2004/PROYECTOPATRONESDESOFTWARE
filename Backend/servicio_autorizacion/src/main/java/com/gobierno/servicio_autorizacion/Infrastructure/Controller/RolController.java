package com.gobierno.servicio_autorizacion.Infrastructure.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_autorizacion.Application.UseCase.CrearRolUseCase;
import com.gobierno.servicio_autorizacion.Domain.Model.Rol;
import com.gobierno.servicio_autorizacion.Infrastructure.Persistence.RolJpaRepository;

@RestController
@RequestMapping("/roles")
public class RolController {

    private final CrearRolUseCase crearRolUseCase;
    private final RolJpaRepository rolRepository;

    public RolController(CrearRolUseCase crearRolUseCase, RolJpaRepository rolRepository) {
        this.crearRolUseCase = crearRolUseCase;
        this.rolRepository = rolRepository;
    }

    @PostMapping("/crear/{tipoRol}")
    public Rol crearRol(@PathVariable String tipoRol) {
        return crearRolUseCase.ejecutar(tipoRol);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> obtenerListaRoles() {
        return ResponseEntity.ok(rolRepository.findAll());
    }
}
