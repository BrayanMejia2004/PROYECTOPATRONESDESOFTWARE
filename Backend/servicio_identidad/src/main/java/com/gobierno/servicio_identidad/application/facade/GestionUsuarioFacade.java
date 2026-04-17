package com.gobierno.servicio_identidad.application.facade;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioCompletoResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioListaResponse;

import java.util.List;

public interface GestionUsuarioFacade {
    
    UsuarioCompletoResponse obtenerUsuarioCompleto(String username);
    
    PerfilResponse obtenerPerfil(String username);
    
    PerfilResponse registrarPerfil(String username, PerfilRequest request);
    
    PerfilResponse actualizarPerfil(String username, PerfilRequest request);
    
    Usuario actualizarUsuario(String username, String email, String password);
    
    void eliminarUsuario(String username);
    
    void eliminarUsuarioAdmin(String adminUsername, String username);
    
    void asignarRol(String username, String tipoRol);
    
    void quitarRol(String username, String tipoRol);
    
    List<String> obtenerRoles(String username);
    
    List<String> listarRolesDisponibles();
    
    UsuarioListaResponse actualizarUsuarioAdmin(String adminUsername, String username,
            String nuevoUsername, String email, String password);
}
