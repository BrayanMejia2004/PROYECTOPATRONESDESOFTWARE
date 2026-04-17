package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class PerfilResponse {  // DTO de respuesta para datos del perfil

    private String nombre;  // Nombre del usuario
    private String apellido;  // Apellido del usuario
    private String telefono;  // Teléfono de contacto
    private String email;  // Correo electrónico del usuario

    public PerfilResponse() {
    }  // Constructor vacío para deserialización JSON

    public PerfilResponse(String nombre, String apellido, String telefono, String email) {  // Constructor con parámetros
        this.nombre = nombre;  // Asigna el nombre
        this.apellido = apellido;  // Asigna el apellido
        this.telefono = telefono;  // Asigna el teléfono
        this.email = email;  // Asigna el email
    }

    public String getNombre() {  // Getter para obtener el nombre
        return nombre;
    }

    public String getApellido() {  // Getter para obtener el apellido
        return apellido;
    }

    public String getTelefono() {  // Getter para obtener el teléfono
        return telefono;
    }

    public String getEmail() {  // Getter para obtener el email
        return email;
    }
}