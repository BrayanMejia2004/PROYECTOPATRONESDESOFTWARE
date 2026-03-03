package com.gobierno.servicio_identidad.Infrastructure.Persistence;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.gobierno.servicio_identidad.Domain.Model.Usuario;
import com.gobierno.servicio_identidad.Ports.Output.UsuarioRepositorio;

// Implementación del repositorio de usuario utilizando JPA
@Repository
public class UsuarioRepositorioImpl implements UsuarioRepositorio {
    
        private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositorioImpl(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    // Implementación de los métodos del repositorio utilizando el JPA Repository

    // Método para guardar un usuario
    @Override
    public Usuario guardar(Usuario usuario) {
        return jpaRepository.save(usuario);
    }

    // Método para buscar un usuario por su ID
    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return jpaRepository.findByUsername(username);
    }

    // Método para buscar un usuario por su email
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    // Método para verificar la existencia de un usuario por su username
    @Override
    public boolean existePorUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    // Método para verificar la existencia de un usuario por su email
    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    // Método para eliminar un usuario por su username
    @Override
    public void eliminarPorUsername(String username) {
        jpaRepository.deleteByUsername(username);
    }
    
}
