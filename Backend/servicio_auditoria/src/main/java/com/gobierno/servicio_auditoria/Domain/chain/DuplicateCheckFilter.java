package com.gobierno.servicio_auditoria.domain.chain;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.AuditoriaJpaRepository;
import java.sql.Timestamp;
import java.util.List;

public class DuplicateCheckFilter extends AbstractAuditoriaFilter { // Verifica duplicados recientes

    private final AuditoriaJpaRepository auditoriaJpaRepository; // Repositorio JPA (sin modificar)

    public DuplicateCheckFilter(AuditoriaJpaRepository auditoriaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    @Override
    public boolean doFilter(Auditoria auditoria) { // Verifica duplicados reales
        if (auditoria.getUsuario_id() == null || auditoria.getAccion() == null) {
            return false; // Datos incompletos
        }

        // Busca auditorías del mismo usuario usando el repositorio existente
        List<Auditoria> auditorias = auditoriaJpaRepository.findByUsuarioId(auditoria.getUsuario_id());

        // Filtra en memoria para encontrar duplicados en los últimos 5 segundos
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        Timestamp hace5Segundos = new Timestamp(ahora.getTime() - 5000);

        for (Auditoria existente : auditorias) {
            if (auditoria.getAccion().equals(existente.getAccion())) {
                // Verifica si es reciente
                if (existente.getFecha() != null && existente.getFecha().after(hace5Segundos)) {
                    return false; // Hay un duplicado reciente
                }
            }
        }

        return doNext(auditoria); // No hay duplicados, pasa al siguiente filtro
    }
}
