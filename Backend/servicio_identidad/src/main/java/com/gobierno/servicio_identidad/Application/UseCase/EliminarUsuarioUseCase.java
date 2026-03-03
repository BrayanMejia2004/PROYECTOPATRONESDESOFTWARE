package com.gobierno.servicio_identidad.Application.UseCase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

@Service
public class EliminarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;

    public EliminarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Transactional
    public void ejecutar(String username) {

        if (!usuarioRepositorio.existePorUsername(username)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        usuarioRepositorio.eliminarPorUsername(username);
    }
    
}
