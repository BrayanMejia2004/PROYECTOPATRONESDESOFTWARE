package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class UsuarioResponse {  // DTO de respuesta para datos de usuario

    private Long id;  // ID único del usuario
    private String username;  // Nombre de usuario
    private String email;  // Correo electrónico del usuario

    public UsuarioResponse(Long id, String username, String email) {  // Constructor con parámetros
        this.id = id;  // Asigna el ID
        this.username = username;  // Asigna el username
        this.email = email;  // Asigna el email
    }

    public Long getId() {  // Getter para obtener el ID
        return id;
    }

    public String getUsername() {  // Getter para obtener el username
        return username;
    }

    public String getEmail() {  // Getter para obtener el email
        return email;
    }
}