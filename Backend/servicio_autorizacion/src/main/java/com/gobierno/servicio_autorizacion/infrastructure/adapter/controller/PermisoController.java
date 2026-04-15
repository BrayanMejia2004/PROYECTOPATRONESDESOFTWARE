package com.gobierno.servicio_autorizacion.infrastructure.adapter.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_autorizacion.application.usecases.CrearPermisoUseCase;
import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.ports.out.PermisoRepositoryPort;

@RestController
@RequestMapping("/permisos")
public class PermisoController {

    private final CrearPermisoUseCase crearPermisoUseCase;
    private final PermisoRepositoryPort permisoRepositoryPort;

    public PermisoController(CrearPermisoUseCase crearPermisoUseCase,
                            PermisoRepositoryPort permisoRepositoryPort) {
        this.crearPermisoUseCase = crearPermisoUseCase;
        this.permisoRepositoryPort = permisoRepositoryPort;
    }

    @GetMapping
    public ResponseEntity<List<Permiso>> listarPermisos() {
        return ResponseEntity.ok(permisoRepositoryPort.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Permiso> crearPermiso(@RequestBody PermisoRequest request) {
        Permiso permiso = crearPermisoUseCase.ejecutar(
                request.getNombre(),
                request.getDescripcion(),
                request.getRecurso(),
                request.getAccion()
        );
        return ResponseEntity.ok(permiso);
    }

    public static class PermisoRequest {
        private String nombre;
        private String descripcion;
        private String recurso;
        private String accion;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getRecurso() {
            return recurso;
        }

        public void setRecurso(String recurso) {
            this.recurso = recurso;
        }

        public String getAccion() {
            return accion;
        }

        public void setAccion(String accion) {
            this.accion = accion;
        }
    }
}
