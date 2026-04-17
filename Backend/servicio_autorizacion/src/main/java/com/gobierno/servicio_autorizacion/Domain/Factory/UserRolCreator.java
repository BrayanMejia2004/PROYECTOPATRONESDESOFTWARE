package com.gobierno.servicio_autorizacion.domain.factory;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.UserRol;

@Component("USER")
public class UserRolCreator extends AbstractRolCreator { // Concrete Creator para rol USER

    @Override // Sobrescribe el método de la clase abstracta
    public Rol crearRol() { // Crea un rol de tipo USER
        return UserRol.crear(); // Delega la creación a la entidad UserRol
    }

}