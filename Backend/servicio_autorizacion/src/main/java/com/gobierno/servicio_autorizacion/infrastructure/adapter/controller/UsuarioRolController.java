package com.gobierno.servicio_autorizacion.infrastructure.adapter.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_autorizacion.application.usecases.AsignarRolAUsuarioUseCase;
import com.gobierno.servicio_autorizacion.application.usecases.ObtenerRolesDeUsuarioUseCase;
import com.gobierno.servicio_autorizacion.application.usecases.QuitarRolAUsuarioUseCase;
import com.gobierno.servicio_autorizacion.domain.ports.out.UsuariosRolesPort;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRolController {

    private final AsignarRolAUsuarioUseCase asignarRolAUsuarioUseCase;
    private final QuitarRolAUsuarioUseCase quitarRolAUsuarioUseCase;
    private final ObtenerRolesDeUsuarioUseCase obtenerRolesDeUsuarioUseCase;
    private final UsuariosRolesPort usuariosRolesPort;

    public UsuarioRolController(AsignarRolAUsuarioUseCase asignarRolAUsuarioUseCase,
                                QuitarRolAUsuarioUseCase quitarRolAUsuarioUseCase,
                                ObtenerRolesDeUsuarioUseCase obtenerRolesDeUsuarioUseCase,
                                UsuariosRolesPort usuariosRolesPort) {
        this.asignarRolAUsuarioUseCase = asignarRolAUsuarioUseCase;
        this.quitarRolAUsuarioUseCase = quitarRolAUsuarioUseCase;
        this.obtenerRolesDeUsuarioUseCase = obtenerRolesDeUsuarioUseCase;
        this.usuariosRolesPort = usuariosRolesPort;
    }

    @GetMapping("/{username}/roles")
    public ResponseEntity<List<String>> obtenerRolesDeUsuario(@PathVariable String username) {
        List<String> roles = obtenerRolesDeUsuarioUseCase.ejecutar(username);
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/{username}/roles/{nombreRol}")
    public ResponseEntity<String> asignarRol(@PathVariable String username, @PathVariable String nombreRol) {
        asignarRolAUsuarioUseCase.ejecutar(username, nombreRol);
        return ResponseEntity.ok("Rol " + nombreRol + " asignado a " + username + " exitosamente");
    }

    @DeleteMapping("/{username}/roles/{nombreRol}")
    public ResponseEntity<String> quitarRol(@PathVariable String username, @PathVariable String nombreRol) {
        quitarRolAUsuarioUseCase.ejecutar(username, nombreRol);
        return ResponseEntity.ok("Rol " + nombreRol + " removido de " + username + " exitosamente");
    }

    @DeleteMapping("/{username}/roles/todos")
    public ResponseEntity<String> quitarTodosLosRoles(@PathVariable String username) {
        usuariosRolesPort.eliminarPorUsername(username);
        return ResponseEntity.ok("Todos los roles removidos de " + username);
    }
}
