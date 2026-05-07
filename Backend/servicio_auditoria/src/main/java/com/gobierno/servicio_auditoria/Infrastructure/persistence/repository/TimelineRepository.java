package com.gobierno.servicio_auditoria.infrastructure.persistence.repository;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TimelineRepository extends JpaRepository<Auditoria, Long> {

    @Query("SELECT a FROM Auditoria a WHERE a.usuario_id = :usuarioId")
    List<Auditoria> findTimelineByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);
}
