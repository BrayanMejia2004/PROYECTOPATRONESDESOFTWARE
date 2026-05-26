package com.gobierno.servicio_auditoria.infrastructure.persistence.adapter;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.model.ActividadDiariaDTO;
import com.gobierno.servicio_auditoria.domain.ports.out.TimelineRepositoryPort;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.TimelineRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TimelineRepositoryAdapter implements TimelineRepositoryPort {

    private final TimelineRepository timelineRepository;

    public TimelineRepositoryAdapter(TimelineRepository timelineRepository) {
        this.timelineRepository = timelineRepository;
    }

    @Override
    public List<Auditoria> findByUsuarioIdOrderByFechaAsc(Long usuarioId, int limite) {
        Pageable pageable = PageRequest.of(0, limite);
        return timelineRepository.findTimelineByUsuarioId(usuarioId, pageable);
    }

    @Override
    public List<ActividadDiariaDTO> findActividadCalendario(Long usuarioId, int dias) {
        if (usuarioId == null || dias <= 0) return Collections.emptyList();
        LocalDateTime desde = LocalDateTime.now().minusDays(dias);
        List<Object[]> results = timelineRepository.findActividadCalendario(usuarioId, Timestamp.valueOf(desde));
        return results.stream().map(row -> {
            java.sql.Date sqlDate = (java.sql.Date) row[0];
            LocalDate fecha = sqlDate.toLocalDate();
            Long total = ((Number) row[1]).longValue();
            return new ActividadDiariaDTO(fecha, total);
        }).collect(Collectors.toList());
    }
}
