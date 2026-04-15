package com.gobierno.servicio_autorizacion.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;

public interface PermisoRepositoryPort {

    Permiso guardar(Permiso permiso);

    Optional<Permiso> buscarPorId(Long id);

    Optional<Permiso> buscarPorNombre(String nombre);

    List<Permiso> listarTodos();

    boolean existePorNombre(String nombre);
}
