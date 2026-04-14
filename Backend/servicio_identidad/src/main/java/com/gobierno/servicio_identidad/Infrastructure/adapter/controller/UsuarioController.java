package com.gobierno.servicio_identidad.infrastructure.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_identidad.application.usecases.ActualizarPerfilUseCase;
import com.gobierno.servicio_identidad.application.usecases.ActualizarUsuarioUseCase;
import com.gobierno.servicio_identidad.application.usecases.EliminarUsuarioUseCase;
import com.gobierno.servicio_identidad.application.usecases.RegistrarPerfilUseCase;
import com.gobierno.servicio_identidad.application.usecases.RegistroUsuarioUseCase;
import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.AutenticadorPort;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.ActualizarUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.LoginRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.SolicitudAutenticacion;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioResponse;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.UsuarioJpaRepository;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final RegistroUsuarioUseCase registrarUsuarioUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final RegistrarPerfilUseCase registrarPerfilUseCase;
    private final ActualizarPerfilUseCase actualizarPerfilUseCase;
    private final UsuarioRepositorioPort usuarioRepositorioPort;
    private final AutenticadorPort autenticadorPort;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioController(RegistroUsuarioUseCase registrarUsuarioUseCase,
            ActualizarUsuarioUseCase actualizarUsuarioUseCase,
            EliminarUsuarioUseCase eliminarUsuarioUseCase,
            RegistrarPerfilUseCase registrarPerfilUseCase,
            ActualizarPerfilUseCase actualizarPerfilUseCase,
            UsuarioRepositorioPort usuarioRepositorioPort,
            AutenticadorPort autenticadorPort,
            UsuarioJpaRepository usuarioJpaRepository) {
                
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
        this.registrarPerfilUseCase = registrarPerfilUseCase;
        this.actualizarPerfilUseCase = actualizarPerfilUseCase;
        this.usuarioRepositorioPort = usuarioRepositorioPort;
        this.autenticadorPort = autenticadorPort;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        SolicitudAutenticacion solicitud = new SolicitudAutenticacion(request.getUsername(), request.getPassword(), null);
        String token = (String) autenticadorPort.autenticar(solicitud);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/validar")
    public ResponseEntity<Object> validarToken(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        SolicitudAutenticacion solicitud = new SolicitudAutenticacion(null, null, token);
        Object resultado = autenticadorPort.autenticar(solicitud);
        return ResponseEntity.ok(resultado);
    }

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

    @GetMapping("/perfil")
    public ResponseEntity<PerfilResponse> obtenerPerfil(Authentication authentication) {
        String username = authentication.getName();
        
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        
        PerfilUsuario perfil = registrarPerfilUseCase.obtenerPerfilPorUsername(username);
        if (perfil == null) {
            return ResponseEntity.notFound().build();
        }
        
        PerfilResponse response = new PerfilResponse(
                perfil.getId(),
                perfil.getUsuarioId(),
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getTelefono());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/perfil")
    public ResponseEntity<PerfilResponse> registrarPerfil(
            @RequestBody PerfilRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        
        PerfilUsuario perfil = registrarPerfilUseCase.ejecutar(
                usuario.getId().intValue(),
                request.getNombre(),
                request.getApellido(),
                request.getTelefono());
        
        PerfilResponse response = new PerfilResponse(
                perfil.getId(),
                perfil.getUsuarioId(),
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getTelefono());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/perfil")
    public ResponseEntity<PerfilResponse> actualizarPerfil(
            @RequestBody PerfilRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        
        PerfilUsuario perfil = actualizarPerfilUseCase.ejecutar(
                usuario.getId().intValue(),
                request.getNombre(),
                request.getApellido(),
                request.getTelefono());
        
        PerfilResponse response = new PerfilResponse(
                perfil.getId(),
                perfil.getUsuarioId(),
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getTelefono());
        
        return ResponseEntity.ok(response);
    }

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

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarUsuario(Authentication authentication) {

        String username = authentication.getName();
        eliminarUsuarioUseCase.ejecutar(username);

        return ResponseEntity.ok("Usuario Eliminado correctamente");
    }

    @GetMapping("/lista")
    public ResponseEntity<?> obtenerListaUsuarios() {
        return ResponseEntity.ok(usuarioJpaRepository.findAll());
    }
}
