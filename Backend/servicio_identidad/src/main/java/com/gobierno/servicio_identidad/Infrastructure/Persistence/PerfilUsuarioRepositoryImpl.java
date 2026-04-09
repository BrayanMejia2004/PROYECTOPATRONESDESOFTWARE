package com.gobierno.servicio_identidad.Infrastructure.Persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_identidad.Domain.Model.PerfilUsuario;
import com.gobierno.servicio_identidad.Ports.Output.PerfilUsuarioRepository;

@Repository
public class PerfilUsuarioRepositoryImpl implements PerfilUsuarioRepository {

    private final PerfilUsuarioJpaRepository jpaRepository;

    public PerfilUsuarioRepositoryImpl(PerfilUsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

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

    @Override
    public PerfilUsuario actualizar(PerfilUsuario perfil) {
        return jpaRepository.save(perfil);
    }
}
