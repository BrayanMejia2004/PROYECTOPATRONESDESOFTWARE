package com.gobierno.servicio_auditoria.infrastructure.persistence.repository;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.sql.Timestamp;
import java.util.List;

public interface TimelineRepository extends JpaRepository<Auditoria, Long> {

    @Query("SELECT a FROM Auditoria a WHERE a.usuario_id = :usuarioId")
    List<Auditoria> findTimelineByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query(value = "SELECT DATE(fecha) AS dia, COUNT(*) AS total FROM auditoria " +
           "WHERE usuario_id = :usuarioId AND fecha >= :desde " +
           "GROUP BY dia ORDER BY dia ASC", nativeQuery = true)
    List<Object[]> findActividadCalendario(@Param("usuarioId") Long usuarioId,
                                            @Param("desde") Timestamp desde);
}
