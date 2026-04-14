package com.gobierno.servicio_autorizacion.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolRepositoryPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Repository
public class RolRepositorioAdapter implements RolRepositoryPort {

    private final RolJpaRepository rolJpaRepository;

    public RolRepositorioAdapter(RolJpaRepository rolJpaRepository) {
        this.rolJpaRepository = rolJpaRepository;
    }

    @Override
    public Rol guardar(Rol rol) {
        return rolJpaRepository.save(rol);
    }

}
