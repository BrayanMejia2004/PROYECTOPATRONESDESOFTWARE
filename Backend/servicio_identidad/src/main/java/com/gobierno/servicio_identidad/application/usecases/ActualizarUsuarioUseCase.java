package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;

@Service
public class ActualizarUsuarioUseCase { // Caso de uso para actualizar datos del usuario

    private final UsuarioRepositorioPort usuarioRepositorioPort; // Puerto de repositorio de usuarios
    private final PasswordEncoder passwordEncoder; // Encriptador de contraseñas

    public ActualizarUsuarioUseCase(UsuarioRepositorioPort usuarioRepositorioPort, // Constructor
            PasswordEncoder passwordEncoder) { // Constructor con inyección de dependencias
        this.usuarioRepositorioPort = usuarioRepositorioPort; // Asigna el repositorio
        this.passwordEncoder = passwordEncoder; // Asigna el encriptador
    }

    public Usuario ejecutar(String username, String nuevoEmail, String nuevaPassword) { // Método principal

        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username) // Busca el usuario por username
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Lanza excepción si no existe

        if (nuevoEmail != null && !nuevoEmail.isEmpty()) { // Si se proporciona un nuevo email
            usuario.actualizarEmail(nuevoEmail); // Actualiza el email del usuario
        }

        if (nuevaPassword != null && !nuevaPassword.isEmpty()) { // Si se proporciona una nueva password
            String passwordEncriptada = passwordEncoder.encode(nuevaPassword); // Encripta la nueva password
            usuario.actualizarPassword(passwordEncriptada); // Actualiza la password del usuario
        }

        return usuarioRepositorioPort.guardar(usuario); // Persiste los cambios y retorna el usuario actualizado
    }
}