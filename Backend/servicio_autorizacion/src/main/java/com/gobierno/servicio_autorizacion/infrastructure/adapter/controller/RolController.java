package com.gobierno.servicio_autorizacion.infrastructure.adapter.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_autorizacion.application.usecases.CrearRolConPermisosUseCase;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.infrastructure.adapter.client.AuditoriaClient;
import com.gobierno.servicio_autorizacion.infrastructure.adapter.client.IdentidadClient;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolesPermisosPort;
import com.gobierno.servicio_autorizacion.domain.ports.out.UsuariosRolesPort;

@RestController
@RequestMapping("/roles")
public class RolController { // Controlador REST para gestionar roles

    private static final List<String> ROLES_FIJOS = Arrays.asList("ADMIN", "USER", "AUDITOR"); // Roles que no se pueden
                                                                                               // eliminar/editar

    private final CrearRolConPermisosUseCase crearRolConPermisosUseCase; // Caso de uso para crear rol
    private final RolJpaRepository rolJpaRepository; // Repositorio JPA de roles
    private final RolesPermisosPort rolesPermisosPort; // Puerto de roles-permisos
    private final UsuariosRolesPort usuariosRolesPort; // Puerto de usuarios-roles
    private final AuditoriaClient auditoriaClient; // Cliente de auditoría
    private final IdentidadClient identidadClient; // Cliente de identidad

    public RolController(CrearRolConPermisosUseCase crearRolConPermisosUseCase, // Constructor con inyección
            RolJpaRepository rolJpaRepository, // Constructor con inyección
            RolesPermisosPort rolesPermisosPort, // Constructor con inyección
            UsuariosRolesPort usuariosRolesPort, // Constructor con inyección
            AuditoriaClient auditoriaClient, // Constructor con inyección
            IdentidadClient identidadClient) { // Constructor con inyección
        this.crearRolConPermisosUseCase = crearRolConPermisosUseCase; // Asigna el caso de uso
        this.rolJpaRepository = rolJpaRepository; // Asigna el repositorio
        this.rolesPermisosPort = rolesPermisosPort; // Asigna el puerto
        this.usuariosRolesPort = usuariosRolesPort; // Asigna el puerto
        this.auditoriaClient = auditoriaClient; // Asigna el cliente
        this.identidadClient = identidadClient; // Asigna el cliente
    }

    @PostMapping("/crear/{tipoRol}") // Endpoint: POST /roles/crear/{tipoRol}
    public ResponseEntity<?> crearRol(@PathVariable String tipoRol, // Tipo de rol a crear
            @RequestBody(required = false) RolRequest request, // Datos opcionales del rol
            @RequestHeader(value = "X-Usuario", required = false) String usuarioAdmin) { // Username del admin
        String nombre = tipoRol.toUpperCase(); // Convierte el nombre a mayúsculas
        String descripcion = (request != null && request.getDescripcion() != null) // Si hay descripción
                ? request.getDescripcion() // Usa la descripción del request
                : "Rol personalizado"; // Si no, usa una por defecto

        if (rolJpaRepository.existsByNombre(nombre)) { // Si el rol ya existe
            return ResponseEntity.badRequest().body("El rol " + nombre + " ya existe"); // Retorna error 400
        }

        List<String> permisos = (request != null) ? request.getPermisos() : null; // Obtiene los permisos del request
        Rol rol = crearRolConPermisosUseCase.ejecutar(nombre, descripcion, permisos); // Ejecuta el caso de uso

        Integer adminId = identidadClient.obtenerIdPorUsername(usuarioAdmin); // Obtiene el ID del admin
        auditoriaClient.registrarAuditoria( // Registra la auditoría de creación de rol
                adminId, // ID del admin
                "CREAR_ROL", // Acción realizada
                "Admin " + usuarioAdmin + " cre\u00f3 rol " + nombre, // Descripción
                "COMPLETA" // Tipo de auditoría
        );

        return ResponseEntity.ok(new RolResponse(rol.getId(), rol.getNombre(), rol.getDescripcion())); // Retorna el rol
                                                                                                       // creado
    }

    @GetMapping("/lista") // Endpoint: GET /roles/lista
    public ResponseEntity<List<RolResponse>> obtenerListaRoles() { // Lista todos los roles
        List<RolResponse> roles = rolJpaRepository.findAll().stream() // Obtiene todos los roles de la BD
                .map(r -> new RolResponse(r.getId(), r.getNombre(), r.getDescripcion())) // Convierte a DTO
                .collect(Collectors.toList()); // Recolecta en una lista
        return ResponseEntity.ok(roles); // Retorna la lista de roles
    }

