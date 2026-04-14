package com.gobierno.servicio_identidad.domain.ports.in;

public interface AutenticadorPorCredencialesPort {
    
    String autenticarPorCredenciales(String username, String password);
}
