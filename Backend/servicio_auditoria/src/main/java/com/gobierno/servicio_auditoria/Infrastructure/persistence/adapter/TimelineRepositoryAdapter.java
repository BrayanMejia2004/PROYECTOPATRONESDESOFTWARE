package com.gobierno.servicio_auditoria.infrastructure.persistence.adapter;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.ports.out.TimelineRepositoryPort;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.TimelineRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

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
}
