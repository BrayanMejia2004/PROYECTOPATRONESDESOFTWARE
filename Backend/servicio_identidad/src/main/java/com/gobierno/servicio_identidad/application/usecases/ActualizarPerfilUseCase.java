package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.ports.out.PerfilUsuarioRepositoryPort;

@Service
public class ActualizarPerfilUseCase {

    private final PerfilUsuarioRepositoryPort perfilRepositoryPort;

    public ActualizarPerfilUseCase(PerfilUsuarioRepositoryPort perfilRepositoryPort) {
        this.perfilRepositoryPort = perfilRepositoryPort;
    }

    public PerfilUsuario ejecutar(Integer usuarioId, String nombre, 
            String apellido, String telefono) {

        PerfilUsuario perfil = perfilRepositoryPort.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El perfil no existe"));

        if (nombre != null && !nombre.isBlank()) {
            perfil.setNombre(nombre);
        }

        if (apellido != null && !apellido.isBlank()) {
            perfil.setApellido(apellido);
        }

        if (telefono != null && !telefono.isBlank()) {
            perfil.setTelefono(telefono);
        }

        return perfilRepositoryPort.actualizar(perfil);
    }
}
