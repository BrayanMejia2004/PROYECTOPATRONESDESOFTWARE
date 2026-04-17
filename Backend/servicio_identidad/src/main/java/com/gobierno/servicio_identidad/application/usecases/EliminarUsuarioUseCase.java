package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;

@Service
public class EliminarUsuarioUseCase { // Caso de uso para eliminar un usuario

    private final UsuarioRepositorioPort usuarioRepositorioPort; // Puerto de repositorio de usuarios

    public EliminarUsuarioUseCase(UsuarioRepositorioPort usuarioRepositorioPort) { // Constructor
        this.usuarioRepositorioPort = usuarioRepositorioPort; // Asigna el repositorio
    }

    @Transactional // Marca el método como transaccional (rollback automático si falla)
    public void ejecutar(String username) { // Método principal para eliminar usuario

        if (!usuarioRepositorioPort.existePorUsername(username)) { // Si el usuario no existe
            throw new RuntimeException("Usuario no encontrado"); // Lanza excepción
        }

        usuarioRepositorioPort.eliminarPorUsername(username); // Elimina el usuario de la base de datos
    }
}