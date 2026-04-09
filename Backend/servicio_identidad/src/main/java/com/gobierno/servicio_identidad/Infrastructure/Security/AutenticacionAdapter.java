package com.gobierno.servicio_identidad.Infrastructure.Security;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_identidad.Infrastructure.Dto.SolicitudAutenticacion;
import com.gobierno.servicio_identidad.Ports.Output.Autenticador;
import com.gobierno.servicio_identidad.Ports.Output.AutenticadorPorCredenciales;

@Component
public class AutenticacionAdapter implements Autenticador {

    private final AutenticadorPorCredenciales autenticadorPorCredenciales;
    private final ValidadorJwt validadorJwt;

    public AutenticacionAdapter(AutenticadorPorCredenciales autenticadorPorCredenciales,
            ValidadorJwt validadorJwt) {
        this.autenticadorPorCredenciales = autenticadorPorCredenciales;
        this.validadorJwt = validadorJwt;
    }

    @Override
    public Object autenticar(SolicitudAutenticacion solicitud) {
        if (solicitud.getUsername() != null && solicitud.getPassword() != null) {
            return autenticadorPorCredenciales.autenticarPorCredenciales(
                    solicitud.getUsername(),
                    solicitud.getPassword());
        }

        if (solicitud.getToken() != null) {
            return validadorJwt.validarToken(solicitud.getToken());
        }

        throw new RuntimeException("Solicitud de autenticación inválida");
    }
}
