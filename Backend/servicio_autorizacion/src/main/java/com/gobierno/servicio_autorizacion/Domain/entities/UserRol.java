package com.gobierno.servicio_autorizacion.domain.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("USER")
public class UserRol extends Rol {

    public UserRol() {
       super("USER", "Usuario Estandar Del Sistema");
    }
     
}
