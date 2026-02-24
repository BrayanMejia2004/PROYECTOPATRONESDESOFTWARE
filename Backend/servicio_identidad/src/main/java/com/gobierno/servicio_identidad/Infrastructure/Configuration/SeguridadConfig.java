package com.gobierno.servicio_identidad.Infrastructure.Configuration;

import java.io.InputStream;
import java.util.Properties;

public enum SeguridadConfig {

    INSTANCE;

    private final String jwtSecret;
    private final long jwtExpiracion;

    SeguridadConfig() {
        try {
            Properties properties = new Properties();

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

            System.out.println("✅ SeguridadConfig cargado correctamente");

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al cargar la configuración de seguridad", e);
        }
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public long getJwtExpiracion() {
        return jwtExpiracion;
    }
}
