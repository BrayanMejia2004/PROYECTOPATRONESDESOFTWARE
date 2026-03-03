package com.gobierno.servicio_identidad.Infrastructure.Persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gobierno.servicio_identidad.Domain.Model.Usuario;

// Repositorio JPA para la entidad Usuario
public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> {

    // Métodos personalizados para buscar por username y email
    Optional<Usuario> findByUsername(String username);

    // Método para buscar por email
    Optional<Usuario> findByEmail(String email);

    // Métodos para verificar la existencia de username y email
    boolean existsByUsername(String username);

    // Método para verificar la existencia de email
    boolean existsByEmail(String email);

    // Método para eliminar un usuario por su username
    void deleteByUsername(String username);
  
}
