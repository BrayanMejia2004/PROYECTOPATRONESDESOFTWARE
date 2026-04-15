package com.gobierno.servicio_autorizacion.application.usecases;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.ports.out.PermisoRepositoryPort;

@Service
public class CrearPermisoUseCase {

    private final PermisoRepositoryPort permisoRepositoryPort;

    public CrearPermisoUseCase(PermisoRepositoryPort permisoRepositoryPort) {
        this.permisoRepositoryPort = permisoRepositoryPort;
    }

    public Permiso ejecutar(String nombre, String descripcion, String recurso, String accion) {
        if (permisoRepositoryPort.existePorNombre(nombre)) {
            throw new IllegalArgumentException("El permiso '" + nombre + "' ya existe");
        }

        Permiso permiso = new Permiso(nombre, descripcion, recurso, accion);
        return permisoRepositoryPort.guardar(permiso);
    }
}
