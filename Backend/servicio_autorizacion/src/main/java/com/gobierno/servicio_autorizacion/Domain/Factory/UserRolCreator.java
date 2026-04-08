package com.gobierno.servicio_autorizacion.Domain.Factory;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_autorizacion.Domain.Model.UserRol;
import com.gobierno.servicio_autorizacion.Domain.Model.Rol;

@Component("USER")
public class UserRolCreator extends AbstractRolCreator {

    @Override
    public Rol crearRol() {
        return new UserRol();
    }
    
}
