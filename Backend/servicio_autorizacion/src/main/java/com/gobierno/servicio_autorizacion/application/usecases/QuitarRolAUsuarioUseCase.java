package com.gobierno.servicio_autorizacion.application.usecases;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_autorizacion.domain.entities.Rol;
import com.gobierno.servicio_autorizacion.domain.ports.out.UsuariosRolesPort;
import com.gobierno.servicio_autorizacion.infrastructure.persistence.repository.RolJpaRepository;

@Service
public class QuitarRolAUsuarioUseCase {

    private final UsuariosRolesPort usuariosRolesPort;
    private final RolJpaRepository rolJpaRepository;

    public QuitarRolAUsuarioUseCase(UsuariosRolesPort usuariosRolesPort,
                                    RolJpaRepository rolJpaRepository) {
        this.usuariosRolesPort = usuariosRolesPort;
        this.rolJpaRepository = rolJpaRepository;
    }

    public void ejecutar(String username, String nombreRol) {
        Rol rol = rolJpaRepository.findByNombre(nombreRol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol));
        usuariosRolesPort.quitarRol(username, rol);
    }
}
