package com.gobierno.servicio_autorizacion.infrastructure.persistence.adapter;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.entities.UsuariosRoles;
import com.gobierno.servicio_autorizacion.domain.ports.out.UsuariosRolesPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.UsuariosRolesJpaRepository;

@Repository
public class UsuariosRolesAdapter implements UsuariosRolesPort { // Adapter que implementa el puerto del dominio

    private final UsuariosRolesJpaRepository usuariosRolesJpaRepository; // Repositorio JPA

    public UsuariosRolesAdapter(UsuariosRolesJpaRepository usuariosRolesJpaRepository) {
        this.usuariosRolesJpaRepository = usuariosRolesJpaRepository;
    }

    @Override
    @Transactional
    public void asignarRol(String username, Rol rol) { // Asigna un rol a un usuario
        if (!tieneRol(username, rol)) { // Si el usuario no tiene el rol
            UsuariosRoles ur = new UsuariosRoles(username, rol); // Crea la relación
            usuariosRolesJpaRepository.save(ur); // Persiste la relación
        }
    }

    @Override
    @Transactional
    public void quitarRol(String username, Rol rol) { // Quita un rol a un usuario
        usuariosRolesJpaRepository.deleteByUsernameAndRol(username, rol); // Elimina la relación
    }

    @Override
    public List<String> listarPorUsername(String username) { // Lista los roles de un usuario
        return usuariosRolesJpaRepository.findByUsername(username) // Busca las relaciones del usuario
                .stream()
                .map(ur -> ur.getRol().getNombre()) // Extrae el nombre del rol
                .collect(Collectors.toList()); // Convierte a lista
    }

    @Override
    public boolean tieneRol(String username, Rol rol) { // Verifica si un usuario tiene un rol
        return usuariosRolesJpaRepository.existsByUsernameAndRol(username, rol); // Retorna true si existe la relación
    }

    @Override
    @Transactional
    public void eliminarPorUsername(String username) { // Elimina todas las relaciones de un usuario
        usuariosRolesJpaRepository.deleteByUsername(username);
    }

    @Override
    @Transactional
    public void eliminarPorRol(Rol rol) { // Elimina todas las relaciones de un rol
        usuariosRolesJpaRepository.deleteByRol(rol);
    }
}