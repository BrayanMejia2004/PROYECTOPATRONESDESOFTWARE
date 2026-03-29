package com.gobierno.servicio_identidad.Infrastructure.Security;

import com.gobierno.servicio_identidad.Application.UseCase.LoginUsuarioUseCase;
import com.gobierno.servicio_identidad.Infrastructure.Dto.SolicitudAutenticacion;
import com.gobierno.servicio_identidad.Ports.Output.Autenticador;

// Adapter que unifica autenticación por:
// Credenciales (LoginUsuarioUseCase)
// Token JWT (ValidadorJwt)
public class AutenticacionAdapter implements Autenticador {

    private final LoginUsuarioUseCase loginUseCase;

    // Se inyectan las clases existentess
    public AutenticacionAdapter(LoginUsuarioUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;

    }

    @Override
    public Object autenticar(SolicitudAutenticacion solicitud) {

        // Login usuario/password
        if (solicitud.getUsername() != null && solicitud.getPassword() != null) {
            return loginUseCase.ejecutar(
                    solicitud.getUsername(),
                    solicitud.getPassword());
        }

        // Validación de token
        if (solicitud.getToken() != null) {
            return ValidadorJwt.validarToken(solicitud.getToken());
        }

        throw new RuntimeException("Solicitud de autenticación inválida");
    }
}
