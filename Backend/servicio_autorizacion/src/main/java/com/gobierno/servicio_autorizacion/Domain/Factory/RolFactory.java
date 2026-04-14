package com.gobierno.servicio_autorizacion.domain.factory;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public class RolFactory {

    private static final String PACKAGE = "com.gobierno.servicio_autorizacion.domain.entities.";

    public static Rol crearRol(String tipoRol) {

        try {

            String nombreClase = PACKAGE + tipoRol;
            Class<?> clase = Class.forName(nombreClase);
            return (Rol) clase.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de rol no reconocido: " + tipoRol);
        }

    }
    
}
