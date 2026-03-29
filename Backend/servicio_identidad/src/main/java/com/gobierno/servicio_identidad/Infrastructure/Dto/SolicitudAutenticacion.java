package com.gobierno.servicio_identidad.Infrastructure.Dto;

// Representa una solicitud de autenticacion generica
// Puede venir con credenciales o con token.
public class SolicitudAutenticacion {

    private String username;
    private String password;
    private String token;

    public SolicitudAutenticacion(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    // Determinar si es autenticacion por JWT
    public boolean esToken() {
        return token != null && !token.isEmpty();
    }

    // Determinar si es autenticacion por credenciales
    public boolean esCredencial() {
        return username != null && password != null;
    }
}
