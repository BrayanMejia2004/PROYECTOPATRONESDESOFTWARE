package com.gobierno.servicio_autorizacion.Domain.Factory;

import com.gobierno.servicio_autorizacion.Domain.Model.Rol;

// Fábrica de roles para crear instancias de roles específicos
public class RolFactory {

    // Ruta del paquete donde se encuentran las clases de roles específicos
    private static final String PACKAGE = "com.gobierno.servicio_autorizacion.Domain.Model.";

    // Método estático para crear un rol específico basado en el tipo de rol proporcionado
    public static Rol crearRol(String tipoRol) {

        try {

            // Construir el nombre completo de la clase del rol específico
            String nombreClase = PACKAGE + tipoRol;
            Class<?> clase = Class.forName(nombreClase);
            return (Rol) clase.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de rol no reconocido: " + tipoRol);
        }

    }
    
}
