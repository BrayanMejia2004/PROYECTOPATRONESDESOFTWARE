package com.gobierno.servicio_autorizacion.application.usecases;

import org.springframework.stereotype.Service;
import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.ports.out.PermisoRepositoryPort;

@Service
public class CrearPermisoUseCase { // Caso de uso para crear un nuevo permiso

    private final PermisoRepositoryPort permisoRepositoryPort; // Puerto de repositorio de permisos

    public CrearPermisoUseCase(PermisoRepositoryPort permisoRepositoryPort) {
        this.permisoRepositoryPort = permisoRepositoryPort;
    }

    public Permiso ejecutar(String nombre, String descripcion, String recurso, String accion) { // Método principal
        if (permisoRepositoryPort.existePorNombre(nombre)) { // Si el permiso ya existe
            throw new IllegalArgumentException("El permiso '" + nombre + "' ya existe"); // Lanza excepción
        }

        Permiso permiso = new Permiso(nombre, descripcion, recurso, accion); // Crea el permiso
        return permisoRepositoryPort.guardar(permiso); // Persiste y retorna el permiso
    }
}