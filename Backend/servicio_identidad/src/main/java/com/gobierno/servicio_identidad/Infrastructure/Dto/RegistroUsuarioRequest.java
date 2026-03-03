package com.gobierno.servicio_identidad.Infrastructure.Dto;

// Clase que representa la solicitud de registro de usuario
public class RegistroUsuarioRequest {

    // Campos necesarios para el registro de un nuevo usuario
    private String username;
    private String password;
    private String email;

    // Getters y setters para los campos de la solicitud de registro
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
