package com.gobierno.servicio_identidad.Application.UseCase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Infrastructure.Security.GeneradorJWT;
import com.gobierno.servicio_identidad.Ports.Output.AutenticadorPorCredenciales;
import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

@Service
public class LoginUsuarioUseCase implements AutenticadorPorCredenciales {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepositorio usuarioRepositorio;
    private final GeneradorJWT generadorJwt;

    public LoginUsuarioUseCase(UsuarioRepositorio usuarioRepositorio, 
            PasswordEncoder passwordEncoder,
            GeneradorJWT generadorJwt) {
                
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.generadorJwt = generadorJwt;
    }

    public String ejecutar(String username, String password) {
        Usuario usuario = usuarioRepositorio.buscarPorUsername(username)
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
