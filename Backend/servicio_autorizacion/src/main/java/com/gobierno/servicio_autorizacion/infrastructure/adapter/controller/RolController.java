package com.gobierno.servicio_autorizacion.infrastructure.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_autorizacion.application.usecases.CrearRolUseCase;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@RestController
@RequestMapping("/roles")
public class RolController {

    private final CrearRolUseCase crearRolUseCase;
    private final RolJpaRepository rolJpaRepository;

    public RolController(CrearRolUseCase crearRolUseCase, RolJpaRepository rolJpaRepository) {
        this.crearRolUseCase = crearRolUseCase;
        this.rolJpaRepository = rolJpaRepository;
    }

    @PostMapping("/crear/{tipoRol}")
    public Rol crearRol(@PathVariable String tipoRol) {
        return crearRolUseCase.ejecutar(tipoRol);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> obtenerListaRoles() {
        return ResponseEntity.ok(rolJpaRepository.findAll());
    }
}
