package com.gobierno.servicio_autorizacion.domain.entities;

public class AdminRol {

    public static Rol crear() {
        return new Rol("ADMIN", "Administrador Con Control Del Sistema");
    }
}
