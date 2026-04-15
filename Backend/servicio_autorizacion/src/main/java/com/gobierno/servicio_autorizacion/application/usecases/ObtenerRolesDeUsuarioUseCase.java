package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.ports.out.UsuariosRolesPort;

@Service
public class ObtenerRolesDeUsuarioUseCase {

    private final UsuariosRolesPort usuariosRolesPort;

    public ObtenerRolesDeUsuarioUseCase(UsuariosRolesPort usuariosRolesPort) {
        this.usuariosRolesPort = usuariosRolesPort;
    }

    public List<String> ejecutar(String username) {
        return usuariosRolesPort.listarPorUsername(username);
    }
}
