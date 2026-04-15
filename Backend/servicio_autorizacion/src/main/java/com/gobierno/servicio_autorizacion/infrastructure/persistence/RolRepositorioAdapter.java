package com.gobierno.servicio_autorizacion.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.RolRepositoryPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Repository
public class RolRepositorioAdapter implements RolRepositoryPort {

    private final RolJpaRepository rolJpaRepository;

    public RolRepositorioAdapter(RolJpaRepository rolJpaRepository) {
        this.rolJpaRepository = rolJpaRepository;
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public Rol guardar(Rol rol) {
        return rolJpaRepository.save(rol);
    }

    @Override
    public Optional<Rol> findByNombre(String nombre) {
        return rolJpaRepository.findByNombre(nombre);
    }

}
