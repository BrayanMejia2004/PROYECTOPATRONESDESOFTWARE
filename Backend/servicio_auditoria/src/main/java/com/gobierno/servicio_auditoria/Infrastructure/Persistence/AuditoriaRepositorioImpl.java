package com.gobierno.servicio_auditoria.Infrastructure.Persistence;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Ports.Output.RegistroAuditoria;

@Repository
public class AuditoriaRepositorioImpl implements RegistroAuditoria{

    private final AuditoriaJpaRepository auditoriaJpaRepository;

    public AuditoriaRepositorioImpl(AuditoriaJpaRepository auditoriaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    @Override
    public void registrarAccion(Auditoria auditoria) {

        auditoriaJpaRepository.save(auditoria);
    }

}
