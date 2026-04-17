package com.gobierno.servicio_autorizacion.domain.factory;

import org.springframework.stereotype.Component;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.AdminRol;

@Component("ADMIN")
public class AdminRolCreator extends AbstractRolCreator { // Concrete Creator para rol ADMIN

    @Override // Sobrescribe el método de la clase abstracta
    public Rol crearRol() { // Crea un rol de tipo ADMIN
        return AdminRol.crear(); // Delega la creación a la entidad AdminRol
    }

}