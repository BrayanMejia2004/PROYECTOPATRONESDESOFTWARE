package com.gobierno.servicio_autorizacion.domain.ports.out;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public interface RolRepositoryPort {
    
    Rol guardar(Rol rol);
}
