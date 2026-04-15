package com.gobierno.servicio_autorizacion.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gobierno.servicio_autorizacion.domain.entities.Permiso;
import com.gobierno.servicio_autorizacion.domain.ports.out.PermisoRepositoryPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.PermisoJpaRepository;

@Repository
public class PermisoRepositorioAdapter implements PermisoRepositoryPort {

    private final PermisoJpaRepository permisoJpaRepository;

    public PermisoRepositorioAdapter(PermisoJpaRepository permisoJpaRepository) {
        this.permisoJpaRepository = permisoJpaRepository;
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public Permiso guardar(Permiso permiso) {
        return permisoJpaRepository.save(permiso);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Permiso> buscarPorId(Long id) {
        return permisoJpaRepository.findById(id);
    }

    @Override
    public Optional<Permiso> buscarPorNombre(String nombre) {
        return permisoJpaRepository.findByNombre(nombre);
    }

    @Override
    public List<Permiso> listarTodos() {
        return permisoJpaRepository.findAll();
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return permisoJpaRepository.existsByNombre(nombre);
    }
}
