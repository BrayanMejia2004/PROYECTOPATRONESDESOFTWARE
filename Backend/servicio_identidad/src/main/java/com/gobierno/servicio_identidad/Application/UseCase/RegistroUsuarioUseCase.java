package com.gobierno.servicio_identidad.Application.UseCase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

// Caso de uso para el registro de un nuevo usuario
@Service
public class RegistroUsuarioUseCase {

    // Repositorio de usuarios para la persistencia de datos
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public RegistroUsuarioUseCase(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    // Método para ejecutar el caso de uso de registro de usuario
    public Usuario ejecutar (String username, String password, String email) {
        
        // Validar que el nombre de usuario y el correo electrónico no existan previamente
        if (usuarioRepositorio.existePorUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        if (usuarioRepositorio.existePorEmail(email)) {
            throw new IllegalArgumentException("El correo electrónico ya existe");
        }

        // Encriptar la contraseña antes de guardar el usuario
        String passwordEncriptado = passwordEncoder.encode(password);

        // Crear un nuevo usuario y guardarlo en el repositorio
        Usuario nuevoUsuario = new Usuario(username, passwordEncriptado, email, null, null);
        return usuarioRepositorio.guardar(nuevoUsuario);
    }
}
