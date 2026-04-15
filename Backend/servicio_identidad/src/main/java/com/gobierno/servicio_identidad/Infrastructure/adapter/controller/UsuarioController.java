package com.gobierno.servicio_identidad.infrastructure.adapter.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AuditoriaClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.GeneradorJwtAdapter;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.ActualizarUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.LoginRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.SolicitudAutenticacion;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioCompletoResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioListaResponse;
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
    private final GeneradorJwtAdapter generadorJwtAdapter;
    private final AutorizacionClient autorizacionClient;
    private final AuditoriaClient auditoriaClient;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(RegistroUsuarioUseCase registrarUsuarioUseCase,
            ActualizarUsuarioUseCase actualizarUsuarioUseCase,
            EliminarUsuarioUseCase eliminarUsuarioUseCase,
            RegistrarPerfilUseCase registrarPerfilUseCase,
            ActualizarPerfilUseCase actualizarPerfilUseCase,
            UsuarioRepositorioPort usuarioRepositorioPort,
            AutenticadorPort autenticadorPort,
            UsuarioJpaRepository usuarioJpaRepository,
            GeneradorJwtAdapter generadorJwtAdapter,
            AutorizacionClient autorizacionClient,
            AuditoriaClient auditoriaClient,
            PasswordEncoder passwordEncoder) {
                
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
        this.registrarPerfilUseCase = registrarPerfilUseCase;
        this.actualizarPerfilUseCase = actualizarPerfilUseCase;
        this.usuarioRepositorioPort = usuarioRepositorioPort;
        this.autenticadorPort = autenticadorPort;
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.generadorJwtAdapter = generadorJwtAdapter;
        this.autorizacionClient = autorizacionClient;
        this.auditoriaClient = auditoriaClient;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        SolicitudAutenticacion solicitud = new SolicitudAutenticacion(request.getUsername(), request.getPassword(), null);
        autenticadorPort.autenticar(solicitud);
        
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<String> roles = autorizacionClient.obtenerRolesDeUsuario(request.getUsername());
        
        if (roles.isEmpty()) {
            autorizacionClient.asignarRolAUsuario(request.getUsername(), "USER");
            roles = autorizacionClient.obtenerRolesDeUsuario(request.getUsername());
        }
        
        String token = generadorJwtAdapter.generarToken(usuario, roles);
        
        auditoriaClient.registrarAuditoria(
            usuario.getId().intValue(),
            "LOGIN",
            "Usuario " + request.getUsername() + " inici\u00f3 sesi\u00f3n",
            "BASICA"
        );
        
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

        try {
            autorizacionClient.asignarRolAUsuario(request.getUsername(), "USER");
        } catch (Exception e) {
            System.err.println("No se pudo asignar rol USER: " + e.getMessage());
        }

        auditoriaClient.registrarAuditoria(
            nuevoUsuario.getId().intValue(),
            "REGISTRO_USUARIO",
            "Usuario " + request.getUsername() + " se registr\u00f3",
            "BASICA"
        );

        UsuarioResponse response = new UsuarioResponse(
                nuevoUsuario.getId(),
                nuevoUsuario.getUsername(),
                nuevoUsuario.getEmail());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioCompletoResponse> obtenerUsuarioCompleto(Authentication authentication) {
        String username = authentication.getName();
        
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<String> roles = autorizacionClient.obtenerRolesDeUsuario(username);
        
        PerfilUsuario perfil = registrarPerfilUseCase.obtenerPerfilPorUsername(username);
        
        UsuarioCompletoResponse.PerfilInfo perfilInfo = null;
        if (perfil != null) {
            perfilInfo = new UsuarioCompletoResponse.PerfilInfo(
                    perfil.getNombre(),
                    perfil.getApellido(),
                    perfil.getTelefono(),
                    usuario.getEmail()
            );
        }
        
        UsuarioCompletoResponse response = new UsuarioCompletoResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                roles,
                perfilInfo
        );
        
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
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getTelefono(),
                usuario.getEmail());
        
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
        
        auditoriaClient.registrarAuditoria(
            usuario.getId().intValue(),
            "REGISTRAR_PERFIL",
            "Usuario " + username + " cre\u00f3 su perfil",
            "COMPLETA"
        );

        PerfilResponse response = new PerfilResponse(
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getTelefono(),
                usuario.getEmail());
        
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
        
        auditoriaClient.registrarAuditoria(
            usuario.getId().intValue(),
            "ACTUALIZAR_PERFIL",
            "Usuario " + username + " actualiz\u00f3 su perfil",
            "COMPLETA"
        );

        PerfilResponse response = new PerfilResponse(
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getTelefono(),
                usuario.getEmail());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/actualizar")
    public ResponseEntity<Usuario> actualizarUsuario(
            @RequestBody ActualizarUsuarioRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);

        Usuario usuarioActualizado = actualizarUsuarioUseCase.ejecutar(
                username,
                request.getEmail(),
                request.getPassword());
        
        auditoriaClient.registrarAuditoria(
            usuario.getId().intValue(),
            "ACTUALIZAR_USUARIO",
            "Usuario " + username + " actualiz\u00f3 sus datos",
            "BASICA"
        );

        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarUsuario(Authentication authentication) {

        String username = authentication.getName();
        
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario != null) {
            auditoriaClient.registrarAuditoria(
                usuario.getId().intValue(),
                "ELIMINAR_USUARIO",
                "Usuario " + username + " elimin\u00f3 su cuenta",
                "SEGURIDAD"
            );
        }
        
        eliminarUsuarioUseCase.ejecutar(username);

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
        autorizacionClient.asignarRolAUsuario(username, tipoRol);
        return ResponseEntity.ok("Rol " + tipoRol + " asignado a " + username + " exitosamente");
    }

    @DeleteMapping("/{username}/roles/{tipoRol}")
    public ResponseEntity<String> quitarRol(@PathVariable String username, @PathVariable String tipoRol) {
        autorizacionClient.quitarRolAUsuario(username, tipoRol);
        return ResponseEntity.ok("Rol " + tipoRol + " removido de " + username + " exitosamente");
    }

    @GetMapping("/{username}/roles")
    public ResponseEntity<List<String>> obtenerRoles(@PathVariable String username) {
        List<String> roles = autorizacionClient.obtenerRolesDeUsuario(username);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/roles/lista")
    public ResponseEntity<List<String>> listarRolesDisponibles() {
        List<String> roles = autorizacionClient.obtenerListaRoles();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> actualizarUsuarioAdmin(@PathVariable String username,
                                                   @RequestBody ActualizarUsuarioAdminRequest request,
                                                   Authentication authentication) {
        if (authentication.getName().equals(username)) {
            return ResponseEntity.badRequest()
                    .body("No puedes editarte a ti mismo");
        }

        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            String nuevoUsername = request.getUsername();
            if (!nuevoUsername.equals(username) && usuarioRepositorioPort.existePorUsername(nuevoUsername)) {
                return ResponseEntity.badRequest()
                        .body("Ya existe un usuario con el nombre: " + nuevoUsername);
            }
            usuario.setUsername(nuevoUsername);
        }

        if (request.getEmail() != null) {
            usuario.actualizarEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.actualizarPassword(passwordEncoder.encode(request.getPassword()));
        }

        usuarioRepositorioPort.guardar(usuario);
        
        auditoriaClient.registrarAuditoria(
            usuario.getId().intValue(),
            "EDITAR_USUARIO",
            "Admin " + authentication.getName() + " edit\u00f3 usuario " + username,
            "SEGURIDAD"
        );

        return ResponseEntity.ok(new UsuarioListaResponse(
                usuario.getId(), usuario.getUsername(), usuario.getEmail()));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> eliminarUsuarioAdmin(@PathVariable String username,
                                                 Authentication authentication) {
        if (authentication.getName().equals(username)) {
            return ResponseEntity.badRequest()
                    .body("No puedes eliminarte a ti mismo");
        }

        if (!usuarioRepositorioPort.existePorUsername(username)) {
            return ResponseEntity.notFound().build();
        }
        
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        Integer usuarioId = (usuario != null) ? usuario.getId().intValue() : 0;

        auditoriaClient.registrarAuditoria(
            usuarioId,
            "ELIMINAR_USUARIO",
            "Admin " + authentication.getName() + " elimin\u00f3 usuario " + username,
            "SEGURIDAD"
        );

        autorizacionClient.quitarTodosLosRolesDeUsuario(username);
        eliminarUsuarioUseCase.ejecutar(username);

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
