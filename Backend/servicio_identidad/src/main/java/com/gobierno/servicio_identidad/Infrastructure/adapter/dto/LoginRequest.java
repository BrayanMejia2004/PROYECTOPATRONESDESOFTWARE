package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class LoginRequest {  // DTO para solicitar login de usuario

    private String username;  // Nombre de usuario para autenticación
    private String password;  // Contraseña del usuario (sin encriptar)

    public String getUsername() {  // Getter para obtener el username
        return username;
    }

    public String getPassword() {  // Getter para obtener el password
        return password;
    }

    public void setUsername(String username) {  // Setter para asignar el username
        this.username = username;
    }

    public void setPassword(String password) {  // Setter para asignar el password
        this.password = password;
    }
}