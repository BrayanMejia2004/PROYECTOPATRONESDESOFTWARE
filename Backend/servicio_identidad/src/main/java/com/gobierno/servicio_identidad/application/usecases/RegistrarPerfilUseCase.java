package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.ports.out.PerfilUsuarioRepositoryPort;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;

@Service
public class RegistrarPerfilUseCase {

    private final PerfilUsuarioRepositoryPort perfilRepositoryPort;
    private final UsuarioRepositorioPort usuarioRepositoryPort;

    public RegistrarPerfilUseCase(PerfilUsuarioRepositoryPort perfilRepositoryPort,
            UsuarioRepositorioPort usuarioRepositoryPort) {
        this.perfilRepositoryPort = perfilRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    public PerfilUsuario ejecutar(Integer usuarioId, String nombre, 
            String apellido, String telefono) {

        if (usuarioRepositoryPort.buscarPorId(usuarioId.longValue()).isEmpty()) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        if (perfilRepositoryPort.existePorUsuarioId(usuarioId)) {
            throw new IllegalArgumentException("El usuario ya tiene un perfil registrado");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }

        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El telefono es obligatorio");
        }

        PerfilUsuario perfil = new PerfilUsuario.Builder()
                .usuarioId(usuarioId)
                .nombre(nombre)
                .apellido(apellido)
                .telefono(telefono)
                .build();

        return perfilRepositoryPort.guardar(perfil);
    }

    public PerfilUsuario obtenerPerfilPorUsername(String username) {
        return usuarioRepositoryPort.buscarPorUsername(username)
                .flatMap(usuario -> perfilRepositoryPort.buscarPorUsuarioId(usuario.getId().intValue()))
                .orElse(null);
    }
}
