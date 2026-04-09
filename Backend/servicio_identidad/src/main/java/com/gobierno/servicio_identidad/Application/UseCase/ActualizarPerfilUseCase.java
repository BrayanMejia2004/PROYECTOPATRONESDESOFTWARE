package com.gobierno.servicio_identidad.Application.UseCase;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.Domain.Model.PerfilUsuario;
import com.gobierno.servicio_identidad.Ports.Output.PerfilUsuarioRepository;

@Service
public class ActualizarPerfilUseCase {

    private final PerfilUsuarioRepository perfilRepository;

    public ActualizarPerfilUseCase(PerfilUsuarioRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    public PerfilUsuario ejecutar(Integer usuarioId, String nombre, 
            String apellido, String telefono) {

        PerfilUsuario perfil = perfilRepository.buscarPorUsuarioId(usuarioId)
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

        return perfilRepository.actualizar(perfil);
    }
}
