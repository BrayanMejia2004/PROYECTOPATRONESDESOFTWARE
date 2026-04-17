package com.gobierno.servicio_identidad.domain.ports.out;

import java.util.Optional;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;

public interface PerfilUsuarioRepositoryPort { // Interfaz que define el contrato para repositorio de perfiles

    PerfilUsuario guardar(PerfilUsuario perfil); // Persiste un perfil y retorna el perfil guardado

    Optional<PerfilUsuario> buscarPorUsuarioId(Integer usuarioId); // Busca perfil por ID de usuario

    boolean existePorUsuarioId(Integer usuarioId); // Verifica si existe un perfil para el usuario

    PerfilUsuario actualizar(PerfilUsuario perfil); // Actualiza un perfil existente
}