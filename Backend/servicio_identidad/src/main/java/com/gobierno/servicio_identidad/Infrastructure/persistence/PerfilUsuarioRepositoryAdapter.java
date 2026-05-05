package com.gobierno.servicio_identidad.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.ports.out.PerfilUsuarioRepositoryPort;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.PerfilUsuarioJpaRepository;

@Repository
public class PerfilUsuarioRepositoryAdapter implements PerfilUsuarioRepositoryPort { // Adapter que implementa el puerto
                                                                                     // del dominio

    private final PerfilUsuarioJpaRepository jpaRepository; // Repositorio JPA de perfiles

    public PerfilUsuarioRepositoryAdapter(PerfilUsuarioJpaRepository jpaRepository) { // Constructor con inyección
        this.jpaRepository = jpaRepository; // Asigna el repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public PerfilUsuario guardar(PerfilUsuario perfil) { // Persiste un perfil (create o update)
        return jpaRepository.save(perfil); // Delega la operación al repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public Optional<PerfilUsuario> buscarPorUsuarioId(Integer usuarioId) { // Busca un perfil por ID de usuario
        return jpaRepository.findByUsuarioId(usuarioId); // Delega la operación al repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public boolean existePorUsuarioId(Integer usuarioId) { // Verifica si existe un perfil para el usuario
        return jpaRepository.existsByUsuarioId(usuarioId); // Delega la operación al repositorio JPA
    }

    @Override // Sobrescribe el método de la interfaz
    public PerfilUsuario actualizar(PerfilUsuario perfil) { // Actualiza un perfil existente
        return jpaRepository.save(perfil); // Delega la operación al repositorio JPA (save funciona para update)
    }
}