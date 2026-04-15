package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.PermisoRepositoryPort;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolesPermisosPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Service
public class AsignarPermisosARolUseCase {

    private final PermisoRepositoryPort permisoRepositoryPort;
    private final RolesPermisosPort rolesPermisosPort;
    private final RolJpaRepository rolJpaRepository;

    public AsignarPermisosARolUseCase(PermisoRepositoryPort permisoRepositoryPort,
                                      RolesPermisosPort rolesPermisosPort,
                                      RolJpaRepository rolJpaRepository) {
        this.permisoRepositoryPort = permisoRepositoryPort;
        this.rolesPermisosPort = rolesPermisosPort;
        this.rolJpaRepository = rolJpaRepository;
    }

    public void ejecutar(String nombreRol, List<String> nombresPermisos) {
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol));
        
        rolesPermisosPort.eliminarPorRol(rol);

        for (String nombrePermiso : nombresPermisos) {
            Permiso permiso = permisoRepositoryPort.buscarPorNombre(nombrePermiso)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Permiso no encontrado: " + nombrePermiso));
            rolesPermisosPort.asignarPermiso(rol, permiso);
        }
    }

    public List<Permiso> obtenerPermisosDeRol(String nombreRol) {
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol));
        return rolesPermisosPort.listarPorRol(rol);
    }
}
