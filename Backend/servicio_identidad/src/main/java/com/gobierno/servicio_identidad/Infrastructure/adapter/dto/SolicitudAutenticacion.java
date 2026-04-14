package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

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

    public boolean esToken() {
        return token != null && !token.isEmpty();
    }

    public boolean esCredencial() {
        return username != null && password != null;
    }
}
