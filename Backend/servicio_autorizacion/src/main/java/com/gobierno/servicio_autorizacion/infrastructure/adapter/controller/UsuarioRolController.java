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
public class UsuarioRolController { // Controlador para gestionar roles de usuarios

    private final AsignarRolAUsuarioUseCase asignarRolAUsuarioUseCase; // Caso de uso para asignar rol
    private final QuitarRolAUsuarioUseCase quitarRolAUsuarioUseCase; // Caso de uso para quitar rol
    private final ObtenerRolesDeUsuarioUseCase obtenerRolesDeUsuarioUseCase; // Caso de uso para obtener roles
    private final UsuariosRolesPort usuariosRolesPort; // Puerto de gestión de usuarios-roles

    public UsuarioRolController(AsignarRolAUsuarioUseCase asignarRolAUsuarioUseCase,
            QuitarRolAUsuarioUseCase quitarRolAUsuarioUseCase,
            ObtenerRolesDeUsuarioUseCase obtenerRolesDeUsuarioUseCase,
            UsuariosRolesPort usuariosRolesPort) {
        this.asignarRolAUsuarioUseCase = asignarRolAUsuarioUseCase;
        this.quitarRolAUsuarioUseCase = quitarRolAUsuarioUseCase;
        this.obtenerRolesDeUsuarioUseCase = obtenerRolesDeUsuarioUseCase;
        this.usuariosRolesPort = usuariosRolesPort;
    }

    @GetMapping("/{username}/roles") // GET /usuarios/{username}/roles
    public ResponseEntity<List<String>> obtenerRolesDeUsuario(@PathVariable String username) {
        List<String> roles = obtenerRolesDeUsuarioUseCase.ejecutar(username); // Obtiene los roles del usuario
        return ResponseEntity.ok(roles); // Retorna la lista de roles
    }

    @PostMapping("/{username}/roles/{nombreRol}") // POST /usuarios/{username}/roles/{nombreRol}
    public ResponseEntity<String> asignarRol(@PathVariable String username, @PathVariable String nombreRol) {
        asignarRolAUsuarioUseCase.ejecutar(username, nombreRol); // Asigna el rol al usuario
        return ResponseEntity.ok("Rol " + nombreRol + " asignado a " + username + " exitosamente");
    }

    @DeleteMapping("/{username}/roles/{nombreRol}") // DELETE /usuarios/{username}/roles/{nombreRol}
    public ResponseEntity<String> quitarRol(@PathVariable String username, @PathVariable String nombreRol) {
        quitarRolAUsuarioUseCase.ejecutar(username, nombreRol); // Quita el rol al usuario
        return ResponseEntity.ok("Rol " + nombreRol + " removido de " + username + " exitosamente");
    }

    @DeleteMapping("/{username}/roles/todos") // DELETE /usuarios/{username}/roles/todos
    public ResponseEntity<String> quitarTodosLosRoles(@PathVariable String username) {
        usuariosRolesPort.eliminarPorUsername(username); // Elimina todos los roles del usuario
        return ResponseEntity.ok("Todos los roles removidos de " + username);
    }
}