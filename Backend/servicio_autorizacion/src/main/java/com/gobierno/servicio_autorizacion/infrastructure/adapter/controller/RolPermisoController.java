package com.gobierno.servicio_autorizacion.infrastructure.adapter.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gobierno.servicio_autorizacion.application.usecases.AsignarPermisosARolUseCase;
import com.gobierno.servicio_autorizacion.domain.entities.Permiso;

@RestController
@RequestMapping("/roles")
public class RolPermisoController { // Controlador para gestionar permisos de roles

    private final AsignarPermisosARolUseCase asignarPermisosARolUseCase; // Caso de uso para asignar permisos

    public RolPermisoController(AsignarPermisosARolUseCase asignarPermisosARolUseCase) {
        this.asignarPermisosARolUseCase = asignarPermisosARolUseCase;
    }

    @GetMapping("/{nombreRol}/permisos") // GET /roles/{nombreRol}/permisos
    public ResponseEntity<List<PermisoResponse>> obtenerPermisosDeRol(@PathVariable String nombreRol) { // Obtiene los
                                                                                                        // permisos de
                                                                                                        // un rol
        List<Permiso> permisos = asignarPermisosARolUseCase.obtenerPermisosDeRol(nombreRol); // Ejecuta el caso de uso

        List<PermisoResponse> response = permisos.stream() // Convierte la lista de permisos a DTOs
                .map(p -> new PermisoResponse(p.getNombre(), p.getDescripcion(), p.getRecurso(), p.getAccion()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response); // Retorna la lista de permisos
    }

    @PostMapping("/{nombreRol}/permisos") // POST /roles/{nombreRol}/permisos
    public ResponseEntity<String> asignarPermisos(@PathVariable String nombreRol, // Asigna permisos a un rol
            @RequestBody PermisosRequest request) {
        asignarPermisosARolUseCase.ejecutar(nombreRol, request.getPermisos()); // Ejecuta el caso de uso
        return ResponseEntity.ok("Permisos asignados al rol " + nombreRol + " exitosamente");
    }

    public static class PermisosRequest { // Clase interna para solicitud de permisos
        private List<String> permisos;

        public List<String> getPermisos() {
            return permisos;
        }

        public void setPermisos(List<String> permisos) {
            this.permisos = permisos;
        }
    }

    public static class PermisoResponse { // Clase interna para respuesta de permiso
        private String nombre;
        private String descripcion;
        private String recurso;
        private String accion;

        public PermisoResponse(String nombre, String descripcion, String recurso, String accion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.recurso = recurso;
            this.accion = accion;
        }

        public String getNombre() {
            return nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getRecurso() {
            return recurso;
        }

        public String getAccion() {
            return accion;
        }
    }
}