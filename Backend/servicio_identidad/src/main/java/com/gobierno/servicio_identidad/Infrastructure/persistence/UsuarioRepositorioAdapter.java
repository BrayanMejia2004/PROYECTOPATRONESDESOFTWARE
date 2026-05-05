package com.gobierno.servicio_identidad.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.UsuarioJpaRepository;

@Repository
public class UsuarioRepositorioAdapter implements UsuarioRepositorioPort { // Adapter que implementa el puerto del
                                                                           // dominio

    private final UsuarioJpaRepository jpaRepository; // Repositorio JPA de usuarios

    public UsuarioRepositorioAdapter(UsuarioJpaRepository jpaRepository) { // Constructor con inyección
        this.jpaRepository = jpaRepository; // Asigna el repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public Usuario guardar(Usuario usuario) { // Persiste un usuario (create o update)
        return jpaRepository.save(usuario); // Delegal la operación al repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public Optional<Usuario> buscarPorId(Long id) { // Busca un usuario por su ID
        return jpaRepository.findById(id); // Delegal la operación al repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public Optional<Usuario> buscarPorUsername(String username) { // Busca un usuario por username
        return jpaRepository.findByUsername(username); // Delegal la operación al repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public Optional<Usuario> buscarPorEmail(String email) { // Busca un usuario por email
        return jpaRepository.findByEmail(email); // Delegal la operación al repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public boolean existePorUsername(String username) { // Verifica si existe un usuario con el username
        return jpaRepository.existsByUsername(username); // Delegal la operación al repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public boolean existePorEmail(String email) { // Verifica si existe un usuario con el email
        return jpaRepository.existsByEmail(email); // Delegal la operación al repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public void eliminarPorUsername(String username) { // Elimina un usuario por su username
        jpaRepository.deleteByUsername(username); // Delegal la operación al repositorio JPA
    }
}