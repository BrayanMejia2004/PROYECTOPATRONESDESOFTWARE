package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_identidad.domain.ports.in.AutenticadorPorCredencialesPort;
import com.gobierno.servicio_identidad.domain.ports.out.AutenticadorPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.SolicitudAutenticacion;

@Component
public class AutenticacionAdapter implements AutenticadorPort {

    private final AutenticadorPorCredencialesPort autenticadorPorCredencialesPort;
    private final ValidadorJwtAdapter validadorJwtAdapter;

    public AutenticacionAdapter(AutenticadorPorCredencialesPort autenticadorPorCredencialesPort,
            ValidadorJwtAdapter validadorJwtAdapter) {
        this.autenticadorPorCredencialesPort = autenticadorPorCredencialesPort;
        this.validadorJwtAdapter = validadorJwtAdapter;
    }

    @Override
    public Object autenticar(SolicitudAutenticacion solicitud) {
        if (solicitud.getUsername() != null && solicitud.getPassword() != null) {
            return autenticadorPorCredencialesPort.autenticarPorCredenciales(
                    solicitud.getUsername(),
                    solicitud.getPassword());
        }

        if (solicitud.getToken() != null) {
            return validadorJwtAdapter.validarToken(solicitud.getToken());
        }

        throw new RuntimeException("Solicitud de autenticacion invalida");
    }
}
