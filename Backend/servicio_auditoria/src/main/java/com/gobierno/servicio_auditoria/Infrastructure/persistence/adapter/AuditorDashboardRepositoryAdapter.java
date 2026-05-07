package com.gobierno.servicio_auditoria.infrastructure.persistence.adapter;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.ports.out.AuditorDashboardRepositoryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AuditorDashboardRepositoryAdapter implements AuditorDashboardRepositoryPort {

    private final EntityManager entityManager;

    public AuditorDashboardRepositoryAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public long contarEventosDesde(LocalDateTime desde) {
        Query query = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM auditoria WHERE fecha >= :desde");
        query.setParameter("desde", java.sql.Timestamp.valueOf(desde));
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> actividadDiaria(LocalDateTime desde) {
        Query query = entityManager.createNativeQuery(
                "SELECT DATE(fecha) AS dia, COUNT(*) AS total FROM auditoria " +
                "WHERE fecha >= :desde GROUP BY dia ORDER BY dia ASC");
        query.setParameter("desde", java.sql.Timestamp.valueOf(desde));
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Auditoria> ultimosEventosSeguridad(int limite) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM auditoria WHERE tipo = 'SEGURIDAD' ORDER BY fecha DESC LIMIT :limite",
                Auditoria.class);
        query.setParameter("limite", limite);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> topIpsHoy(LocalDateTime desde) {
        Query query = entityManager.createNativeQuery(
                "SELECT ip_origen, COUNT(*) AS total FROM auditoria " +
                "WHERE fecha >= :desde AND ip_origen IS NOT NULL " +
                "GROUP BY ip_origen ORDER BY total DESC LIMIT 5");
        query.setParameter("desde", java.sql.Timestamp.valueOf(desde));
        return query.getResultList();
    }
}
