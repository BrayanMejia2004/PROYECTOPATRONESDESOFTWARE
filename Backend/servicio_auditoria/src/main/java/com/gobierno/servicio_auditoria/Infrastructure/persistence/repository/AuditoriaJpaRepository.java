package com.gobierno.servicio_auditoria.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public interface AuditoriaJpaRepository extends JpaRepository<Auditoria, Long>{  // Repositorio JPA para entidad Auditoria
    
    @Query("SELECT a FROM Auditoria a WHERE a.usuario_id = :usuarioId")  // Query JPQL
    List<Auditoria> findByUsuarioId(@Param("usuarioId") Integer usuarioId);  // Busca auditorías por ID de usuario
    
    @Query("SELECT a FROM Auditoria a WHERE a.tipo = :tipo")  // Query JPQL
    List<Auditoria> findByTipo(@Param("tipo") String tipo);  // Busca auditorías por tipo
    
    @Query("SELECT a FROM Auditoria a WHERE a.accion = :accion")  // Query JPQL
    List<Auditoria> findByAccion(@Param("accion") String accion);  // Busca auditorías por acción
}