package com.gobierno.servicio_identidad.Infrastructure.Dto;

// DTO para la solicitud de actualización de un usuario
public class ActualizarUsuarioRequest {

    private String email;
    private String password;


    //Getters para los campos de correo electrónico y contraseña
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
