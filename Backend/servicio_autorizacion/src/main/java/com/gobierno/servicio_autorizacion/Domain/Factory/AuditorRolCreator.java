package com.gobierno.servicio_autorizacion.domain.factory;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.AuditorRol;

@Component("AUDITOR")
public class AuditorRolCreator extends AbstractRolCreator { // Concrete Creator para rol AUDITOR

    @Override // Sobrescribe el método de la clase abstracta
    public Rol crearRol() { // Crea un rol de tipo AUDITOR
        return AuditorRol.crear(); // Delega la creación a la entidad AuditorRol
    }

}