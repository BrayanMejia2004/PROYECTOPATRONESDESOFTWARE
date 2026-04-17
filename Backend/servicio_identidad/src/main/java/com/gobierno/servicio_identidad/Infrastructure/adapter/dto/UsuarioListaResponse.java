package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class UsuarioListaResponse {  // DTO para listar usuarios en tablas

    private Long id;  // ID único del usuario
    private String username;  // Nombre de usuario
    private String email;  // Correo electrónico del usuario

    public UsuarioListaResponse() {
    }  // Constructor vacío para deserialización JSON

    public UsuarioListaResponse(Long id, String username, String email) {  // Constructor con parámetros
        this.id = id;  // Asigna el ID
        this.username = username;  // Asigna el username
        this.email = email;  // Asigna el email
    }

    public Long getId() {  // Getter para obtener el ID
        return id;
    }

    public void setId(Long id) {  // Setter para modificar el ID
        this.id = id;
    }

    public String getUsername() {  // Getter para obtener el username
        return username;
    }

    public void setUsername(String username) {  // Setter para modificar el username
        this.username = username;
    }

    public String getEmail() {  // Getter para obtener el email
        return email;
    }

    public void setEmail(String email) {  // Setter para modificar el email
        this.email = email;
    }
}