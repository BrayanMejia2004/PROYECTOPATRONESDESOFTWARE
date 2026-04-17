package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import org.springframework.stereotype.Component;

import com.gobierno.servicio_identidad.domain.ports.in.AutenticadorPorCredencialesPort;
import com.gobierno.servicio_identidad.domain.ports.out.AutenticadorPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.SolicitudAutenticacion;

@Component
public class AutenticacionAdapter implements AutenticadorPort { // Adapter que implementa el puerto de autenticación

    private final AutenticadorPorCredencialesPort autenticadorPorCredencialesPort; // Puerto para autenticar por
                                                                                   // credenciales
    private final ValidadorJwtAdapter validadorJwtAdapter; // Adapter para validar tokens JWT

    public AutenticacionAdapter(AutenticadorPorCredencialesPort autenticadorPorCredencialesPort, // Constructor con
                                                                                                 // inyección
            ValidadorJwtAdapter validadorJwtAdapter) { // Constructor con inyección
        this.autenticadorPorCredencialesPort = autenticadorPorCredencialesPort; // Asigna el autenticador por
                                                                                // credenciales
        this.validadorJwtAdapter = validadorJwtAdapter; // Asigna el validador de JWT
    }

    @Override // Sobrescribe el método de la interfaz
    public Object autenticar(SolicitudAutenticacion solicitud) { // Método principal de autenticación
        if (solicitud.getUsername() != null && solicitud.getPassword() != null) { // Si la solicitud tiene credenciales
            return autenticadorPorCredencialesPort.autenticarPorCredenciales( // Autentica por credenciales
                    solicitud.getUsername(), // Pasa el username
                    solicitud.getPassword()); // Pasa el password
        }

        if (solicitud.getToken() != null) { // Si la solicitud tiene un token
            return validadorJwtAdapter.validarToken(solicitud.getToken()); // Valida el token JWT
        }

        throw new RuntimeException("Solicitud de autenticacion invalida"); // Lanza excepción si no es válida
    }
}