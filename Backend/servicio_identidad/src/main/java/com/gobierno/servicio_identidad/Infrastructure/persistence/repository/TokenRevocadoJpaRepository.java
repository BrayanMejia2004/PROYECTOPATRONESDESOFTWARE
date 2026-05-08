package com.gobierno.servicio_identidad.infrastructure.persistence.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gobierno.servicio_identidad.domain.entities.TokenRevocado;

@Repository
public interface TokenRevocadoJpaRepository extends JpaRepository<TokenRevocado, Long> {

    boolean existsByTokenHash(String tokenHash);

    long countByFechaRevocacionAfter(Timestamp fecha);
}
