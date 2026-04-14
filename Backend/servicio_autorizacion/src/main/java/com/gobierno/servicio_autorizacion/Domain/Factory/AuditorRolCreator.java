package com.gobierno.servicio_autorizacion.domain.factory;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.AuditorRol;

@Component("AUDITOR")
public class AuditorRolCreator extends AbstractRolCreator {

    @Override
    public Rol crearRol() {
        return new AuditorRol();
    }
    
}
