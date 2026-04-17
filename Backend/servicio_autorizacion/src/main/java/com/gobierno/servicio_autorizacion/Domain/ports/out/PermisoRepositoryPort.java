package com.gobierno.servicio_autorizacion.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;

public interface PermisoRepositoryPort { // Puerto de salida para repositorio de permisos

    Permiso guardar(Permiso permiso); // Persiste un permiso y lo retorna

    Optional<Permiso> buscarPorId(Long id); // Busca un permiso por su ID

    Optional<Permiso> buscarPorNombre(String nombre); // Busca un permiso por su nombre

    List<Permiso> listarTodos(); // Lista todos los permisos

    boolean existePorNombre(String nombre); // Verifica si existe un permiso por nombre
}