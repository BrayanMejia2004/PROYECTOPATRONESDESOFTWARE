package com.gobierno.servicio_autorizacion.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;

@Repository
public interface PermisoJpaRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
