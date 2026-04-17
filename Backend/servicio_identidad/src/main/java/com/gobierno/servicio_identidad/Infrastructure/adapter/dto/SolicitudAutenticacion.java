package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class SolicitudAutenticacion {  // DTO para solicitud de autenticación (credenciales o token)

    private String username;  // Nombre de usuario (para autenticación por credenciales)
    private String password;  // Contraseña del usuario (para autenticación por credenciales)
    private String token;  // Token JWT (para autenticación por token)

    public SolicitudAutenticacion(String username, String password, String token) {  // Constructor con parámetros
        this.username = username;  // Asigna el username
        this.password = password;  // Asigna el password
        this.token = token;  // Asigna el token
    }

    public String getUsername() {  // Getter para username
        return username;
    }

    public String getPassword() {  // Getter para password
        return password;
    }

    public String getToken() {  // Getter para token
        return token;
    }

    public boolean esToken() {  // Método que verifica si la solicitud es por token
        return token != null && !token.isEmpty();  // Retorna true si hay un token válido
    }

    public boolean esCredencial() {  // Método que verifica si la solicitud es por credenciales
        return username != null && password != null;  // Retorna true si hay username y password
    }
}