package com.gobierno.servicio_identidad.Infrastructure.Controller;

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

import com.gobierno.servicio_identidad.Application.UseCase.ActualizarPerfilUseCase;
import com.gobierno.servicio_identidad.Application.UseCase.ActualizarUsuarioUseCase;
import com.gobierno.servicio_identidad.Application.UseCase.EliminarUsuarioUseCase;
import com.gobierno.servicio_identidad.Application.UseCase.RegistrarPerfilUseCase;
import com.gobierno.servicio_identidad.Application.UseCase.RegistroUsuarioUseCase;
import com.gobierno.servicio_identidad.Domain.Model.PerfilUsuario;
import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Infrastructure.Dto.ActualizarUsuarioRequest;
import com.gobierno.servicio_identidad.Infrastructure.Dto.LoginRequest;
import com.gobierno.servicio_identidad.Infrastructure.Dto.PerfilRequest;
import com.gobierno.servicio_identidad.Infrastructure.Dto.PerfilResponse;
import com.gobierno.servicio_identidad.Infrastructure.Dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.Infrastructure.Dto.SolicitudAutenticacion;
import com.gobierno.servicio_identidad.Infrastructure.Dto.UsuarioResponse;
import com.gobierno.servicio_identidad.Ports.Output.Autenticador;
import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final RegistroUsuarioUseCase registrarUsuarioUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final RegistrarPerfilUseCase registrarPerfilUseCase;
    private final ActualizarPerfilUseCase actualizarPerfilUseCase;
    private final UsuarioRepositorio usuarioRepositorio;
    private final Autenticador autenticador;

    public UsuarioController(RegistroUsuarioUseCase registrarUsuarioUseCase,
            ActualizarUsuarioUseCase actualizarUsuarioUseCase,
            EliminarUsuarioUseCase eliminarUsuarioUseCase,
            RegistrarPerfilUseCase registrarPerfilUseCase,
            ActualizarPerfilUseCase actualizarPerfilUseCase,
            UsuarioRepositorio usuarioRepositorio,
            Autenticador autenticador) {
                
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
        this.registrarPerfilUseCase = registrarPerfilUseCase;
        this.actualizarPerfilUseCase = actualizarPerfilUseCase;
        this.usuarioRepositorio = usuarioRepositorio;
        this.autenticador = autenticador;
    }

    // Endpoint para el inicio de sesión de usuarios (Con Adapter)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        SolicitudAutenticacion solicitud = new SolicitudAutenticacion(request.getUsername(), request.getPassword(),
                null);

        String token = (String) autenticador.autenticar(solicitud);

        return ResponseEntity.ok(token);
    }

    // Endpoint para validar del token
    @GetMapping("/validar")
    public ResponseEntity<Object> validarToken(@RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        SolicitudAutenticacion solicitud =
                new SolicitudAutenticacion(null, null, token);

        Object resultado = autenticador.autenticar(solicitud);

        return ResponseEntity.ok(resultado);
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

    // Endpoint para obtener el perfil del usuario autenticado
    @GetMapping("/perfil")
    public ResponseEntity<PerfilResponse> obtenerPerfil(Authentication authentication) {
        String username = authentication.getName();
        
        Usuario usuario = usuarioRepositorio.buscarPorUsername(username).orElse(null);
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
        
        Usuario usuario = usuarioRepositorio.buscarPorUsername(username).orElse(null);
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
        
        Usuario usuario = usuarioRepositorio.buscarPorUsername(username).orElse(null);
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
