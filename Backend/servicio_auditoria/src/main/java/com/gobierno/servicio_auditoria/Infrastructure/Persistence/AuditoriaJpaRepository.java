package com.gobierno.servicio_auditoria.Infrastructure.Persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

// Repositorio JPA para la entidad Auditoria
public interface AuditoriaJpaRepository extends JpaRepository<Auditoria, Long>{
    
}
