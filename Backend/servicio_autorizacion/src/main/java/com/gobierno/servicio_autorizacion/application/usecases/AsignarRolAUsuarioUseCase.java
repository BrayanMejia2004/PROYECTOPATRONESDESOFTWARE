package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.UsuariosRolesPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Service
public class AsignarRolAUsuarioUseCase { // Caso de uso para asignar un rol a un usuario

    private final UsuariosRolesPort usuariosRolesPort; // Puerto de gestión de usuarios-roles
    private final RolJpaRepository rolJpaRepository; // Repositorio JPA de roles

    public AsignarRolAUsuarioUseCase(UsuariosRolesPort usuariosRolesPort, // Constructor con inyección
            RolJpaRepository rolJpaRepository) { // Constructor con inyección
        this.usuariosRolesPort = usuariosRolesPort; // Asigna el puerto de usuarios-roles
        this.rolJpaRepository = rolJpaRepository; // Asigna el repositorio de roles
    }

    public void ejecutar(String username, String nombreRol) { // Método para asignar un rol
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase()) // Busca el rol por nombre
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol)); // Lanza excepción
                                                                                                     // si no existe
        usuariosRolesPort.asignarRol(username, rol); // Asigna el rol al usuario
    }

    public List<String> obtenerRolesDeUsuario(String username) { // Método para obtener roles de un usuario
        return usuariosRolesPort.listarPorUsername(username); // Delega al puerto para listar roles
    }
}