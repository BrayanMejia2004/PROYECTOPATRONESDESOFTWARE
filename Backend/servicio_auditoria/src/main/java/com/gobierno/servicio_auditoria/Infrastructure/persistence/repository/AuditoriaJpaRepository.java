package com.gobierno.servicio_auditoria.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public interface AuditoriaJpaRepository extends JpaRepository<Auditoria, Long>{
    
    @Query("SELECT a FROM Auditoria a WHERE a.usuario_id = :usuarioId")
    List<Auditoria> findByUsuarioId(@Param("usuarioId") Integer usuarioId);
    
    @Query("SELECT a FROM Auditoria a WHERE a.tipo = :tipo")
    List<Auditoria> findByTipo(@Param("tipo") String tipo);
    
    @Query("SELECT a FROM Auditoria a WHERE a.accion = :accion")
    List<Auditoria> findByAccion(@Param("accion") String accion);
}
