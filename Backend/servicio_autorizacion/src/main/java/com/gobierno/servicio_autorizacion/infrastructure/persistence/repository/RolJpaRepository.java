package com.gobierno.servicio_autorizacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;

public interface RolJpaRepository extends JpaRepository<Rol, Long> {
    
}
