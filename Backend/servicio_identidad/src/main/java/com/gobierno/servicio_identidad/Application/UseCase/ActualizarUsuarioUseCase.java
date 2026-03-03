package com.gobierno.servicio_identidad.Application.UseCase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

@Service
public class ActualizarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public ActualizarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio,
                                    PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario ejecutar(String username, String nuevoEmail, String nuevaPassword) {

        Usuario usuario = usuarioRepositorio.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nuevoEmail != null && !nuevoEmail.isEmpty()) {
            usuario.actualizarEmail(nuevoEmail);
        }

        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
            usuario.actualizarPassword(passwordEncriptada);
        }

        return usuarioRepositorio.guardar(usuario);
    }
}
