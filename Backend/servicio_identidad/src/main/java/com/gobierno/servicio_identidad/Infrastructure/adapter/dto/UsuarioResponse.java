package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class UsuarioResponse {

    private Long id;
    private String username;
    private String email;

    public UsuarioResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

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
