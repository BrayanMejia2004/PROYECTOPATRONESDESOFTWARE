package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolesPermisosPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Service
public class ObtenerPermisosDeRolUseCase { // Caso de uso para obtener los permisos de un rol

    private final RolesPermisosPort rolesPermisosPort; // Puerto de gestión de roles-permisos
    private final RolJpaRepository rolJpaRepository; // Repositorio JPA de roles

    public ObtenerPermisosDeRolUseCase(RolesPermisosPort rolesPermisosPort,
            RolJpaRepository rolJpaRepository) {
        this.rolesPermisosPort = rolesPermisosPort;
        this.rolJpaRepository = rolJpaRepository;
    }

    public List<Permiso> ejecutar(String nombreRol) { // Método principal
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase()) // Busca el rol por nombre
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol)); // Lanza excepción
                                                                                                     // si no existe
        return rolesPermisosPort.listarPorRol(rol); // Lista los permisos del rol
    }
}