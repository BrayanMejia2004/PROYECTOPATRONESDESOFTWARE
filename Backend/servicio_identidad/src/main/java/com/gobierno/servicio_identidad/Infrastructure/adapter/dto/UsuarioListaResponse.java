package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class UsuarioListaResponse {

    private Long id;
    private String username;
    private String email;

    public UsuarioListaResponse() {
    }

    public UsuarioListaResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
