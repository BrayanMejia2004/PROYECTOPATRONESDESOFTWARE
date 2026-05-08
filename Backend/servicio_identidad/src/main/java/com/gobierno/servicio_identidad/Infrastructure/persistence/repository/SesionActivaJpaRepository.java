package com.gobierno.servicio_identidad.infrastructure.persistence.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gobierno.servicio_identidad.domain.entities.SesionActiva;

@Repository
public interface SesionActivaJpaRepository extends JpaRepository<SesionActiva, Long> {

    List<SesionActiva> findByActivaTrue();

    long countByFechaInicioAfter(Timestamp fecha);
}
