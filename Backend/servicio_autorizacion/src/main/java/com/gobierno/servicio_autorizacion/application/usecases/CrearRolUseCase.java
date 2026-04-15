package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.factory.AbstractRolCreator;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolRepositoryPort;

@Service
public class CrearRolUseCase {

    private final RolRepositoryPort rolRepositoryPort;
    private final Map<String, AbstractRolCreator> creators;

    public CrearRolUseCase(RolRepositoryPort rolRepositoryPort,
            Map<String, AbstractRolCreator> creators) {
        this.rolRepositoryPort = rolRepositoryPort;
        this.creators = creators;
    }

    public Rol ejecutar(String nombreRol, String descripcion) {
        AbstractRolCreator creator = creators.get(nombreRol.toUpperCase());
        
        Rol rol;
        if (creator != null) {
            rol = creator.crearRol();
        } else {
            rol = new Rol(nombreRol.toUpperCase(), descripcion);
        }
        
        return rolRepositoryPort.guardar(rol);
    }
    
}
