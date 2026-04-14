package com.gobierno.servicio_identidad.application.usecases;

import java.sql.Timestamp;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;

@Service
public class RegistroUsuarioUseCase {

    private final UsuarioRepositorioPort usuarioRepositorioPort;
    private final PasswordEncoder passwordEncoder;

    public RegistroUsuarioUseCase(UsuarioRepositorioPort usuarioRepositorioPort, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorioPort = usuarioRepositorioPort;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario ejecutar(String username, String password, String email) {
        if (usuarioRepositorioPort.existePorUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        if (usuarioRepositorioPort.existePorEmail(email)) {
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

        return usuarioRepositorioPort.guardar(nuevoUsuario);
    }
}
