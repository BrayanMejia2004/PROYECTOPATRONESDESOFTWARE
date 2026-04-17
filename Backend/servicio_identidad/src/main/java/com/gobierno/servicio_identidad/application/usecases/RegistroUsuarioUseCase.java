package com.gobierno.servicio_identidad.application.usecases;

import java.sql.Timestamp;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;

@Service
public class RegistroUsuarioUseCase { // Caso de uso para registrar un nuevo usuario

    private final UsuarioRepositorioPort usuarioRepositorioPort; // Puerto de repositorio de usuarios
    private final PasswordEncoder passwordEncoder; // Encriptador de contraseñas

    public RegistroUsuarioUseCase(UsuarioRepositorioPort usuarioRepositorioPort, PasswordEncoder passwordEncoder) { // Constructor
        this.usuarioRepositorioPort = usuarioRepositorioPort; // Asigna el repositorio
        this.passwordEncoder = passwordEncoder; // Asigna el encriptador
    }

    public Usuario ejecutar(String username, String password, String email) { // Método principal del caso de uso
        if (usuarioRepositorioPort.existePorUsername(username)) { // Si el username ya existe
            throw new IllegalArgumentException("El nombre de usuario ya existe"); // Lanza excepción
        }

        if (usuarioRepositorioPort.existePorEmail(email)) { // Si el email ya existe
            throw new IllegalArgumentException("El correo electrónico ya existe"); // Lanza excepción
        }

        String passwordEncriptado = passwordEncoder.encode(password); // Encripta la contraseña con BCrypt

        Usuario nuevoUsuario = new Usuario.Builder() // Crea el usuario usando el patrón Builder
                .username(username) // Asigna el username
                .password(passwordEncriptado) // Asigna la contraseña encriptada
                .email(email) // Asigna el email
                .estado(true) // Activa el usuario por defecto
                .fechaCreacion(Timestamp.from(java.time.Instant.now())) // Asigna la fecha actual
                .build(); // Construye el objeto Usuario

        return usuarioRepositorioPort.guardar(nuevoUsuario); // Persiste el usuario y lo retorna
    }
}