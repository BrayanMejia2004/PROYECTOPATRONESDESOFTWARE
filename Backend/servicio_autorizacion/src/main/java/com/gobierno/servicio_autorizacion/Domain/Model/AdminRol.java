package com.gobierno.servicio_autorizacion.Domain.Model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Clase que representa el rol de administrador en el sistema
@Entity
@DiscriminatorValue("ADMIN")
public class AdminRol extends Rol {

    // Constructor que inicializa el nombre y la descripción del rol de administrador
    public AdminRol() {
        super("ADMIN", "Administrador Con Control Del Sistema");
    }
}
