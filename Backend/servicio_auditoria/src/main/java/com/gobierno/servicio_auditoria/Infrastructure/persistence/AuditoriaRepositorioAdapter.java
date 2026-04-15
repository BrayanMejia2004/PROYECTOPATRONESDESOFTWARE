package com.gobierno.servicio_auditoria.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.ports.out.RegistroAuditoriaPort;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.AuditoriaJpaRepository;

@Repository
public class AuditoriaRepositorioAdapter implements RegistroAuditoriaPort {

    private final AuditoriaJpaRepository auditoriaJpaRepository;

    public AuditoriaRepositorioAdapter(AuditoriaJpaRepository auditoriaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    @SuppressWarnings("null")
    @Override
    public void registrarAccion(Auditoria auditoria) {
        auditoriaJpaRepository.save(auditoria);
    }
}
