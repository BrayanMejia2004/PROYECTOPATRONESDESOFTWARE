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
public class RolPermisoController {

    private final AsignarPermisosARolUseCase asignarPermisosARolUseCase;

    public RolPermisoController(AsignarPermisosARolUseCase asignarPermisosARolUseCase) {
        this.asignarPermisosARolUseCase = asignarPermisosARolUseCase;
    }

    @GetMapping("/{nombreRol}/permisos")
    public ResponseEntity<List<PermisoResponse>> obtenerPermisosDeRol(@PathVariable String nombreRol) {
        List<Permiso> permisos = asignarPermisosARolUseCase.obtenerPermisosDeRol(nombreRol);
        
        List<PermisoResponse> response = permisos.stream()
                .map(p -> new PermisoResponse(p.getNombre(), p.getDescripcion(), p.getRecurso(), p.getAccion()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{nombreRol}/permisos")
    public ResponseEntity<String> asignarPermisos(@PathVariable String nombreRol,
                                                  @RequestBody PermisosRequest request) {
        asignarPermisosARolUseCase.ejecutar(nombreRol, request.getPermisos());
        return ResponseEntity.ok("Permisos asignados al rol " + nombreRol + " exitosamente");
    }

    public static class PermisosRequest {
        private List<String> permisos;

        public List<String> getPermisos() {
            return permisos;
        }

        public void setPermisos(List<String> permisos) {
            this.permisos = permisos;
        }
    }

    public static class PermisoResponse {
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
