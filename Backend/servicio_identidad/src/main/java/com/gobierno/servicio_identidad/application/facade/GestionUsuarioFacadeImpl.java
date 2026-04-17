package com.gobierno.servicio_identidad.application.facade;

import com.gobierno.servicio_identidad.application.usecases.ActualizarPerfilUseCase;
import com.gobierno.servicio_identidad.application.usecases.ActualizarUsuarioUseCase;
import com.gobierno.servicio_identidad.application.usecases.EliminarUsuarioUseCase;
import com.gobierno.servicio_identidad.application.usecases.RegistrarPerfilUseCase;
import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AuditoriaClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioCompletoResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioListaResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GestionUsuarioFacadeImpl implements GestionUsuarioFacade {
    
    private final UsuarioRepositorioPort usuarioRepositorioPort;
    private final RegistrarPerfilUseCase registrarPerfilUseCase;
    private final ActualizarPerfilUseCase actualizarPerfilUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final AutorizacionClient autorizacionClient;
    private final AuditoriaClient auditoriaClient;
    private final PasswordEncoder passwordEncoder;
    
    public GestionUsuarioFacadeImpl(
            UsuarioRepositorioPort usuarioRepositorioPort,
            RegistrarPerfilUseCase registrarPerfilUseCase,
            ActualizarPerfilUseCase actualizarPerfilUseCase,
            ActualizarUsuarioUseCase actualizarUsuarioUseCase,
            EliminarUsuarioUseCase eliminarUsuarioUseCase,
            AutorizacionClient autorizacionClient,
            AuditoriaClient auditoriaClient,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepositorioPort = usuarioRepositorioPort;
        this.registrarPerfilUseCase = registrarPerfilUseCase;
        this.actualizarPerfilUseCase = actualizarPerfilUseCase;
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
        this.autorizacionClient = autorizacionClient;
        this.auditoriaClient = auditoriaClient;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UsuarioCompletoResponse obtenerUsuarioCompleto(String username) {
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return null;
        }
        
        List<String> roles = autorizacionClient.obtenerRolesDeUsuario(username);
        PerfilUsuario perfil = registrarPerfilUseCase.obtenerPerfilPorUsername(username);
        
        UsuarioCompletoResponse.PerfilInfo perfilInfo = null;
        if (perfil != null) {
            perfilInfo = new UsuarioCompletoResponse.PerfilInfo(
                    perfil.getNombre(),
                    perfil.getApellido(),
                    perfil.getTelefono(),
                    usuario.getEmail()
            );
        }
        
        return new UsuarioCompletoResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                roles,
                perfilInfo);
    }
    
    @Override
    public PerfilResponse obtenerPerfil(String username) {
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return null;
        }
        
        PerfilUsuario perfil = registrarPerfilUseCase.obtenerPerfilPorUsername(username);
        if (perfil == null) {
            return null;
        }
        
        return new PerfilResponse(
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getTelefono(),
                usuario.getEmail());
    }
    
    @Override
    public PerfilResponse registrarPerfil(String username, PerfilRequest request) {
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        PerfilUsuario perfil = registrarPerfilUseCase.ejecutar(
                usuario.getId().intValue(),
                request.getNombre(),
                request.getApellido(),
                request.getTelefono());
        
        auditoriaClient.registrarAuditoria(
            usuario.getId().intValue(),
            "REGISTRAR_PERFIL",
            "Usuario " + username + " cre\u00f3 su perfil",
            "COMPLETA"
        );

        return new PerfilResponse(
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getTelefono(),
                usuario.getEmail());
    }
    
    @Override
    public PerfilResponse actualizarPerfil(String username, PerfilRequest request) {
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        PerfilUsuario perfil = actualizarPerfilUseCase.ejecutar(
                usuario.getId().intValue(),
                request.getNombre(),
                request.getApellido(),
                request.getTelefono());
        
        auditoriaClient.registrarAuditoria(
            usuario.getId().intValue(),
            "ACTUALIZAR_PERFIL",
            "Usuario " + username + " actualiz\u00f3 su perfil",
            "COMPLETA"
        );

        return new PerfilResponse(
                perfil.getNombre(),
                perfil.getApellido(),
                perfil.getTelefono(),
                usuario.getEmail());
    }
    
    @Override
    public Usuario actualizarUsuario(String username, String email, String password) {
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);

        Usuario usuarioActualizado = actualizarUsuarioUseCase.ejecutar(
                username,
                email,
                password);
        
        auditoriaClient.registrarAuditoria(
            usuario.getId().intValue(),
            "ACTUALIZAR_USUARIO",
            "Usuario " + username + " actualiz\u00f3 sus datos",
            "BASICA"
        );

        return usuarioActualizado;
    }
    
    @Override
    public void eliminarUsuario(String username) {
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario != null) {
            auditoriaClient.registrarAuditoria(
                usuario.getId().intValue(),
                "ELIMINAR_USUARIO",
                "Usuario " + username + " elimin\u00f3 su cuenta",
                "SEGURIDAD"
            );
        }
        
        eliminarUsuarioUseCase.ejecutar(username);
    }
    
    @Override
    public void eliminarUsuarioAdmin(String adminUsername, String username) {
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        Integer usuarioId = (usuario != null) ? usuario.getId().intValue() : 0;

        auditoriaClient.registrarAuditoria(
            usuarioId,
            "ELIMINAR_USUARIO",
            "Admin " + adminUsername + " elimin\u00f3 usuario " + username,
            "SEGURIDAD"
        );

        autorizacionClient.quitarTodosLosRolesDeUsuario(username);
        eliminarUsuarioUseCase.ejecutar(username);
    }
    
    @Override
    public void asignarRol(String username, String tipoRol) {
        autorizacionClient.asignarRolAUsuario(username, tipoRol);
    }
    
    @Override
    public void quitarRol(String username, String tipoRol) {
        autorizacionClient.quitarRolAUsuario(username, tipoRol);
    }
    
    @Override
    public List<String> obtenerRoles(String username) {
        return autorizacionClient.obtenerRolesDeUsuario(username);
    }
    
    @Override
    public List<String> listarRolesDisponibles() {
        return autorizacionClient.obtenerListaRoles();
    }
    
    @Override
    public UsuarioListaResponse actualizarUsuarioAdmin(String adminUsername, String username,
            String nuevoUsername, String email, String password) {
        if (adminUsername.equals(username)) {
            throw new RuntimeException("No puedes editarte a ti mismo");
        }

        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (nuevoUsername != null && !nuevoUsername.isEmpty()) {
            String nombreActualizado = nuevoUsername;
            if (!nombreActualizado.equals(username) && usuarioRepositorioPort.existePorUsername(nombreActualizado)) {
                throw new RuntimeException("Ya existe un usuario con el nombre: " + nombreActualizado);
            }
            usuario.setUsername(nombreActualizado);
        }

        if (email != null) {
            usuario.actualizarEmail(email);
        }

        if (password != null && !password.isEmpty()) {
            usuario.actualizarPassword(passwordEncoder.encode(password));
        }

        usuarioRepositorioPort.guardar(usuario);

        return new UsuarioListaResponse(usuario.getId(), usuario.getUsername(), usuario.getEmail());
    }
}
