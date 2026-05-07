package com.gobierno.servicio_identidad.infrastructure.adapter.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_identidad.application.facade.AutenticacionFacade;
import com.gobierno.servicio_identidad.application.facade.GestionUsuarioFacade;
import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.ActualizarUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.LoginRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioCompletoResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioListaResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioResponse;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.PerfilUsuarioJpaRepository;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.UsuarioJpaRepository;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController { // Controlador REST para gestionar usuarios

    private final AutenticacionFacade autenticacionFacade;  // Facade de autenticación
    private final GestionUsuarioFacade gestionUsuarioFacade;  // Facade de gestión de usuarios
    private final UsuarioJpaRepository usuarioJpaRepository;  // Repositorio JPA de usuarios
    private final PerfilUsuarioJpaRepository perfilUsuarioJpaRepository;  // Repositorio JPA de perfiles

    public UsuarioController(  // Constructor con inyección de dependencias
            AutenticacionFacade autenticacionFacade,  // Inyecta el facade de autenticación
            GestionUsuarioFacade gestionUsuarioFacade,  // Inyecta el facade de gestión de usuarios
            UsuarioJpaRepository usuarioJpaRepository,  // Inyecta el repositorio JPA
            PerfilUsuarioJpaRepository perfilUsuarioJpaRepository) {  // Inyecta el repositorio JPA de perfiles
        this.autenticacionFacade = autenticacionFacade;  // Asigna el facade de autenticación
        this.gestionUsuarioFacade = gestionUsuarioFacade;  // Asigna el facade de gestión de usuarios
        this.usuarioJpaRepository = usuarioJpaRepository;  // Asigna el repositorio JPA
        this.perfilUsuarioJpaRepository = perfilUsuarioJpaRepository;  // Asigna el repositorio JPA de perfiles
    }

    @PostMapping("/login") // Endpoint: POST /usuarios/login
    public ResponseEntity<String> login(@RequestBody LoginRequest request) { // Método para login de usuario
        String token = autenticacionFacade.login(request); // Llama al facade de autenticación
        return ResponseEntity.ok(token); // Retorna el token JWT
    }

    @GetMapping("/validar") // Endpoint: GET /usuarios/validar
    public ResponseEntity<Object> validarToken(@RequestHeader("Authorization") String token) { // Método para validar
                                                                                               // token
        Object resultado = autenticacionFacade.validarToken(token); // Llama al facade para validar
        return ResponseEntity.ok(resultado); // Retorna el resultado de la validación
    }

    @PostMapping("/registro") // Endpoint: POST /usuarios/registro
    public ResponseEntity<UsuarioResponse> registrarUsuario(@RequestBody RegistroUsuarioRequest request) { // Registro
                                                                                                           // de nuevo
                                                                                                           // usuario
        UsuarioResponse response = autenticacionFacade.registrarUsuario(request); // Llama al facade de autenticación
        return ResponseEntity.ok(response); // Retorna los datos del usuario registrado
    }

    @GetMapping("/me") // Endpoint: GET /usuarios/me
    public ResponseEntity<UsuarioCompletoResponse> obtenerUsuarioCompleto(Authentication authentication) { // Obtiene
                                                                                                           // info del
                                                                                                           // usuario
                                                                                                           // actual
        String username = authentication.getName(); // Obtiene el username del contexto de seguridad
        UsuarioCompletoResponse response = gestionUsuarioFacade.obtenerUsuarioCompleto(username); // Llama al facade
        if (response == null) { // Si el usuario no existe
            return ResponseEntity.notFound().build(); // Retorna 404
        }
        return ResponseEntity.ok(response); // Retorna la información completa del usuario
    }

    @GetMapping("/perfil") // Endpoint: GET /usuarios/perfil
    public ResponseEntity<PerfilResponse> obtenerPerfil(Authentication authentication) { // Obtiene el perfil del
                                                                                         // usuario actual
        String username = authentication.getName(); // Obtiene el username del contexto de seguridad
        PerfilResponse response = gestionUsuarioFacade.obtenerPerfil(username); // Llama al facade
        if (response == null) { // Si no tiene perfil
            return ResponseEntity.notFound().build(); // Retorna 404
        }
        return ResponseEntity.ok(response); // Retorna los datos del perfil
    }

    @PostMapping("/perfil") // Endpoint: POST /usuarios/perfil
    public ResponseEntity<PerfilResponse> registrarPerfil( // Registra el perfil del usuario
            @RequestBody PerfilRequest request, // Datos del perfil
            Authentication authentication) { // Contexto de seguridad
        String username = authentication.getName(); // Obtiene el username
        PerfilResponse response = gestionUsuarioFacade.registrarPerfil(username, request); // Llama al facade
        return ResponseEntity.ok(response); // Retorna los datos del perfil registrado
    }

    @PutMapping("/perfil") // Endpoint: PUT /usuarios/perfil
    public ResponseEntity<PerfilResponse> actualizarPerfil( // Actualiza el perfil del usuario
            @RequestBody PerfilRequest request, // Nuevos datos del perfil
            Authentication authentication) { // Contexto de seguridad
        String username = authentication.getName(); // Obtiene el username
        PerfilResponse response = gestionUsuarioFacade.actualizarPerfil(username, request); // Llama al facade
        return ResponseEntity.ok(response); // Retorna los datos del perfil actualizado
    }

    @PutMapping("/actualizar") // Endpoint: PUT /usuarios/actualizar
    public ResponseEntity<Usuario> actualizarUsuario( // Actualiza datos del propio usuario
            @RequestBody ActualizarUsuarioRequest request, // Datos a actualizar (email, password)
            Authentication authentication) { // Contexto de seguridad
        String username = authentication.getName(); // Obtiene el username
        Usuario usuario = gestionUsuarioFacade.actualizarUsuario( // Llama al facade
                username, // Pasa el username
                request.getEmail(), // Pasa el nuevo email (puede ser null)
                request.getPassword()); // Pasa la nueva password (puede ser null)
        return ResponseEntity.ok(usuario); // Retorna el usuario actualizado
    }

    @DeleteMapping("/eliminar") // Endpoint: DELETE /usuarios/eliminar
    public ResponseEntity<String> eliminarUsuario(Authentication authentication) { // Elimina la cuenta del propio
                                                                                   // usuario
        String username = authentication.getName(); // Obtiene el username
        gestionUsuarioFacade.eliminarUsuario(username); // Llama al facade para eliminar
        return ResponseEntity.ok("Usuario Eliminado correctamente"); // Retorna mensaje de éxito
    }

    @GetMapping("/lista") // Endpoint: GET /usuarios/lista
    public ResponseEntity<Page<UsuarioListaResponse>> obtenerListaUsuarios( // Lista usuarios paginados
            @RequestParam(defaultValue = "0") int page, // Número de página (0 por defecto)
            @RequestParam(defaultValue = "5") int size) { // Tamaño de página (5 por defecto)
        Pageable pageable = PageRequest.of(page, size); // Crea el objeto de paginación
        Page<UsuarioListaResponse> usuarios = usuarioJpaRepository.findAll(pageable) // Obtiene usuarios paginados
                .map(u -> new UsuarioListaResponse(u.getId(), u.getUsername(), u.getEmail())); // Convierte a DTO
        return ResponseEntity.ok(usuarios); // Retorna la página de usuarios
    }

    @GetMapping("/{username}/id") // Endpoint: GET /usuarios/{username}/id
    public ResponseEntity<?> obtenerIdPorUsername(@PathVariable String username) { // Obtiene el ID de un usuario
        Usuario usuario = usuarioJpaRepository.findByUsername(username).orElse(null); // Busca el usuario por username
        if (usuario == null) { // Si el usuario no existe
            return ResponseEntity.notFound().build(); // Retorna 404
        }
        return ResponseEntity.ok(usuario.getId().intValue()); // Retorna el ID del usuario
    }

    @PostMapping("/{username}/roles/{tipoRol}") // Endpoint: POST /usuarios/{username}/roles/{tipoRol}
    public ResponseEntity<String> asignarRol(@PathVariable String username, @PathVariable String tipoRol, // Asigna un
                                                                                                             // rol
            Authentication authentication) { // Contexto de seguridad (debe ser admin)
        gestionUsuarioFacade.asignarRol(authentication.getName(), username, tipoRol); // Llama al facade para asignar rol
        return ResponseEntity.ok("Rol " + tipoRol + " asignado a " + username + " exitosamente"); // Retorna mensaje
    }

    @DeleteMapping("/{username}/roles/{tipoRol}") // Endpoint: DELETE /usuarios/{username}/roles/{tipoRol}
    public ResponseEntity<String> quitarRol(@PathVariable String username, @PathVariable String tipoRol, // Quita un
                                                                                                            // rol
            Authentication authentication) { // Contexto de seguridad (debe ser admin)
        gestionUsuarioFacade.quitarRol(authentication.getName(), username, tipoRol); // Llama al facade para quitar rol
        return ResponseEntity.ok("Rol " + tipoRol + " removido de " + username + " exitosamente"); // Retorna mensaje
    }

    @GetMapping("/{username}/roles") // Endpoint: GET /usuarios/{username}/roles
    public ResponseEntity<List<String>> obtenerRoles(@PathVariable String username) { // Obtiene los roles de un usuario
        List<String> roles = gestionUsuarioFacade.obtenerRoles(username); // Llama al facade para obtener roles
        return ResponseEntity.ok(roles); // Retorna la lista de roles
    }

    @GetMapping("/roles/lista") // Endpoint: GET /usuarios/roles/lista
    public ResponseEntity<List<String>> listarRolesDisponibles() { // Lista todos los roles disponibles
        List<String> roles = gestionUsuarioFacade.listarRolesDisponibles(); // Llama al facade para obtener roles
        return ResponseEntity.ok(roles); // Retorna la lista de roles
    }

    @PutMapping("/{username}") // Endpoint: PUT /usuarios/{username}
    public ResponseEntity<?> actualizarUsuarioAdmin(@PathVariable String username, // Admin actualiza un usuario
            @RequestBody ActualizarUsuarioAdminRequest request, // Datos a actualizar
            Authentication authentication) { // Contexto de seguridad (debe ser admin)
        try { // Try-catch para manejar excepciones del facade
            UsuarioListaResponse response = gestionUsuarioFacade.actualizarUsuarioAdmin( // Llama al facade
                    authentication.getName(), // Username del admin
                    username, // Username del usuario a editar
                    request.getUsername(), // Nuevo username (puede ser null)
                    request.getEmail(), // Nuevo email (puede ser null)
                    request.getPassword()); // Nueva password (puede ser null)
            return ResponseEntity.ok(response); // Retorna los datos actualizados
        } catch (RuntimeException e) { // Si hay algún error
            if (e.getMessage().contains("editarte a ti mismo")) { // Si el admin se intenta editar a sí mismo
                return ResponseEntity.badRequest().body(e.getMessage()); // Retorna 400 con el mensaje
            }
            if (e.getMessage().contains("Ya existe")) { // Si el username ya existe
                return ResponseEntity.badRequest().body(e.getMessage()); // Retorna 400 con el mensaje
            }
            return ResponseEntity.notFound().build(); // Retorna 404 si el usuario no existe
        }
    }

    @DeleteMapping("/{username}") // Endpoint: DELETE /usuarios/{username}
    public ResponseEntity<?> eliminarUsuarioAdmin(@PathVariable String username, // Admin elimina un usuario
            Authentication authentication) { // Contexto de seguridad
        if (authentication.getName().equals(username)) { // Si el admin intenta eliminarse a sí mismo
            return ResponseEntity.badRequest() // Retorna 400
                    .body("No puedes eliminarte a ti mismo"); // Mensaje de error
        }

        gestionUsuarioFacade.eliminarUsuarioAdmin(authentication.getName(), username); // Llama al facade para eliminar
        return ResponseEntity.ok("Usuario " + username + " eliminado exitosamente"); // Retorna mensaje de éxito
    }

    @GetMapping("/{id}/resumen") // GET /usuarios/{id}/resumen
    public ResponseEntity<?> obtenerResumenUsuario(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioJpaRepository.findById(id);
        
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Usuario usuario = usuarioOpt.get();
        
        Optional<PerfilUsuario> perfilOpt = perfilUsuarioJpaRepository.findByUsuarioId(usuario.getId().intValue());
        
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("id", usuario.getId());
        resumen.put("username", usuario.getUsername());
        resumen.put("email", usuario.getEmail());
        resumen.put("estado", usuario.getEstado());
        resumen.put("fechaCreacion", usuario.getFechaCreacion());
        
        if (perfilOpt.isPresent()) {
            PerfilUsuario perfil = perfilOpt.get();
            resumen.put("nombre", perfil.getNombre());
            resumen.put("apellido", perfil.getApellido());
            resumen.put("telefono", perfil.getTelefono());
        } else {
            resumen.put("nombre", null);
            resumen.put("apellido", null);
            resumen.put("telefono", null);
        }
        
        return ResponseEntity.ok(resumen);
    }

    public static class ActualizarUsuarioAdminRequest { // Clase interna para DTO de actualización por admin
        private String username; // Nuevo username (opcional)
        private String email; // Nuevo email (opcional)
        private String password; // Nueva password (opcional)

        public String getUsername() { // Getter para username
            return username;
        }

        public void setUsername(String username) { // Setter para username
            this.username = username;
        }

        public String getEmail() { // Getter para email
            return email;
        }

        public void setEmail(String email) { // Setter para email
            this.email = email;
        }

        public String getPassword() { // Getter para password
            return password;
        }

        public void setPassword(String password) { // Setter para password
            this.password = password;
        }
    }
}