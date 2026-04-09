package com.gobierno.servicio_identidad.Ports.Output;

public interface AutenticadorPorCredenciales {
    
    String autenticarPorCredenciales(String username, String password);
}
