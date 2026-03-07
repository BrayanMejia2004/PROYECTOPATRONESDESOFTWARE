package com.gobierno.servicio_auditoria.Infrastructure.Persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

public interface AuditoriaJpaRepository extends JpaRepository<Auditoria, Long>{
    
}
