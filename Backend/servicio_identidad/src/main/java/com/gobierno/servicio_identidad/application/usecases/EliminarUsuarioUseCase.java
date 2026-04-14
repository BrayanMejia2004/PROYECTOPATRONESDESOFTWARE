package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;

@Service
public class EliminarUsuarioUseCase {

    private final UsuarioRepositorioPort usuarioRepositorioPort;

    public EliminarUsuarioUseCase(UsuarioRepositorioPort usuarioRepositorioPort) {
        this.usuarioRepositorioPort = usuarioRepositorioPort;
    }

    @Transactional
    public void ejecutar(String username) {

        if (!usuarioRepositorioPort.existePorUsername(username)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        usuarioRepositorioPort.eliminarPorUsername(username);
    }
}
