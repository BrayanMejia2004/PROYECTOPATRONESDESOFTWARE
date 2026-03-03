package com.gobierno.servicio_identidad.Infrastructure.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_identidad.Application.UseCase.ActualizarUsuarioUseCase;
import com.gobierno.servicio_identidad.Application.UseCase.EliminarUsuarioUseCase;
import com.gobierno.servicio_identidad.Application.UseCase.LoginUsuarioUseCase;
import com.gobierno.servicio_identidad.Application.UseCase.RegistroUsuarioUseCase;

import com.gobierno.servicio_identidad.Domain.Model.Usuario;

import com.gobierno.servicio_identidad.Infrastructure.Dto.ActualizarUsuarioRequest;
import com.gobierno.servicio_identidad.Infrastructure.Dto.LoginRequest;
import com.gobierno.servicio_identidad.Infrastructure.Dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.Infrastructure.Dto.UsuarioResponse;

// Controlador REST para manejar las solicitudes relacionadas con los usuarios
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    // Dependencias para los casos de uso de login y registro de usuarios,
    // inyectados a través del constructor
    private final LoginUsuarioUseCase loginUsuarioUseCase;
    private final RegistroUsuarioUseCase registrarUsuarioUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;

    // Constructor para inyectar las dependencias necesarias para el controlador
    public UsuarioController(LoginUsuarioUseCase loginUsuarioUseCase, RegistroUsuarioUseCase registrarUsuarioUseCase, ActualizarUsuarioUseCase actualizarUsuarioUseCase, EliminarUsuarioUseCase eliminarUsuarioUseCase) {
        this.loginUsuarioUseCase = loginUsuarioUseCase;
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
    }

    // Endpoint para el inicio de sesión de usuarios
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        String token = loginUsuarioUseCase.ejecutar(
                request.getUsername(),
                request.getPassword());

        return ResponseEntity.ok(token);
    }

    // Endpoint para el registro de nuevos usuarios
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrarUsuario(@RequestBody RegistroUsuarioRequest request) {

        Usuario nuevoUsuario = registrarUsuarioUseCase.ejecutar(
                request.getUsername(),
                request.getPassword(),
                request.getEmail());

        UsuarioResponse response = new UsuarioResponse(
                nuevoUsuario.getId(),
                nuevoUsuario.getUsername(),
                nuevoUsuario.getEmail());

        return ResponseEntity.ok(response);
    }

    // Endpoint protegido que requiere autenticación
    @GetMapping("/perfil")
    public ResponseEntity<String> perfil() {
        return ResponseEntity.ok("Acceso autorizado al perfil");
    }

    // Endpoint para actualizar la información del usuario autenticado
    @PutMapping("/actualizar")
    public ResponseEntity<Usuario> actualizarUsuario(
            @RequestBody ActualizarUsuarioRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        Usuario usuarioActualizado = actualizarUsuarioUseCase.ejecutar(
                username,
                request.getEmail(),
                request.getPassword());

        return ResponseEntity.ok(usuarioActualizado);
    }

    // Endpoint para eliminar la cuenta del usuario autenticado
    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarUsuario(Authentication authentication) {

        String username = authentication.getName();
        eliminarUsuarioUseCase.ejecutar(username);

        return ResponseEntity.ok("Usuario Eliminado correctamente");
    }
}
