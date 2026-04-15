package com.gobierno.servicio_autorizacion.infrastructure.persistence.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.RolesPermisos;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolesPermisosPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolesPermisosJpaRepository;

@Repository
public class RolesPermisosAdapter implements RolesPermisosPort {

    private final RolesPermisosJpaRepository rolesPermisosJpaRepository;

    public RolesPermisosAdapter(RolesPermisosJpaRepository rolesPermisosJpaRepository) {
        this.rolesPermisosJpaRepository = rolesPermisosJpaRepository;
    }

    @Override
    @Transactional
    public void asignarPermiso(Rol rol, Permiso permiso) {
        if (!existeAsignacion(rol, permiso)) {
            RolesPermisos rp = new RolesPermisos(rol, permiso);
            rolesPermisosJpaRepository.save(rp);
        }
    }

    @Override
    @Transactional
    public void quitarPermiso(Rol rol, Permiso permiso) {
        List<RolesPermisos> asignaciones = rolesPermisosJpaRepository
                .findByRolAndPermiso(rol, permiso);
        if (!asignaciones.isEmpty()) {
            rolesPermisosJpaRepository.deleteAll(asignaciones);
        }
    }

    @Override
    public List<Permiso> listarPorRol(Rol rol) {
        return rolesPermisosJpaRepository.findPermisosByRol(rol);
    }

    @Override
    public List<Rol> listarRolesPorPermiso(Permiso permiso) {
        return rolesPermisosJpaRepository.findByPermiso(permiso)
                .stream()
                .map(RolesPermisos::getRol)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existeAsignacion(Rol rol, Permiso permiso) {
        return rolesPermisosJpaRepository.existsByRolAndPermiso(rol, permiso);
    }

    @Override
    @Transactional
    public void eliminarPorRol(Rol rol) {
        rolesPermisosJpaRepository.deleteByRol(rol);
    }
}
