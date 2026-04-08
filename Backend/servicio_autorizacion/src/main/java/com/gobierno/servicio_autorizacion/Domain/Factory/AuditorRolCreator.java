package com.gobierno.servicio_autorizacion.Domain.Factory;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_autorizacion.Domain.Model.AuditorRol;
import com.gobierno.servicio_autorizacion.Domain.Model.Rol;

@Component("AUDITOR")
public class AuditorRolCreator extends AbstractRolCreator {

    @Override
    public Rol crearRol() {
        return new AuditorRol();
    }
    
}
