package com.gobierno.servicio_auditoria.infrastructure.persistence.adapter;

import com.gobierno.servicio_auditoria.domain.ports.out.EstadisticasRepositoryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EstadisticasRepositoryAdapter implements EstadisticasRepositoryPort {

    private final EntityManager entityManager;

    public EstadisticasRepositoryAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Long> contarEventosPorTipo() {
        Map<String, Long> resultado = new HashMap<>();
        Query query = entityManager.createNativeQuery(
                "SELECT tipo, COUNT(*) AS total FROM auditoria GROUP BY tipo ORDER BY total DESC");
        List<Object[]> rows = query.getResultList();
        for (Object[] row : rows) {
            String tipo = (String) row[0];
            Long total = ((Number) row[1]).longValue();
            resultado.put(tipo, total);
        }
        return resultado;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, Long> contarEventosPorHora() {
        Map<Integer, Long> resultado = new HashMap<>();
        Query query = entityManager.createNativeQuery(
                "SELECT EXTRACT(HOUR FROM fecha) AS hora, COUNT(*) AS total FROM auditoria " +
                "GROUP BY hora ORDER BY hora ASC");
        List<Object[]> rows = query.getResultList();
        for (Object[] row : rows) {
            if (row[0] == null) continue;
            Integer hora = ((Number) row[0]).intValue();
            Long total = ((Number) row[1]).longValue();
            resultado.put(hora, total);
        }
        return resultado;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> obtenerTop5Activos() {
        Query query = entityManager.createNativeQuery(
                "SELECT usuario_id, COUNT(*) AS total_acciones FROM auditoria " +
                "GROUP BY usuario_id ORDER BY total_acciones DESC LIMIT 5");
        return query.getResultList();
    }

    @Override
    public List<Integer> obtenerTodosLosUsuarioIds() {
        Query query = entityManager.createNativeQuery(
                "SELECT DISTINCT usuario_id FROM auditoria WHERE usuario_id IS NOT NULL");
        List<?> result = query.getResultList();
        List<Integer> ids = new ArrayList<>();
        for (Object obj : result) {
            if (obj instanceof Number) {
                ids.add(((Number) obj).intValue());
            }
        }
        return ids;
    }
}
