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
public class PermisoController { // Controlador para gestionar permisos

    private final CrearPermisoUseCase crearPermisoUseCase; // Caso de uso para crear permisos
    private final PermisoRepositoryPort permisoRepositoryPort; // Puerto de repositorio de permisos

    public PermisoController(CrearPermisoUseCase crearPermisoUseCase,
            PermisoRepositoryPort permisoRepositoryPort) {
        this.crearPermisoUseCase = crearPermisoUseCase;
        this.permisoRepositoryPort = permisoRepositoryPort;
    }

    @GetMapping // GET /permisos
    public ResponseEntity<List<Permiso>> listarPermisos() { // Lista todos los permisos
        return ResponseEntity.ok(permisoRepositoryPort.listarTodos());
    }

    @PostMapping // POST /permisos
    public ResponseEntity<Permiso> crearPermiso(@RequestBody PermisoRequest request) { // Crea un nuevo permiso
        Permiso permiso = crearPermisoUseCase.ejecutar( // Ejecuta el caso de uso
                request.getNombre(),
                request.getDescripcion(),
                request.getRecurso(),
                request.getAccion());
        return ResponseEntity.ok(permiso); // Retorna el permiso creado
    }

    public static class PermisoRequest { // Clase interna para solicitud de permiso
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