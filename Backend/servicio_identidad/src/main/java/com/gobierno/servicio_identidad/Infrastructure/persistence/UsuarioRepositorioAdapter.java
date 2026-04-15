package com.gobierno.servicio_identidad.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.UsuarioJpaRepository;

@Repository
public class UsuarioRepositorioAdapter implements UsuarioRepositorioPort {
    
    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositorioAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @SuppressWarnings("null")
    @Override
    public Usuario guardar(Usuario usuario) {
        return jpaRepository.save(usuario);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return jpaRepository.findByUsername(username);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public boolean existePorUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void eliminarPorUsername(String username) {
        jpaRepository.deleteByUsername(username);
    }
}
