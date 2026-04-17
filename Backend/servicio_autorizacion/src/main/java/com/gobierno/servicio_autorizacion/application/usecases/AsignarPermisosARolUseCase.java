package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.PermisoRepositoryPort;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolesPermisosPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Service
public class AsignarPermisosARolUseCase { // Caso de uso para asignar permisos a un rol

    private final PermisoRepositoryPort permisoRepositoryPort; // Puerto de repositorio de permisos
    private final RolesPermisosPort rolesPermisosPort; // Puerto de gestión de roles-permisos
    private final RolJpaRepository rolJpaRepository; // Repositorio JPA de roles

    public AsignarPermisosARolUseCase(PermisoRepositoryPort permisoRepositoryPort,
            RolesPermisosPort rolesPermisosPort,
            RolJpaRepository rolJpaRepository) {
        this.permisoRepositoryPort = permisoRepositoryPort;
        this.rolesPermisosPort = rolesPermisosPort;
        this.rolJpaRepository = rolJpaRepository;
    }

    public void ejecutar(String nombreRol, List<String> nombresPermisos) { // Método para asignar permisos
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase()) // Busca el rol por nombre
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol));

        rolesPermisosPort.eliminarPorRol(rol); // Limpia los permisos existentes del rol

        for (String nombrePermiso : nombresPermisos) { // Itera sobre los permisos a asignar
            Permiso permiso = permisoRepositoryPort.buscarPorNombre(nombrePermiso) // Busca el permiso
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Permiso no encontrado: " + nombrePermiso)); // Lanza excepción si no existe
            rolesPermisosPort.asignarPermiso(rol, permiso); // Asigna el permiso al rol
        }
    }

    public List<Permiso> obtenerPermisosDeRol(String nombreRol) { // Método para obtener permisos de un rol
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase()) // Busca el rol por nombre
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol)); // Lanza excepción
                                                                                                     // si no existe
        return rolesPermisosPort.listarPorRol(rol); // Lista los permisos del rol
    }
}