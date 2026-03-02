package com.gobierno.servicio_identidad.Application.UseCase;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Infrastructure.Security.GeneradorJWT;
import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

// Use case para el login de un usuario. Valida las credenciales y genera un token JWT si son correctas.
@Service
public class LoginUsuarioUseCase {

    // Inyección de dependencias para el repositorio de usuarios y el codificador de contraseñas.
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepositorio usuarioRepositorio;

    public LoginUsuarioUseCase(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    // Método principal del caso de uso que ejecuta el proceso de login.
    public String ejecutar(String username, String password) {
        
        // Busca el usuario por su nombre de usuario. Si no se encuentra, lanza una excepción.
        Usuario usuario =  usuarioRepositorio.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verifica que la contraseña proporcionada coincida con la almacenada. Si no coincide, lanza una excepción.
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Verifica que el usuario esté activo. Si no lo está, lanza una excepción.
        if(!usuario.getEstado()) {
            throw new RuntimeException("Usuario inactivo");
        }

        // Si las credenciales son correctas y el usuario está activo, genera un token JWT para el usuario y lo devuelve.
        return GeneradorJWT.generarToken(usuario);
    }
}
