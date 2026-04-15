package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class PerfilResponse {

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;

    public PerfilResponse() {
    }

    public PerfilResponse(String nombre, String apellido, String telefono, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}
