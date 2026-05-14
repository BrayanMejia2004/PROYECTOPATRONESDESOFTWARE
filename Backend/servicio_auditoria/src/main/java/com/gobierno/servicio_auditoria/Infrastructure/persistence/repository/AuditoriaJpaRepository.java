package com.gobierno.servicio_auditoria.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public interface AuditoriaJpaRepository extends JpaRepository<Auditoria, Long> {
}