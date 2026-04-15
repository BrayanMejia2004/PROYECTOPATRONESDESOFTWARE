package com.gobierno.servicio_autorizacion.domain.factory;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.UserRol;

@Component("USER")
public class UserRolCreator extends AbstractRolCreator {

    @Override
    public Rol crearRol() {
        return UserRol.crear();
    }
    
}
