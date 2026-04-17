package com.gobierno.servicio_autorizacion.application.usecases;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.UsuariosRolesPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Service
public class QuitarRolAUsuarioUseCase { // Caso de uso para quitar un rol a un usuario

    private final UsuariosRolesPort usuariosRolesPort; // Puerto de gestión de usuarios-roles
    private final RolJpaRepository rolJpaRepository; // Repositorio JPA de roles

    public QuitarRolAUsuarioUseCase(UsuariosRolesPort usuariosRolesPort, // Constructor con inyección
            RolJpaRepository rolJpaRepository) { // Constructor con inyección
        this.usuariosRolesPort = usuariosRolesPort; // Asigna el puerto de usuarios-roles
        this.rolJpaRepository = rolJpaRepository; // Asigna el repositorio de roles
    }

    public void ejecutar(String username, String nombreRol) { // Método para quitar un rol
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase()) // Busca el rol por nombre
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol)); // Lanza excepción
                                                                                                     // si no existe
        usuariosRolesPort.quitarRol(username, rol); // Quita el rol al usuario
    }
}