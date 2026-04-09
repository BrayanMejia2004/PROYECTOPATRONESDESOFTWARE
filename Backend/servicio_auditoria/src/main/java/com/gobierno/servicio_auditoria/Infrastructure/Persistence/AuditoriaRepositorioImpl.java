package com.gobierno.servicio_auditoria.Infrastructure.Persistence;

import org.springframework.stereotype.Repository;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;
import com.gobierno.servicio_auditoria.Ports.Output.RegistroAuditoria;

// Implementacion del repositorio de auditoria (Adapter del Puerto Output)
@Repository
public class AuditoriaRepositorioImpl implements RegistroAuditoria {

    // Repositorio JPA para operaciones en base de datos
    private final AuditoriaJpaRepository auditoriaJpaRepository;

    // Inyeccion de dependencia por constructor
    public AuditoriaRepositorioImpl(AuditoriaJpaRepository auditoriaJpaRepository) {
        this.auditoriaJpaRepository = auditoriaJpaRepository;
    }

    // Guarda la auditoria en la base de datos
    @Override
    public void registrarAccion(Auditoria auditoria) {
        auditoriaJpaRepository.save(auditoria);
    }
}
