package com.gobierno.servicio_identidad.Application.UseCase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

@Service
public class ActualizarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    // Inyección de dependencias por constructor
    public ActualizarUsuarioUseCase(UsuarioRepositorio usuarioRepositorio,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    // Actualiza email y/o contraseña de un usuario existente
    public Usuario ejecutar(String username, String nuevoEmail, String nuevaPassword) {

        // Busca el usuario o lanza excepción si no existe
        Usuario usuario = usuarioRepositorio.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Solo actualiza el email si viene con valor
        if (nuevoEmail != null && !nuevoEmail.isEmpty()) {
            usuario.actualizarEmail(nuevoEmail);
        }

        // Solo actualiza la contraseña si viene con valor
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
            usuario.actualizarPassword(passwordEncriptada);
        }

        // Persiste los cambios y retorna el usuario actualizado
        return usuarioRepositorio.guardar(usuario);
    }
}
