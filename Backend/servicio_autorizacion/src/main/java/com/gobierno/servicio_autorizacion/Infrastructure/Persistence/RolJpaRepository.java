package com.gobierno.servicio_autorizacion.Infrastructure.Persistence;


import org.springframework.data.jpa.repository.JpaRepository;

import com.gobierno.servicio_autorizacion.Domain.Model.Rol;

// Repositorio JPA para la entidad Rol
public interface RolJpaRepository extends JpaRepository<Rol, Long> {
    
}
