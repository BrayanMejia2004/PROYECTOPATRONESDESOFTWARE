package com.gobierno.servicio_autorizacion.domain.factory;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.AdminRol;

@Component("ADMIN")
public class AdminRolCreator extends AbstractRolCreator {

    @Override
    public Rol crearRol() {
        return AdminRol.crear();
    }
    
}
