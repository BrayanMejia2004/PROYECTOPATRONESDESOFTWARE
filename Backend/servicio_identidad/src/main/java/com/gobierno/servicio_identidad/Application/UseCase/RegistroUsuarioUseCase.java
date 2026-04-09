package com.gobierno.servicio_identidad.Application.UseCase;

import java.sql.Timestamp;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

@Service
public class RegistroUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public RegistroUsuarioUseCase(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario ejecutar(String username, String password, String email) {
        if (usuarioRepositorio.existePorUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        if (usuarioRepositorio.existePorEmail(email)) {
            throw new IllegalArgumentException("El correo electrónico ya existe");
        }

        String passwordEncriptado = passwordEncoder.encode(password);

        Usuario nuevoUsuario = new Usuario.Builder()
                .username(username)
                .password(passwordEncriptado)
                .email(email)
                .estado(true)
                .fechaCreacion(Timestamp.from(java.time.Instant.now()))
                .build();

        return usuarioRepositorio.guardar(nuevoUsuario);
    }
}
