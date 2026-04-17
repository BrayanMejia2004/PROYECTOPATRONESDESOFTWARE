package com.gobierno.servicio_autorizacion.domain.entities;

public class AdminRol {  // Clase auxiliar para crear rol ADMIN (Factory Method)

    public static Rol crear() {  // Método estático que crea un rol ADMIN
        return new Rol("ADMIN", "Administrador Con Control Del Sistema");  // Retorna un Rol con nombre ADMIN y descripción
    }
}