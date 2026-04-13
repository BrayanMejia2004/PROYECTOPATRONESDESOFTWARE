package com.gobierno.servicio_reportes.infrastructure.persistence.repository;

import com.gobierno.servicio_reportes.infrastructure.persistence.entity.ReporteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteJpaRepository extends JpaRepository<ReporteEntity, Long> {
    List<ReporteEntity> findByTipoOrderByFechaGeneracionDesc(String tipo);
}
