package com.gobierno.servicio_identidad.application.facade;

import com.gobierno.servicio_identidad.application.usecases.RegistroUsuarioUseCase;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.AutenticadorPort;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AuditoriaClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.GeneradorJwtAdapter;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.LoginRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.SolicitudAutenticacion;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioResponse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutenticacionFacadeImpl implements AutenticacionFacade {
    
    private final AutenticadorPort autenticadorPort;
    private final UsuarioRepositorioPort usuarioRepositorioPort;
    private final RegistroUsuarioUseCase registroUsuarioUseCase;
    private final AutorizacionClient autorizacionClient;
    private final AuditoriaClient auditoriaClient;
    private final GeneradorJwtAdapter generadorJwtAdapter;
    
    public AutenticacionFacadeImpl(
            AutenticadorPort autenticadorPort,
            UsuarioRepositorioPort usuarioRepositorioPort,
            RegistroUsuarioUseCase registroUsuarioUseCase,
            AutorizacionClient autorizacionClient,
            AuditoriaClient auditoriaClient,
            GeneradorJwtAdapter generadorJwtAdapter) {
        this.autenticadorPort = autenticadorPort;
        this.usuarioRepositorioPort = usuarioRepositorioPort;
        this.registroUsuarioUseCase = registroUsuarioUseCase;
        this.autorizacionClient = autorizacionClient;
        this.auditoriaClient = auditoriaClient;
        this.generadorJwtAdapter = generadorJwtAdapter;
    }
    
    @Override
    public String login(LoginRequest request) {
        SolicitudAutenticacion solicitud = new SolicitudAutenticacion(
            request.getUsername(), 
            request.getPassword(), 
            null
        );
        autenticadorPort.autenticar(solicitud);
        
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<String> roles = autorizacionClient.obtenerRolesDeUsuario(request.getUsername());
        
        if (roles.isEmpty()) {
            autorizacionClient.asignarRolAUsuario(request.getUsername(), "USER");
            roles = autorizacionClient.obtenerRolesDeUsuario(request.getUsername());
        }
        
        String token = generadorJwtAdapter.generarToken(usuario, roles);
        
        auditoriaClient.registrarAuditoria(
            usuario.getId().intValue(),
            "LOGIN",
            "Usuario " + request.getUsername() + " inici\u00f3 sesi\u00f3n",
            "BASICA"
        );
        
        return token;
    }
    
    @Override
    public UsuarioResponse registrarUsuario(RegistroUsuarioRequest request) {
        Usuario nuevoUsuario = registroUsuarioUseCase.ejecutar(
                request.getUsername(),
                request.getPassword(),
                request.getEmail());

        try {
            autorizacionClient.asignarRolAUsuario(request.getUsername(), "USER");
        } catch (Exception e) {
            System.err.println("No se pudo asignar rol USER: " + e.getMessage());
        }

        auditoriaClient.registrarAuditoria(
            nuevoUsuario.getId().intValue(),
            "REGISTRO_USUARIO",
            "Usuario " + request.getUsername() + " se registr\u00f3",
            "BASICA"
        );

        return new UsuarioResponse(
                nuevoUsuario.getId(),
                nuevoUsuario.getUsername(),
                nuevoUsuario.getEmail());
    }
    
    @Override
    public Object validarToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        SolicitudAutenticacion solicitud = new SolicitudAutenticacion(null, null, token);
        return autenticadorPort.autenticar(solicitud);
    }
}
