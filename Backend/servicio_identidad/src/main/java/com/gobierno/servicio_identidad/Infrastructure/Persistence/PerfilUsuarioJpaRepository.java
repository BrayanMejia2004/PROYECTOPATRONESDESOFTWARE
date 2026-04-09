package com.gobierno.servicio_identidad.Infrastructure.Persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gobierno.servicio_identidad.Domain.Model.PerfilUsuario;

@Repository
public interface PerfilUsuarioJpaRepository extends JpaRepository<PerfilUsuario, Long> {

    Optional<PerfilUsuario> findByUsuarioId(Integer usuarioId);

    boolean existsByUsuarioId(Integer usuarioId);
}
