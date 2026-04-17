package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

public class PerfilRequest {  // DTO para crear o actualizar el perfil de usuario

    private String nombre;  // Nombre del usuario
    private String apellido;  // Apellido del usuario
    private String telefono;  // Teléfono de contacto del usuario

    public PerfilRequest() {
    }  // Constructor vacío para deserialización JSON

    public PerfilRequest(String nombre, String apellido, String telefono) {  // Constructor con parámetros
        this.nombre = nombre;  // Asigna el nombre
        this.apellido = apellido;  // Asigna el apellido
        this.telefono = telefono;  // Asigna el teléfono
    }

    public String getNombre() {  // Getter para obtener el nombre
        return nombre;
    }

    public void setNombre(String nombre) {  // Setter para asignar el nombre
        this.nombre = nombre;
    }

    public String getApellido() {  // Getter para obtener el apellido
        return apellido;
    }

    public void setApellido(String apellido) {  // Setter para asignar el apellido
        this.apellido = apellido;
    }

    public String getTelefono() {  // Getter para obtener el teléfono
        return telefono;
    }

    public void setTelefono(String telefono) {  // Setter para asignar el teléfono
        this.telefono = telefono;
    }
}