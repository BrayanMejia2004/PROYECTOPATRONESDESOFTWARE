package com.gobierno.servicio_autorizacion.domain.entities;

public class UserRol {  // Clase auxiliar para crear rol USER (Factory Method)

    public static Rol crear() {  // Método estático que crea un rol USER
        return new Rol("USER", "Usuario Estandar Del Sistema");  // Retorna un Rol con nombre USER y descripción
    }
}