    @PutMapping("/{nombreRol}") // Endpoint: PUT /roles/{nombreRol}
    public ResponseEntity<?> actualizarRol(@PathVariable String nombreRol, // Nombre del rol a actualizar
            @RequestBody RolUpdateRequest request, // Datos a actualizar
            @RequestHeader(value = "X-Usuario", required = false) String usuarioAdmin) { // Username del admin
        String nombreActual = nombreRol.toUpperCase(); // Convierte el nombre a mayúsculas

        if (ROLES_FIJOS.contains(nombreActual)) { // Si es un rol protegido
            return ResponseEntity.badRequest() // Retorna error 400
                    .body("No se puede editar un rol protegido"); // Mensaje de error
        }

        Rol rol = rolJpaRepository.findByNombre(nombreActual) // Busca el rol por nombre
                .orElse(null); // Si no existe, retorna null

        if (rol == null) { // Si el rol no existe
            return ResponseEntity.notFound().build(); // Retorna 404
        }

        String nuevoNombre = request.getNuevoNombre(); // Obtiene el nuevo nombre
        if (nuevoNombre != null && !nuevoNombre.isEmpty()) { // Si hay nuevo nombre
            nuevoNombre = nuevoNombre.toUpperCase(); // Convierte a mayúsculas
            if (!nuevoNombre.equals(nombreActual) && // Si el nombre cambió
                    rolJpaRepository.existsByNombre(nuevoNombre)) { // Y ya existe otro rol con ese nombre
                return ResponseEntity.badRequest() // Retorna error 400
                        .body("Ya existe un rol con el nombre: " + nuevoNombre); // Mensaje de error
            }
            rol.setNombre(nuevoNombre); // Actualiza el nombre del rol
        }

        if (request.getDescripcion() != null) { // Si hay nueva descripción
            rol.setDescripcion(request.getDescripcion()); // Actualiza la descripción
        }

        rolJpaRepository.save(rol); // Persiste los cambios

        Integer adminId = identidadClient.obtenerIdPorUsername(usuarioAdmin); // Obtiene el ID del admin
        auditoriaClient.registrarAuditoria( // Registra la auditoría de actualización
                adminId, // ID del admin
                "ACTUALIZAR_ROL", // Acción realizada
                "Admin " + usuarioAdmin + " actualiz\u00f3 rol " + nombreActual, // Descripción
                "COMPLETA" // Tipo de auditoría
        );

        return ResponseEntity.ok(new RolResponse(rol.getId(), rol.getNombre(), rol.getDescripcion())); // Retorna el rol
                                                                                                       // actualizado
    }

    @DeleteMapping("/{nombreRol}") // Endpoint: DELETE /roles/{nombreRol}
    public ResponseEntity<?> eliminarRol(@PathVariable String nombreRol, // Nombre del rol a eliminar
            @RequestHeader(value = "X-Usuario", required = false) String usuarioAdmin) { // Username del admin
        String nombre = nombreRol.toUpperCase(); // Convierte el nombre a mayúsculas

        if (ROLES_FIJOS.contains(nombre)) { // Si es un rol protegido
            return ResponseEntity.badRequest().body("No se puede eliminar un rol protegido: " + nombre); // Retorna
                                                                                                         // error 400
        }

        Rol rol = rolJpaRepository.findByNombre(nombre) // Busca el rol por nombre
                .orElse(null); // Si no existe, retorna null

        if (rol == null) { // Si el rol no existe
            return ResponseEntity.notFound().build(); // Retorna 404
        }

        usuariosRolesPort.eliminarPorRol(rol); // Elimina el rol de todos los usuarios
        rolesPermisosPort.eliminarPorRol(rol); // Elimina los permisos del rol
        rolJpaRepository.delete(rol); // Elimina el rol de la BD

        Integer adminId = identidadClient.obtenerIdPorUsername(usuarioAdmin); // Obtiene el ID del admin
        auditoriaClient.registrarAuditoria( // Registra la auditoría de eliminación
                adminId, // ID del admin
                "ELIMINAR_ROL", // Acción realizada
                "Admin " + usuarioAdmin + " elimin\u00f3 rol " + nombre, // Descripción
                "SEGURIDAD" // Tipo de auditoría
        );

        return ResponseEntity.ok("Rol " + nombre + " eliminado exitosamente"); // Retorna mensaje de éxito
    }

    public static class RolRequest { // Clase interna para solicitud de creación de rol
        private String descripcion; // Descripción del rol
        private List<String> permisos; // Lista de permisos del rol

        public String getDescripcion() { // Getter para descripción
            return descripcion;
        }

        public void setDescripcion(String descripcion) { // Setter para descripción
            this.descripcion = descripcion;
        }

        public List<String> getPermisos() { // Getter para permisos
            return permisos;
        }

        public void setPermisos(List<String> permisos) { // Setter para permisos
            this.permisos = permisos;
        }
    }

    public static class RolUpdateRequest { // Clase interna para solicitud de actualización de rol
        private String nuevoNombre; // Nuevo nombre del rol
        private String descripcion; // Nueva descripción del rol

        public String getNuevoNombre() { // Getter para nuevo nombre
            return nuevoNombre;
        }

        public void setNuevoNombre(String nuevoNombre) { // Setter para nuevo nombre
            this.nuevoNombre = nuevoNombre;
        }

        public String getDescripcion() { // Getter para descripción
            return descripcion;
        }

        public void setDescripcion(String descripcion) { // Setter para descripción
            this.descripcion = descripcion;
        }
    }

    public static class RolResponse { // Clase interna para respuesta de rol
        private Long id; // ID del rol
        private String nombre; // Nombre del rol
        private String descripcion; // Descripción del rol

        public RolResponse(Long id, String nombre, String descripcion) { // Constructor con parámetros
            this.id = id; // Asigna el ID
            this.nombre = nombre; // Asigna el nombre
            this.descripcion = descripcion; // Asigna la descripción
        }

        public Long getId() { // Getter para ID
            return id;
        }

        public String getNombre() { // Getter para nombre
            return nombre;
        }

        public String getDescripcion() { // Getter para descripción
            return descripcion;
        }
    }
}