package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.in.AutenticadorPorCredencialesPort;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.GeneradorJwtAdapter;

@Service
public class LoginUsuarioUseCase implements AutenticadorPorCredencialesPort { // Caso de uso para login de usuario

    private final PasswordEncoder passwordEncoder; // Encriptador de contraseñas
    private final UsuarioRepositorioPort usuarioRepositorioPort; // Puerto de repositorio de usuarios
    private final GeneradorJwtAdapter generadorJwt; // Generador de tokens JWT

    public LoginUsuarioUseCase(UsuarioRepositorioPort usuarioRepositorioPort, // Constructor con inyección
            PasswordEncoder passwordEncoder, // Constructor con inyección
            GeneradorJwtAdapter generadorJwt) { // Constructor con inyección
        this.usuarioRepositorioPort = usuarioRepositorioPort; // Asigna el repositorio
        this.passwordEncoder = passwordEncoder; // Asigna el encriptador
        this.generadorJwt = generadorJwt; // Asigna el generador de JWT
    }

    public String ejecutar(String username, String password) { // Método principal para login
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username) // Busca el usuario por username
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Lanza excepción si no existe

        if (!passwordEncoder.matches(password, usuario.getPassword())) { // Compara la password ingresada con la
                                                                         // encriptada
            throw new RuntimeException("Contraseña incorrecta"); // Lanza excepción si no coinciden
        }

        if (!usuario.getEstado()) { // Si el usuario está inactivo
            throw new RuntimeException("Usuario inactivo"); // Lanza excepción
        }

        return generadorJwt.generarToken(usuario); // Genera y retorna el token JWT
    }

    @Override // Sobrescribe el método de la interfaz
    public String autenticarPorCredenciales(String username, String password) { // Implementación del puerto
        return ejecutar(username, password); // Delega al método principal
    }
}