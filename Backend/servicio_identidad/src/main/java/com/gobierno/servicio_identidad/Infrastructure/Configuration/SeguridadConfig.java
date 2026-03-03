package com.gobierno.servicio_identidad.Infrastructure.Configuration;

import java.io.InputStream;
import java.util.Properties;

// Configuración de seguridad para la aplicación, cargando las propiedades necesarias para la generación 
// y validación de JWT desde el archivo application.properties.
public enum SeguridadConfig {

    // Singleton para la configuración de seguridad, asegurando que solo exista una instancia de esta clase en toda la aplicación.
    INSTANCE;
    
    // Propiedades para la generación y validación de JWT, incluyendo la clave secreta y el tiempo de expiración.
    private final String jwtSecret;
    private final long jwtExpiracion;

    // Constructor que carga las propiedades de seguridad desde el archivo application.properties, manejando posibles errores de carga.
    SeguridadConfig() {
        try {
            Properties properties = new Properties();

            // Carga el archivo application.properties desde el classpath, asegurándose de que el archivo exista y contenga las propiedades necesarias.
            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("application.properties");

            if (input == null) {
                throw new RuntimeException("No se encontró application.properties");
            }

            properties.load(input);

            String secret = properties.getProperty("app.jwt.secret");
            String expiracionStr = properties.getProperty("app.jwt.expiracion");

            if (secret == null || expiracionStr == null) {
                throw new RuntimeException(
                        "Faltan propiedades app.jwt.secret o app.jwt.expiracion");
            }

            this.jwtSecret = secret;
            this.jwtExpiracion = Long.parseLong(expiracionStr);

            System.out.println("SeguridadConfig cargado correctamente");

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al cargar la configuración de seguridad", e);
        }
    }

    // Getters para las propiedades de seguridad, permitiendo que otras partes de la aplicación accedan al secreto y tiempo de expiración de JWT.
    public String getJwtSecret() {
        return jwtSecret;
    }

    public long getJwtExpiracion() {
        return jwtExpiracion;
    }
}
