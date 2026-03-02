package com.gobierno.servicio_identidad.Infrastructure.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_identidad.Application.UseCase.LoginUsuarioUseCase;
import com.gobierno.servicio_identidad.Application.UseCase.RegistroUsuarioUseCase;
import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Infrastructure.Dto.UsuarioResponse;

// Controlador REST para manejar las solicitudes relacionadas con los usuarios
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    // Dependencias para los casos de uso de login y registro de usuarios, inyectados a través del constructor
    private final LoginUsuarioUseCase loginUsuarioUseCase;
    private final RegistroUsuarioUseCase registrarUsuarioUseCase;

    // Constructor para inyectar las dependencias necesarias para el controlador
    public UsuarioController(LoginUsuarioUseCase loginUsuarioUseCase, RegistroUsuarioUseCase registrarUsuarioUseCase) {
        this.loginUsuarioUseCase = loginUsuarioUseCase;
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
    }

    // Endpoint para el inicio de sesión de usuarios, que recibe una solicitud con el nombre de usuario y la contraseña
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        String token = loginUsuarioUseCase.ejecutar(
                request.getUsername(),
                request.getPassword());

        return ResponseEntity.ok(token);
    }

    // Endpoint para el registro de nuevos usuarios, que recibe una solicitud con el nombre de usuario, contraseña y correo electrónico
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
}
