package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class PerfilResponse {

    private Integer id;
    private Integer usuarioId;
    private String nombre;
    private String apellido;
    private String telefono;

    public PerfilResponse() {
    }

    public PerfilResponse(Integer id, Integer usuarioId, String nombre, 
            String apellido, String telefono) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
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
}
