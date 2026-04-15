package com.gobierno.servicio_identidad.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.ports.out.PerfilUsuarioRepositoryPort;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.PerfilUsuarioJpaRepository;

@Repository
public class PerfilUsuarioRepositoryAdapter implements PerfilUsuarioRepositoryPort {

    private final PerfilUsuarioJpaRepository jpaRepository;

    public PerfilUsuarioRepositoryAdapter(PerfilUsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @SuppressWarnings("null")
    @Override
    public PerfilUsuario guardar(PerfilUsuario perfil) {
        return jpaRepository.save(perfil);
    }

    @Override
    public Optional<PerfilUsuario> buscarPorUsuarioId(Integer usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public boolean existePorUsuarioId(Integer usuarioId) {
        return jpaRepository.existsByUsuarioId(usuarioId);
    }

    @SuppressWarnings("null")
    @Override
    public PerfilUsuario actualizar(PerfilUsuario perfil) {
        return jpaRepository.save(perfil);
    }
}
