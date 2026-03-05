package com.gobierno.servicio_autorizacion.Infrastructure.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_autorizacion.Application.UseCase.CrearRolUseCase;
import com.gobierno.servicio_autorizacion.Domain.Model.Rol;

// Controlador REST para manejar las solicitudes relacionadas con los roles
@RestController
@RequestMapping("/roles")
public class RolController {

    private final CrearRolUseCase crearRolUseCase;

    public RolController(CrearRolUseCase crearRolUseCase) {
        this.crearRolUseCase = crearRolUseCase;
    }

    // Endpoint para crear un nuevo rol basado en el tipo de rol proporcionado
    @PostMapping("/crear/{tipoRol}")
    public Rol crearRol(@PathVariable String tipoRol) {
        return crearRolUseCase.ejecutar(tipoRol);
    }

}
