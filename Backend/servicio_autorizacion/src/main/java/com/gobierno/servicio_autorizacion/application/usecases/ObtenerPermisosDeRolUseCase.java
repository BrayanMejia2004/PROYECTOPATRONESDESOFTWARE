package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolesPermisosPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Service
public class ObtenerPermisosDeRolUseCase {

    private final RolesPermisosPort rolesPermisosPort;
    private final RolJpaRepository rolJpaRepository;

    public ObtenerPermisosDeRolUseCase(RolesPermisosPort rolesPermisosPort,
                                      RolJpaRepository rolJpaRepository) {
        this.rolesPermisosPort = rolesPermisosPort;
        this.rolJpaRepository = rolJpaRepository;
    }

    public List<Permiso> ejecutar(String nombreRol) {
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol));
        return rolesPermisosPort.listarPorRol(rol);
    }
}
