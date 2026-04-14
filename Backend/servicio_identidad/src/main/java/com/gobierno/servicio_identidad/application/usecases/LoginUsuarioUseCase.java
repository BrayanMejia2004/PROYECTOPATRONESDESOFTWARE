package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.in.AutenticadorPorCredencialesPort;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.GeneradorJwtAdapter;

@Service
public class LoginUsuarioUseCase implements AutenticadorPorCredencialesPort {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepositorioPort usuarioRepositorioPort;
    private final GeneradorJwtAdapter generadorJwt;

    public LoginUsuarioUseCase(UsuarioRepositorioPort usuarioRepositorioPort, 
            PasswordEncoder passwordEncoder,
            GeneradorJwtAdapter generadorJwt) {
                
        this.usuarioRepositorioPort = usuarioRepositorioPort;
        this.passwordEncoder = passwordEncoder;
        this.generadorJwt = generadorJwt;
    }

    public String ejecutar(String username, String password) {
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        if (!usuario.getEstado()) {
            throw new RuntimeException("Usuario inactivo");
        }

        return generadorJwt.generarToken(usuario);
    }

    @Override
    public String autenticarPorCredenciales(String username, String password) {
        return ejecutar(username, password);
    }
}
