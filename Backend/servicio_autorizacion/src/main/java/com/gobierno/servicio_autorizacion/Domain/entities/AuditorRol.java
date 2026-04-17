package com.gobierno.servicio_autorizacion.domain.entities;

public class AuditorRol {  // Clase auxiliar para crear rol AUDITOR (Factory Method)

    public static Rol crear() {  // Método estático que crea un rol AUDITOR
        return new Rol("AUDITOR", "Encargado De Auditar El Sistema");  // Retorna un Rol con nombre AUDITOR y descripción
    }
}