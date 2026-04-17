package com.gobierno.servicio_identidad.application.facade;

import com.gobierno.servicio_identidad.infrastructure.adapter.dto.LoginRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioResponse;

public interface AutenticacionFacade {
    
    String login(LoginRequest request);
    
    UsuarioResponse registrarUsuario(RegistroUsuarioRequest request);
    
    Object validarToken(String token);
}
