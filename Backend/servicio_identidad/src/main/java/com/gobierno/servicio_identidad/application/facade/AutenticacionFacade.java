package com.gobierno.servicio_identidad.application.facade;

import com.gobierno.servicio_identidad.infrastructure.adapter.dto.LoginRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioResponse;

public interface AutenticacionFacade { // Interfaz del Facade para operaciones de autenticación

    String login(LoginRequest request); // Autentica al usuario y retorna un token JWT

    UsuarioResponse registrarUsuario(RegistroUsuarioRequest request); // Registra un nuevo usuario y retorna sus datos

    Object validarToken(String token); // Valida un token JWT y retorna el resultado de la autenticación
}