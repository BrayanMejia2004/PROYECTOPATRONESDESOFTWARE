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
public interface RolesPermisosJpaRepository extends JpaRepository<RolesPermisos, Long> {  // Repositorio JPA para entidad RolesPermisos

    List<RolesPermisos> findByRol(Rol rol);  // Lista todas las relaciones de un rol

    List<RolesPermisos> findByPermiso(Permiso permiso);  // Lista todas las relaciones de un permiso

    boolean existsByRolAndPermiso(Rol rol, Permiso permiso);  // Verifica si existe una relación rol-permiso

    List<RolesPermisos> findByRolAndPermiso(Rol rol, Permiso permiso);  // Busca una relación específica

    void deleteByRol(Rol rol);  // Elimina todas las relaciones de un rol

    @Query("SELECT rp.permiso FROM RolesPermisos rp WHERE rp.rol = :rol")  // Query JPQL personalizada
    List<Permiso> findPermisosByRol(@Param("rol") Rol rol);  // Lista los permisos de un rol
}