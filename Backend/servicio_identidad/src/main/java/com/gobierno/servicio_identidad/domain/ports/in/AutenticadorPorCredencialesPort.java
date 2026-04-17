package com.gobierno.servicio_identidad.domain.ports.in;

public interface AutenticadorPorCredencialesPort {  // Puerto de entrada para autenticación por credenciales
    
    String autenticarPorCredenciales(String username, String password);  // Método para autenticar usuario con username y password
}