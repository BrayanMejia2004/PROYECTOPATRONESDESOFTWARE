package com.gobierno.servicio_autorizacion.domain.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class AdminRol extends Rol {

    public AdminRol() {
        super("ADMIN", "Administrador Con Control Del Sistema");
    }
}
