package com.gobierno.servicio_reportes.infrastructure.persistence.repository;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteRepositoryPort;
import com.gobierno.servicio_reportes.infrastructure.persistence.entity.ReporteEntity;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ReporteRepositoryAdapter implements ReporteRepositoryPort {
    
    private final ReporteJpaRepository jpaRepository;
    
    public ReporteRepositoryAdapter(ReporteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Reporte guardar(Reporte reporte) {
        ReporteEntity entity = ReporteEntity.fromDomain(reporte);
        ReporteEntity saved = jpaRepository.save(entity);
        Objects.requireNonNull(saved, "Error al guardar reporte en la base de datos");
        return saved.toDomain();
    }
    
    @Override
    public List<Reporte> findAll() {
        return jpaRepository.findAll().stream()
                .map(ReporteEntity::toDomain)
                .toList();
    }
    
    @Override
    public List<Reporte> findByTipoOrderByFechaGeneracionDesc(String tipo) {
        return jpaRepository.findByTipoOrderByFechaGeneracionDesc(tipo).stream()
                .map(ReporteEntity::toDomain)
                .toList();
    }
    
    @Override
    public Optional<Reporte> findById(Long id) {
        return jpaRepository.findById(id)
                .map(ReporteEntity::toDomain);
    }
}
