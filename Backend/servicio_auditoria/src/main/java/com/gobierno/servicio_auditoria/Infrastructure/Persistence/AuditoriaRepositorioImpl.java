package com.gobierno.servicio_auditoria.Infrastructure.Persistence;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Ports.Output.RegistroAuditoria;

@Repository
public class AuditoriaRepositorioImpl implements RegistroAuditoria{

    // Repositorio JPA para operaciones en BD
    private final AuditoriaJpaRepository auditoriaJpaRepository;

    // Inyección de dependencia por constructor
    public AuditoriaRepositorioImpl(AuditoriaJpaRepository auditoriaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    // Metodo para guardar la auditoria en la base de datos
    @Override
    public void registrarAccion(Auditoria auditoria) {

        auditoriaJpaRepository.save(auditoria);
    }

}
