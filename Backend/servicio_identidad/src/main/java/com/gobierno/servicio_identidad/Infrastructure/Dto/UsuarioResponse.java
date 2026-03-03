package com.gobierno.servicio_identidad.Infrastructure.Dto;

// DTO para la respuesta de usuario
public class UsuarioResponse {

    private Long id;
    private String username;
    private String email;

    // Constructor
    public UsuarioResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
    
}
