package com.gobierno.servicio_identidad.infrastructure.adapter.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_identidad.application.facade.AutenticacionFacade;
import com.gobierno.servicio_identidad.application.facade.GestionUsuarioFacade;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.ActualizarUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.LoginRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioCompletoResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioListaResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioResponse;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.UsuarioJpaRepository;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final AutenticacionFacade autenticacionFacade;
    private final GestionUsuarioFacade gestionUsuarioFacade;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioController(
            AutenticacionFacade autenticacionFacade,
            GestionUsuarioFacade gestionUsuarioFacade,
            UsuarioJpaRepository usuarioJpaRepository) {
        this.autenticacionFacade = autenticacionFacade;
        this.gestionUsuarioFacade = gestionUsuarioFacade;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = autenticacionFacade.login(request);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/validar")
    public ResponseEntity<Object> validarToken(@RequestHeader("Authorization") String token) {
        Object resultado = autenticacionFacade.validarToken(token);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrarUsuario(@RequestBody RegistroUsuarioRequest request) {
        UsuarioResponse response = autenticacionFacade.registrarUsuario(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioCompletoResponse> obtenerUsuarioCompleto(Authentication authentication) {
        String username = authentication.getName();
        UsuarioCompletoResponse response = gestionUsuarioFacade.obtenerUsuarioCompleto(username);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/perfil")
    public ResponseEntity<PerfilResponse> obtenerPerfil(Authentication authentication) {
        String username = authentication.getName();
        PerfilResponse response = gestionUsuarioFacade.obtenerPerfil(username);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/perfil")
    public ResponseEntity<PerfilResponse> registrarPerfil(
            @RequestBody PerfilRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        PerfilResponse response = gestionUsuarioFacade.registrarPerfil(username, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/perfil")
    public ResponseEntity<PerfilResponse> actualizarPerfil(
            @RequestBody PerfilRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        PerfilResponse response = gestionUsuarioFacade.actualizarPerfil(username, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<Usuario> actualizarUsuario(
            @RequestBody ActualizarUsuarioRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = gestionUsuarioFacade.actualizarUsuario(
                username,
                request.getEmail(),
                request.getPassword());
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarUsuario(Authentication authentication) {
        String username = authentication.getName();
        gestionUsuarioFacade.eliminarUsuario(username);
        return ResponseEntity.ok("Usuario Eliminado correctamente");
    }

    @GetMapping("/lista")
    public ResponseEntity<?> obtenerListaUsuarios() {
        List<UsuarioListaResponse> usuarios = usuarioJpaRepository.findAll().stream()
                .map(u -> new UsuarioListaResponse(u.getId(), u.getUsername(), u.getEmail()))
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{username}/id")
    public ResponseEntity<?> obtenerIdPorUsername(@PathVariable String username) {
        Usuario usuario = usuarioJpaRepository.findByUsername(username).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario.getId().intValue());
    }

    @PostMapping("/{username}/roles/{tipoRol}")
    public ResponseEntity<String> asignarRol(@PathVariable String username, @PathVariable String tipoRol) {
        gestionUsuarioFacade.asignarRol(username, tipoRol);
        return ResponseEntity.ok("Rol " + tipoRol + " asignado a " + username + " exitosamente");
    }

    @DeleteMapping("/{username}/roles/{tipoRol}")
    public ResponseEntity<String> quitarRol(@PathVariable String username, @PathVariable String tipoRol) {
        gestionUsuarioFacade.quitarRol(username, tipoRol);
        return ResponseEntity.ok("Rol " + tipoRol + " removido de " + username + " exitosamente");
    }

    @GetMapping("/{username}/roles")
    public ResponseEntity<List<String>> obtenerRoles(@PathVariable String username) {
        List<String> roles = gestionUsuarioFacade.obtenerRoles(username);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/roles/lista")
    public ResponseEntity<List<String>> listarRolesDisponibles() {
        List<String> roles = gestionUsuarioFacade.listarRolesDisponibles();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> actualizarUsuarioAdmin(@PathVariable String username,
                                                   @RequestBody ActualizarUsuarioAdminRequest request,
                                                   Authentication authentication) {
        try {
            UsuarioListaResponse response = gestionUsuarioFacade.actualizarUsuarioAdmin(
                    authentication.getName(),
                    username,
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("editarte a ti mismo")) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            if (e.getMessage().contains("Ya existe")) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> eliminarUsuarioAdmin(@PathVariable String username,
                                                 Authentication authentication) {
        if (authentication.getName().equals(username)) {
            return ResponseEntity.badRequest()
                    .body("No puedes eliminarte a ti mismo");
        }

        gestionUsuarioFacade.eliminarUsuarioAdmin(authentication.getName(), username);
        return ResponseEntity.ok("Usuario " + username + " eliminado exitosamente");
    }

    public static class ActualizarUsuarioAdminRequest {
        private String username;
        private String email;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
