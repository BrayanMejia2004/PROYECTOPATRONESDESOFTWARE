package com.gobierno.servicio_identidad.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gobierno.servicio_identidad.domain.entities.Usuario;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> { // Repositorio JPA para la entidad Usuario

    Optional<Usuario> findByUsername(String username); // Busca un usuario por username (retorna Optional)

    Optional<Usuario> findByEmail(String email); // Busca un usuario por email (retorna Optional)

    boolean existsByUsername(String username); // Verifica si existe un usuario con el username

    boolean existsByEmail(String email); // Verifica si existe un usuario con el email

    void deleteByUsername(String username); // Elimina un usuario por su username
}