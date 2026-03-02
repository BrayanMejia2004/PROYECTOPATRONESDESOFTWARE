package com.gobierno.servicio_identidad.Infrastructure.Controller;


// Clase que representa la solicitud de inicio de sesión, 
// conteniendo el nombre de usuario y la contraseña proporcionados por el usuario.
public class LoginRequest {

    private String username;
    private String password;


    // Getters y setters para los campos de la solicitud de inicio de sesión,
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
