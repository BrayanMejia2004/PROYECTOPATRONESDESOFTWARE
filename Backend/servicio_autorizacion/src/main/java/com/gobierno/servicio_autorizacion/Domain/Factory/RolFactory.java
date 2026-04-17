package com.gobierno.servicio_autorizacion.domain.factory;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public class RolFactory { // Factory para crear roles dinámicamente (Factory Method simple)

    private static final String PACKAGE = "com.gobierno.servicio_autorizacion.domain.entities."; // Paquete donde están
                                                                                                 // las clases de roles

    public static Rol crearRol(String tipoRol) { // Método estático para crear un rol por su tipo
        try { // Try-catch para manejar errores de reflexión
            String nombreClase = PACKAGE + tipoRol; // Construye el nombre completo de la clase
            Class<?> clase = Class.forName(nombreClase); // Carga la clase dinámicamente
            return (Rol) clase.getDeclaredConstructor().newInstance(); // Crea una instancia del rol
        } catch (Exception e) { // Si el tipo de rol no existe
            throw new IllegalArgumentException("Tipo de rol no reconocido: " + tipoRol); // Lanza excepción
        }
    }

}