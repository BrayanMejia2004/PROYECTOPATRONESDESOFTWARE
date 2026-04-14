package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;

@Service
public class ActualizarUsuarioUseCase {

    private final UsuarioRepositorioPort usuarioRepositorioPort;
    private final PasswordEncoder passwordEncoder;

    public ActualizarUsuarioUseCase(UsuarioRepositorioPort usuarioRepositorioPort,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepositorioPort = usuarioRepositorioPort;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario ejecutar(String username, String nuevoEmail, String nuevaPassword) {

        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nuevoEmail != null && !nuevoEmail.isEmpty()) {
            usuario.actualizarEmail(nuevoEmail);
        }

        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
            usuario.actualizarPassword(passwordEncriptada);
        }

        return usuarioRepositorioPort.guardar(usuario);
    }
}
