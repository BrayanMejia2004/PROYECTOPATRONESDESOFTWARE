package com.gobierno.servicio_identidad.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;

@Repository
public interface PerfilUsuarioJpaRepository extends JpaRepository<PerfilUsuario, Long> { // Repositorio JPA para
                                                                                         // PerfilUsuario

    Optional<PerfilUsuario> findByUsuarioId(Integer usuarioId); // Busca un perfil por ID de usuario

    boolean existsByUsuarioId(Integer usuarioId); // Verifica si existe un perfil para el usuario
}