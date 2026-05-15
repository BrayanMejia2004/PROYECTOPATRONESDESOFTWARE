package com.gobierno.servicio_auditoria.infrastructure.persistence.adapter;

import com.gobierno.servicio_auditoria.domain.ports.out.ThreatRepositoryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ThreatRepositoryAdapter implements ThreatRepositoryPort {

    private final EntityManager entityManager;

    public ThreatRepositoryAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> contarIntentosLoginPorIp(LocalDateTime desde) {
        String sql = """
            SELECT ip_origen, COUNT(*) AS total
            FROM auditoria
            WHERE accion = 'LOGIN' AND fecha >= ?1
            GROUP BY ip_origen
            HAVING COUNT(*) >= 3
            ORDER BY total DESC
            """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, Timestamp.valueOf(desde));
        return (List<Object[]>) (List<?>) query.getResultList();
    }

    @Override
    public List<Object[]> contarEventosPorIp(LocalDateTime desde) {
        String sql = """
            SELECT ip_origen, COUNT(*) AS total
            FROM auditoria
            WHERE fecha >= ?1
            GROUP BY ip_origen
            HAVING COUNT(*) >= 10
            ORDER BY total DESC
            """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, Timestamp.valueOf(desde));
        return (List<Object[]>) (List<?>) query.getResultList();
    }

    @Override
    public List<Object[]> contarUsuariosDistintosPorIp(LocalDateTime desde) {
        String sql = """
            SELECT ip_origen, COUNT(DISTINCT usuario_id) AS total_usuarios
            FROM auditoria
            WHERE fecha >= ?1
            GROUP BY ip_origen
            HAVING COUNT(DISTINCT usuario_id) >= 2
            ORDER BY total_usuarios DESC
            """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, Timestamp.valueOf(desde));
        return (List<Object[]>) (List<?>) query.getResultList();
    }

    @Override
    public List<Object[]> eventosEnRangoHorario(LocalDateTime desde, LocalDateTime hasta) {
        String sql = """
            SELECT DISTINCT a.usuario_id, a.ip_origen
            FROM auditoria a
            WHERE a.fecha >= ?1 AND a.fecha <= ?2
              AND EXTRACT(HOUR FROM a.fecha) >= 0 AND EXTRACT(HOUR FROM a.fecha) < 5
            """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, Timestamp.valueOf(desde));
        query.setParameter(2, Timestamp.valueOf(hasta));
        return (List<Object[]>) (List<?>) query.getResultList();
    }

    @Override
    public boolean ipUsadaPorUsuario(String ip, Integer usuarioId, LocalDateTime desde) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM auditoria
            WHERE ip_origen = ?1 AND usuario_id = ?2 AND fecha >= ?3
            """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, ip);
        query.setParameter(2, usuarioId);
        query.setParameter(3, Timestamp.valueOf(desde));
        Object result = query.getSingleResult();
        return result instanceof Boolean ? (Boolean) result : false;
    }
}