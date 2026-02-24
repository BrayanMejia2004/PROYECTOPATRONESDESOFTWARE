package com.gobierno.servicio_identidad.Infrastructure.Security;

import com.gobierno.servicio_identidad.Infrastructure.Configuration.SeguridadConfig;

public class JwtService {

    public void generarToken() {

        String secret = SeguridadConfig.INSTANCE.getJwtSecret();
        long expiracion = SeguridadConfig.INSTANCE.getJwtExpiracion();

        System.out.println("Generando token con secret: " + secret);
        System.out.println("Expira en: " + expiracion);
        
        // Aquí iría la lógica real de generación JWT
    }
    
}
