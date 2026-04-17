package com.gobierno.servicio_identidad.infrastructure.configuration;

import java.io.InputStream;
import java.util.Properties;

public enum SeguridadConfig { // Enum singleton para configuración de seguridad (JWT)

    INSTANCE; // Instancia única del enum (singleton)

    private final String jwtSecret; // Clave secreta para firmar tokens JWT
    private final long jwtExpiracion; // Tiempo de expiración de los tokens JWT en milisegundos

    SeguridadConfig() { // Constructor privado del enum
        try { // Try-catch para manejar errores al cargar properties
            Properties properties = new Properties(); // Objeto para leer propiedades

            InputStream input = getClass() // Obtiene el class loader
                    .getClassLoader()
                    .getResourceAsStream("application.properties"); // Lee el archivo de propiedades

            if (input == null) { // Si el archivo no existe
                throw new RuntimeException("No se encontro application.properties"); // Lanza excepción
            }

            properties.load(input); // Carga las propiedades del archivo

            String secret = properties.getProperty("app.jwt.secret"); // Obtiene la clave secreta
            String expiracionStr = properties.getProperty("app.jwt.expiracion"); // Obtiene el tiempo de expiración

            if (secret == null || expiracionStr == null) { // Si alguna propiedad no existe
                throw new RuntimeException( // Lanza excepción
                        "Faltan propiedades app.jwt.secret o app.jwt.expiracion");
            }

            this.jwtSecret = secret; // Asigna la clave secreta
            this.jwtExpiracion = Long.parseLong(expiracionStr); // Convierte la expiración a long

            System.out.println("SeguridadConfig cargado correctamente"); // Mensaje de éxito

        } catch (Exception e) { // Si hay algún error
            throw new RuntimeException( // Lanza excepción fatal
                    "Error al cargar la configuracion de seguridad", e);
        }
    }

    public String getJwtSecret() { // Getter para obtener la clave secreta
        return jwtSecret; // Retorna la clave secreta
    }

    public long getJwtExpiracion() { // Getter para obtener el tiempo de expiración
        return jwtExpiracion; // Retorna la expiración en milisegundos
    }
}