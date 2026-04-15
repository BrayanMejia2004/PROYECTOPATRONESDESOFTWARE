package com.gobierno.servicio_autorizacion.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.UsuariosRoles;

@Repository
public interface UsuariosRolesJpaRepository extends JpaRepository<UsuariosRoles, Long> {

    List<UsuariosRoles> findByUsername(String username);

    Optional<UsuariosRoles> findByUsernameAndRol(String username, Rol rol);

    boolean existsByUsernameAndRol(String username, Rol rol);

    void deleteByUsername(String username);

    void deleteByUsernameAndRol(String username, Rol rol);
    
    List<UsuariosRoles> findByRol(Rol rol);
    
    void deleteByRol(Rol rol);
}
