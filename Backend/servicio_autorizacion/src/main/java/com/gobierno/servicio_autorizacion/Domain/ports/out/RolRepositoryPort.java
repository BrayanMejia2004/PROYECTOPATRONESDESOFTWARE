package com.gobierno.servicio_autorizacion.domain.ports.out;

import java.util.Optional;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public interface RolRepositoryPort {
    
    Rol guardar(Rol rol);
    
    Optional<Rol> findByNombre(String nombre);
}
