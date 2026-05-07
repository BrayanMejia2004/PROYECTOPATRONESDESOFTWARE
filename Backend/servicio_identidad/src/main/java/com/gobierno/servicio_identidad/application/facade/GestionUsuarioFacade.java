package com.gobierno.servicio_identidad.application.facade;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioCompletoResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioListaResponse;

import java.util.List;

public interface GestionUsuarioFacade { // Interfaz del Facade para gestión de usuarios y perfiles

    UsuarioCompletoResponse obtenerUsuarioCompleto(String username); // Obtiene información completa del usuario (perfil
                                                                     // + roles)

    PerfilResponse obtenerPerfil(String username); // Obtiene el perfil de un usuario

    PerfilResponse registrarPerfil(String username, PerfilRequest request); // Registra un nuevo perfil para el usuario

    PerfilResponse actualizarPerfil(String username, PerfilRequest request); // Actualiza el perfil de un usuario

    Usuario actualizarUsuario(String username, String email, String password); // Actualiza datos del usuario (email o
                                                                               // password)

    void eliminarUsuario(String username); // Elimina la cuenta del usuario (por el propio usuario)

    void eliminarUsuarioAdmin(String adminUsername, String username); // Admin elimina un usuario

    void asignarRol(String adminUsername, String username, String tipoRol); // Admin asigna un rol a un usuario

    void quitarRol(String adminUsername, String username, String tipoRol); // Admin quita un rol a un usuario

    List<String> obtenerRoles(String username); // Obtiene los roles de un usuario

    List<String> listarRolesDisponibles(); // Lista todos los roles disponibles en el sistema

    UsuarioListaResponse actualizarUsuarioAdmin(String adminUsername, String username, // Admin actualiza datos de otro
                                                                                       // usuario
            String nuevoUsername, String email, String password);
}