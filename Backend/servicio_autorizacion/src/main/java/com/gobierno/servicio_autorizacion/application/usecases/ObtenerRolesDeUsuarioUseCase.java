package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gobierno.servicio_autorizacion.domain.ports.out.UsuariosRolesPort;

@Service
public class ObtenerRolesDeUsuarioUseCase { // Caso de uso para obtener los roles de un usuario

    private final UsuariosRolesPort usuariosRolesPort; // Puerto de gestión de usuarios-roles

    public ObtenerRolesDeUsuarioUseCase(UsuariosRolesPort usuariosRolesPort) {
        this.usuariosRolesPort = usuariosRolesPort;
    }

    public List<String> ejecutar(String username) { // Método principal
        return usuariosRolesPort.listarPorUsername(username); // Delega al puerto
    }
}