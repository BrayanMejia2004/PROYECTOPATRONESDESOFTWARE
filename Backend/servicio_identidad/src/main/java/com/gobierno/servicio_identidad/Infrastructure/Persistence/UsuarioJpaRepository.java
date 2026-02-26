package com.gobierno.servicio_identidad.Infrastructure.Persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gobierno.servicio_identidad.Domain.Model.Usuario;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    
}
