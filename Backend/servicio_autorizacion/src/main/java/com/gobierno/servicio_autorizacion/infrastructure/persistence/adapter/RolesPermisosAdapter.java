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
public class RolesPermisosAdapter implements RolesPermisosPort { // Adapter que implementa el puerto del dominio

    private final RolesPermisosJpaRepository rolesPermisosJpaRepository; // Repositorio JPA

    public RolesPermisosAdapter(RolesPermisosJpaRepository rolesPermisosJpaRepository) {
        this.rolesPermisosJpaRepository = rolesPermisosJpaRepository;
    }

    @Override
    @Transactional
    public void asignarPermiso(Rol rol, Permiso permiso) { // Asigna un permiso a un rol
        if (!existeAsignacion(rol, permiso)) { // Si no existe la relación
            RolesPermisos rp = new RolesPermisos(rol, permiso); // Crea la relación
            rolesPermisosJpaRepository.save(rp); // Persiste la relación
        }
    }

    @Override
    @Transactional
    public void quitarPermiso(Rol rol, Permiso permiso) { // Quita un permiso a un rol
        List<RolesPermisos> asignaciones = rolesPermisosJpaRepository
                .findByRolAndPermiso(rol, permiso); // Busca la relación
        if (!asignaciones.isEmpty()) { // Si existe
            rolesPermisosJpaRepository.deleteAll(asignaciones); // Elimina la relación
        }
    }

    @Override
    public List<Permiso> listarPorRol(Rol rol) { // Lista los permisos de un rol
        return rolesPermisosJpaRepository.findPermisosByRol(rol); // Retorna la lista de permisos
    }

    @Override
    public List<Rol> listarRolesPorPermiso(Permiso permiso) { // Lista los roles que tienen un permiso
        return rolesPermisosJpaRepository.findByPermiso(permiso) // Busca las relaciones
                .stream()
                .map(RolesPermisos::getRol) // Extrae el rol
                .collect(Collectors.toList()); // Convierte a lista
    }

    @Override
    public boolean existeAsignacion(Rol rol, Permiso permiso) { // Verifica si existe una relación
        return rolesPermisosJpaRepository.existsByRolAndPermiso(rol, permiso);
    }

    @Override
    @Transactional
    public void eliminarPorRol(Rol rol) { // Elimina todas las relaciones de un rol
        rolesPermisosJpaRepository.deleteByRol(rol);
    }
}