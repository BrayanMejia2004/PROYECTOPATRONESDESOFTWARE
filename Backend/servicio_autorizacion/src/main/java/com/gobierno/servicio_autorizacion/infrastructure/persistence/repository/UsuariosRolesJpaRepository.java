package com.gobierno.servicio_autorizacion.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.UsuariosRoles;

@Repository
public interface UsuariosRolesJpaRepository extends JpaRepository<UsuariosRoles, Long> {  // Repositorio JPA para entidad UsuariosRoles

    List<UsuariosRoles> findByUsername(String username);  // Lista todas las relaciones de un usuario

    Optional<UsuariosRoles> findByUsernameAndRol(String username, Rol rol);  // Busca una relación específica usuario-rol

    boolean existsByUsernameAndRol(String username, Rol rol);  // Verifica si existe una relación usuario-rol

    void deleteByUsername(String username);  // Elimina todas las relaciones de un usuario

    void deleteByUsernameAndRol(String username, Rol rol);  // Elimina una relación específica
    
    List<UsuariosRoles> findByRol(Rol rol);  // Lista todas las relaciones de un rol
    
    void deleteByRol(Rol rol);  // Elimina todas las relaciones de un rol
}