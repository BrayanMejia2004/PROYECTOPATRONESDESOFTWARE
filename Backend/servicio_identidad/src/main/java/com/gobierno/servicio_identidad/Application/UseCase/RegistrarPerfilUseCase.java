package com.gobierno.servicio_identidad.Application.UseCase;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.Domain.Model.PerfilUsuario;
import com.gobierno.servicio_identidad.Ports.Output.PerfilUsuarioRepository;
import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

@Service
public class RegistrarPerfilUseCase {

    private final PerfilUsuarioRepository perfilRepository;
    private final UsuarioRepositorio usuarioRepository;

    public RegistrarPerfilUseCase(PerfilUsuarioRepository perfilRepository,
            UsuarioRepositorio usuarioRepository) {
        this.perfilRepository = perfilRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PerfilUsuario ejecutar(Integer usuarioId, String nombre, 
            String apellido, String telefono) {

        if (usuarioRepository.buscarPorId(usuarioId.longValue()).isEmpty()) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        if (perfilRepository.existePorUsuarioId(usuarioId)) {
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

        return perfilRepository.guardar(perfil);
    }

    public PerfilUsuario obtenerPerfilPorUsername(String username) {
        return usuarioRepository.buscarPorUsername(username)
                .flatMap(usuario -> perfilRepository.buscarPorUsuarioId(usuario.getId().intValue()))
                .orElse(null);
    }
}
