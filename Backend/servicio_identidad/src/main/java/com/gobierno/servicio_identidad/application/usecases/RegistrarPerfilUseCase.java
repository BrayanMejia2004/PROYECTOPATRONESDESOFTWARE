package com.gobierno.servicio_identidad.application.usecases;

import org.springframework.stereotype.Service;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.ports.out.PerfilUsuarioRepositoryPort;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;

@Service
public class RegistrarPerfilUseCase { // Caso de uso para registrar el perfil de un usuario

    private final PerfilUsuarioRepositoryPort perfilRepositoryPort; // Puerto de repositorio de perfiles
    private final UsuarioRepositorioPort usuarioRepositoryPort; // Puerto de repositorio de usuarios

    public RegistrarPerfilUseCase(PerfilUsuarioRepositoryPort perfilRepositoryPort, // Constructor
            UsuarioRepositorioPort usuarioRepositoryPort) { // Constructor con inyección de dependencias
        this.perfilRepositoryPort = perfilRepositoryPort; // Asigna el repositorio de perfiles
        this.usuarioRepositoryPort = usuarioRepositoryPort; // Asigna el repositorio de usuarios
    }

    public PerfilUsuario ejecutar(Integer usuarioId, String nombre, // Método principal para registrar perfil
            String apellido, String telefono) { // Parámetros del perfil a registrar

        if (usuarioRepositoryPort.buscarPorId(usuarioId.longValue()).isEmpty()) { // Si el usuario no existe
            throw new IllegalArgumentException("El usuario no existe"); // Lanza excepción
        }

        if (perfilRepositoryPort.existePorUsuarioId(usuarioId)) { // Si el usuario ya tiene perfil
            throw new IllegalArgumentException("El usuario ya tiene un perfil registrado"); // Lanza excepción
        }

        if (nombre == null || nombre.isBlank()) { // Si el nombre está vacío
            throw new IllegalArgumentException("El nombre es obligatorio"); // Lanza excepción
        }

        if (apellido == null || apellido.isBlank()) { // Si el apellido está vacío
            throw new IllegalArgumentException("El apellido es obligatorio"); // Lanza excepción
        }

        if (telefono == null || telefono.isBlank()) { // Si el teléfono está vacío
            throw new IllegalArgumentException("El telefono es obligatorio"); // Lanza excepción
        }

        PerfilUsuario perfil = new PerfilUsuario.Builder() // Crea el perfil usando el patrón Builder
                .usuarioId(usuarioId) // Asigna el ID del usuario
                .nombre(nombre) // Asigna el nombre
                .apellido(apellido) // Asigna el apellido
                .telefono(telefono) // Asigna el teléfono
                .build(); // Construye el objeto PerfilUsuario

        return perfilRepositoryPort.guardar(perfil); // Persiste el perfil y lo retorna
    }

    public PerfilUsuario obtenerPerfilPorUsername(String username) { // Método para obtener perfil por username
        return usuarioRepositoryPort.buscarPorUsername(username) // Busca el usuario por username
                .flatMap(usuario -> perfilRepositoryPort.buscarPorUsuarioId(usuario.getId().intValue())) // Luego busca
                                                                                                         // el perfil
                .orElse(null); // Retorna null si no encuentra nada
    }
}