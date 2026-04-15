package com.gobierno.servicio_autorizacion.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.UsuariosRolesPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Service
public class AsignarRolAUsuarioUseCase {

    private final UsuariosRolesPort usuariosRolesPort;
    private final RolJpaRepository rolJpaRepository;

    public AsignarRolAUsuarioUseCase(UsuariosRolesPort usuariosRolesPort,
                                     RolJpaRepository rolJpaRepository) {
        this.usuariosRolesPort = usuariosRolesPort;
        this.rolJpaRepository = rolJpaRepository;
    }

    public void ejecutar(String username, String nombreRol) {
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol));
        usuariosRolesPort.asignarRol(username, rol);
    }

    public List<String> obtenerRolesDeUsuario(String username) {
        return usuariosRolesPort.listarPorUsername(username);
    }
}
