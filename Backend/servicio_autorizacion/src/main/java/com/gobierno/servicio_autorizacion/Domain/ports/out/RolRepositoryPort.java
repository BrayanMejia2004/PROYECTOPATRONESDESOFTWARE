package com.gobierno.servicio_autorizacion.domain.ports.out;

import java.util.Optional;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public interface RolRepositoryPort { // Puerto de salida para repositorio de roles

    Rol guardar(Rol rol); // Persiste un rol y lo retorna

    Optional<Rol> findByNombre(String nombre); // Busca un rol por su nombre
}