package com.gobierno.servicio_identidad.Application.UseCase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

@Service
public class EliminarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;

    // Inyección de dependencia por constructor
    public EliminarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Transactional
    public void ejecutar(String username) {

        // Verifica que el usuario exista antes de intentar eliminarlo
        if (!usuarioRepositorio.existePorUsername(username)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Elimina el usuario de la base de datos
        usuarioRepositorio.eliminarPorUsername(username);
    }
    
}
