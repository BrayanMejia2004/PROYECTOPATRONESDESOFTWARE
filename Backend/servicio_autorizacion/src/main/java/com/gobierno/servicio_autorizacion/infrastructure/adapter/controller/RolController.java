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
public class RolController {

    private static final List<String> ROLES_FIJOS = Arrays.asList("ADMIN", "USER", "AUDITOR");

    private final CrearRolConPermisosUseCase crearRolConPermisosUseCase;
    private final RolJpaRepository rolJpaRepository;
    private final RolesPermisosPort rolesPermisosPort;
    private final UsuariosRolesPort usuariosRolesPort;
    private final AuditoriaClient auditoriaClient;
    private final IdentidadClient identidadClient;

    public RolController(CrearRolConPermisosUseCase crearRolConPermisosUseCase,
                        RolJpaRepository rolJpaRepository,
                        RolesPermisosPort rolesPermisosPort,
                        UsuariosRolesPort usuariosRolesPort,
                        AuditoriaClient auditoriaClient,
                        IdentidadClient identidadClient) {
        this.crearRolConPermisosUseCase = crearRolConPermisosUseCase;
        this.rolJpaRepository = rolJpaRepository;
        this.rolesPermisosPort = rolesPermisosPort;
        this.usuariosRolesPort = usuariosRolesPort;
        this.auditoriaClient = auditoriaClient;
        this.identidadClient = identidadClient;
    }

    @PostMapping("/crear/{tipoRol}")
    public ResponseEntity<?> crearRol(@PathVariable String tipoRol,
                                     @RequestBody(required = false) RolRequest request,
                                     @RequestHeader(value = "X-Usuario", required = false) String usuarioAdmin) {
        String nombre = tipoRol.toUpperCase();
        String descripcion = (request != null && request.getDescripcion() != null)
                ? request.getDescripcion()
                : "Rol personalizado";

        if (rolJpaRepository.existsByNombre(nombre)) {
            return ResponseEntity.badRequest().body("El rol " + nombre + " ya existe");
        }

        List<String> permisos = (request != null) ? request.getPermisos() : null;
        Rol rol = crearRolConPermisosUseCase.ejecutar(nombre, descripcion, permisos);

        Integer adminId = identidadClient.obtenerIdPorUsername(usuarioAdmin);
        auditoriaClient.registrarAuditoria(
            adminId,
            "CREAR_ROL",
            "Admin " + usuarioAdmin + " cre\u00f3 rol " + nombre,
            "COMPLETA"
        );

        return ResponseEntity.ok(new RolResponse(rol.getId(), rol.getNombre(), rol.getDescripcion()));
    }

    @GetMapping("/lista")
    public ResponseEntity<List<RolResponse>> obtenerListaRoles() {
        List<RolResponse> roles = rolJpaRepository.findAll().stream()
                .map(r -> new RolResponse(r.getId(), r.getNombre(), r.getDescripcion()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{nombreRol}")
    public ResponseEntity<?> actualizarRol(@PathVariable String nombreRol,
                                           @RequestBody RolUpdateRequest request,
                                           @RequestHeader(value = "X-Usuario", required = false) String usuarioAdmin) {
        String nombreActual = nombreRol.toUpperCase();

        if (ROLES_FIJOS.contains(nombreActual)) {
            return ResponseEntity.badRequest()
                    .body("No se puede editar un rol protegido");
        }

        Rol rol = rolJpaRepository.findByNombre(nombreActual)
                .orElse(null);

        if (rol == null) {
            return ResponseEntity.notFound().build();
        }

        String nuevoNombre = request.getNuevoNombre();
        if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
            nuevoNombre = nuevoNombre.toUpperCase();
            if (!nuevoNombre.equals(nombreActual) &&
                rolJpaRepository.existsByNombre(nuevoNombre)) {
                return ResponseEntity.badRequest()
                        .body("Ya existe un rol con el nombre: " + nuevoNombre);
            }
            rol.setNombre(nuevoNombre);
        }

        if (request.getDescripcion() != null) {
            rol.setDescripcion(request.getDescripcion());
        }

        rolJpaRepository.save(rol);

        Integer adminId = identidadClient.obtenerIdPorUsername(usuarioAdmin);
        auditoriaClient.registrarAuditoria(
            adminId,
            "ACTUALIZAR_ROL",
            "Admin " + usuarioAdmin + " actualiz\u00f3 rol " + nombreActual,
            "COMPLETA"
        );

        return ResponseEntity.ok(new RolResponse(rol.getId(), rol.getNombre(), rol.getDescripcion()));
    }

    @DeleteMapping("/{nombreRol}")
    public ResponseEntity<?> eliminarRol(@PathVariable String nombreRol,
                                         @RequestHeader(value = "X-Usuario", required = false) String usuarioAdmin) {
        String nombre = nombreRol.toUpperCase();

        if (ROLES_FIJOS.contains(nombre)) {
            return ResponseEntity.badRequest().body("No se puede eliminar un rol protegido: " + nombre);
        }

        Rol rol = rolJpaRepository.findByNombre(nombre)
                .orElse(null);

        if (rol == null) {
            return ResponseEntity.notFound().build();
        }

        usuariosRolesPort.eliminarPorRol(rol);
        rolesPermisosPort.eliminarPorRol(rol);
        rolJpaRepository.delete(rol);

        Integer adminId = identidadClient.obtenerIdPorUsername(usuarioAdmin);
        auditoriaClient.registrarAuditoria(
            adminId,
            "ELIMINAR_ROL",
            "Admin " + usuarioAdmin + " elimin\u00f3 rol " + nombre,
            "SEGURIDAD"
        );

        return ResponseEntity.ok("Rol " + nombre + " eliminado exitosamente");
    }

    public static class RolRequest {
        private String descripcion;
        private List<String> permisos;

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public List<String> getPermisos() {
            return permisos;
        }

        public void setPermisos(List<String> permisos) {
            this.permisos = permisos;
        }
    }

    public static class RolUpdateRequest {
        private String nuevoNombre;
        private String descripcion;

        public String getNuevoNombre() {
            return nuevoNombre;
        }

        public void setNuevoNombre(String nuevoNombre) {
            this.nuevoNombre = nuevoNombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    }

    public static class RolResponse {
        private Long id;
        private String nombre;
        private String descripcion;

        public RolResponse(Long id, String nombre, String descripcion) {
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
}
