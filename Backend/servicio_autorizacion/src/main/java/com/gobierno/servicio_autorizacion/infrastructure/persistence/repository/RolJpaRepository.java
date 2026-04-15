package com.gobierno.servicio_autorizacion.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public interface RolJpaRepository extends JpaRepository<Rol, Long> {
    
    Optional<Rol> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
}
