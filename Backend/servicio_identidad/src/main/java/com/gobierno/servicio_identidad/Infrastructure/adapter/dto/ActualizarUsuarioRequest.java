package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class ActualizarUsuarioRequest {  // DTO para actualizar datos del usuario

    private String email;  // Nuevo correo electrónico (opcional)
    private String password;  // Nueva contraseña (opcional)

    public String getEmail() {  // Getter para email
        return email;
    }

    public String getPassword() {  // Getter para password
        return password;
    }
}