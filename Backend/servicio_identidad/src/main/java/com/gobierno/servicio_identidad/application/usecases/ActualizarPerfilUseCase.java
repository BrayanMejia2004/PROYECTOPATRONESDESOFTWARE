package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.ports.out.PerfilUsuarioRepositoryPort;

@Service
public class ActualizarPerfilUseCase { // Caso de uso para actualizar el perfil de un usuario

    private final PerfilUsuarioRepositoryPort perfilRepositoryPort; // Puerto de repositorio de perfiles

    public ActualizarPerfilUseCase(PerfilUsuarioRepositoryPort perfilRepositoryPort) { // Constructor
        this.perfilRepositoryPort = perfilRepositoryPort; // Asigna el repositorio de perfiles
    }

    public PerfilUsuario ejecutar(Integer usuarioId, String nombre, // Método principal para actualizar perfil
            String apellido, String telefono) { // Parámetros a actualizar

        PerfilUsuario perfil = perfilRepositoryPort.buscarPorUsuarioId(usuarioId) // Busca el perfil por ID de usuario
                .orElseThrow(() -> new IllegalArgumentException("El perfil no existe")); // Lanza excepción si no existe

        if (nombre != null && !nombre.isBlank()) { // Si el nombre no está vacío
            perfil.setNombre(nombre); // Actualiza el nombre
        }

        if (apellido != null && !apellido.isBlank()) { // Si el apellido no está vacío
            perfil.setApellido(apellido); // Actualiza el apellido
        }

        if (telefono != null && !telefono.isBlank()) { // Si el teléfono no está vacío
            perfil.setTelefono(telefono); // Actualiza el teléfono
        }

        return perfilRepositoryPort.actualizar(perfil); // Persiste los cambios y retorna el perfil actualizado
    }
}