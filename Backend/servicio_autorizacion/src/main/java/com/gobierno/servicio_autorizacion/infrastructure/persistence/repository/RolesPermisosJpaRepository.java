package com.gobierno.servicio_autorizacion.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.RolesPermisos;

@Repository
public interface RolesPermisosJpaRepository extends JpaRepository<RolesPermisos, Long> {

    List<RolesPermisos> findByRol(Rol rol);

    List<RolesPermisos> findByPermiso(Permiso permiso);

    boolean existsByRolAndPermiso(Rol rol, Permiso permiso);

    List<RolesPermisos> findByRolAndPermiso(Rol rol, Permiso permiso);

    void deleteByRol(Rol rol);

    @Query("SELECT rp.permiso FROM RolesPermisos rp WHERE rp.rol = :rol")
    List<Permiso> findPermisosByRol(@Param("rol") Rol rol);
}
