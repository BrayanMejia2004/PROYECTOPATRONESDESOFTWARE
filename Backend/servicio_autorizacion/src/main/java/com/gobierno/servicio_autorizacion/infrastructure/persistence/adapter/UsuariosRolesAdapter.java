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
public class UsuariosRolesAdapter implements UsuariosRolesPort {

    private final UsuariosRolesJpaRepository usuariosRolesJpaRepository;

    public UsuariosRolesAdapter(UsuariosRolesJpaRepository usuariosRolesJpaRepository) {
        this.usuariosRolesJpaRepository = usuariosRolesJpaRepository;
    }

    @Override
    @Transactional
    public void asignarRol(String username, Rol rol) {
        if (!tieneRol(username, rol)) {
            UsuariosRoles ur = new UsuariosRoles(username, rol);
            usuariosRolesJpaRepository.save(ur);
        }
    }

    @Override
    @Transactional
    public void quitarRol(String username, Rol rol) {
        usuariosRolesJpaRepository.deleteByUsernameAndRol(username, rol);
    }

    @Override
    public List<String> listarPorUsername(String username) {
        return usuariosRolesJpaRepository.findByUsername(username)
                .stream()
                .map(ur -> ur.getRol().getNombre())
                .collect(Collectors.toList());
    }

    @Override
    public boolean tieneRol(String username, Rol rol) {
        return usuariosRolesJpaRepository.existsByUsernameAndRol(username, rol);
    }

    @Override
    @Transactional
    public void eliminarPorUsername(String username) {
        usuariosRolesJpaRepository.deleteByUsername(username);
    }
    
    @Override
    @Transactional
    public void eliminarPorRol(Rol rol) {
        usuariosRolesJpaRepository.deleteByRol(rol);
    }
}
