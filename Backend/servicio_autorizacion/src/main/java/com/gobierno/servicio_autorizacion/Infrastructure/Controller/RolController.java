package com.gobierno.servicio_autorizacion.Infrastructure.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_autorizacion.Application.UseCase.CrearRolUseCase;
import com.gobierno.servicio_autorizacion.Domain.Model.Rol;

@RestController
@RequestMapping("/roles")
public class RolController {

    private final CrearRolUseCase crearRolUseCase;

    public RolController(CrearRolUseCase crearRolUseCase) {
        this.crearRolUseCase = crearRolUseCase;
    }

    @PostMapping("/crear/{tipo}")
    public ResponseEntity<Rol> crearRol(@PathVariable String tipo) {

        Rol rol = crearRolUseCase.ejecutar(tipo);
        return ResponseEntity.ok(rol);
    }

}
