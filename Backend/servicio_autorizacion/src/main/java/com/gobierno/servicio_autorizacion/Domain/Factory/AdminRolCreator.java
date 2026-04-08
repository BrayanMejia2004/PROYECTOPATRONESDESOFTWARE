package com.gobierno.servicio_autorizacion.Domain.Factory;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_autorizacion.Domain.Model.AdminRol;
import com.gobierno.servicio_autorizacion.Domain.Model.Rol;

@Component("ADMIN")
public class AdminRolCreator extends AbstractRolCreator {

    @Override
    public Rol crearRol() {
        return new AdminRol();
    }
    
}
