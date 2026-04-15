package com.gobierno.servicio_autorizacion.domain.entities;

public class UserRol {

    public static Rol crear() {
        return new Rol("USER", "Usuario Estandar Del Sistema");
    }
}
