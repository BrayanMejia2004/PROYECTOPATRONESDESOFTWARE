package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class RegistroUsuarioRequest {  // DTO para registrar un nuevo usuario

    private String username;  // Nombre de usuario único
    private String password;  // Contraseña del usuario (sin encriptar)
    private String email;  // Correo electrónico único del usuario

    public String getUsername() {  // Getter para obtener el username
        return username;
    }

    public void setUsername(String username) {  // Setter para asignar el username
        this.username = username;
    }

    public String getEmail() {  // Getter para obtener el email
        return email;
    }

    public void setEmail(String email) {  // Setter para asignar el email
        this.email = email;
    }

    public String getPassword() {  // Getter para obtener el password
        return password;
    }

    public void setPassword(String password) {  // Setter para asignar el password
        this.password = password;
    }
}