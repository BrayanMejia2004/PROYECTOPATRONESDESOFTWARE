package com.gobierno.servicio_autorizacion.domain.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AUDITOR")
public class AuditorRol extends Rol {

    public AuditorRol() {
        super("AUDITOR", "Encargado De Auditar El Sistema");
    }
    
}
