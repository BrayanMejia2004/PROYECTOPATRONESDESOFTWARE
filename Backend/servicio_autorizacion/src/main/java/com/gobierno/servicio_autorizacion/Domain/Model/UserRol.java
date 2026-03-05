package com.gobierno.servicio_autorizacion.Domain.Model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Clase que representa el rol de usuario en el sistema
@Entity
@DiscriminatorValue("USER")
public class UserRol extends Rol {

    // Constructor que inicializa el nombre y la descripción del rol de usuario
    public UserRol() {
       super("USER", "Usuario Estandar Del Sistema");
    }
    
}
