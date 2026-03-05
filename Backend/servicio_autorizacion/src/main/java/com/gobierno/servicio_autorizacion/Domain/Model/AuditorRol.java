package com.gobierno.servicio_autorizacion.Domain.Model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// Clase que representa el rol de auditor en el sistema
@Entity
@DiscriminatorValue("AUDITOR")
public class AuditorRol extends Rol {

    // Constructor que inicializa el nombre y la descripción del rol de auditor
    public AuditorRol() {
        super("AUDITOR", "Encargado De Auditar El Sistema");
    }
    
}
