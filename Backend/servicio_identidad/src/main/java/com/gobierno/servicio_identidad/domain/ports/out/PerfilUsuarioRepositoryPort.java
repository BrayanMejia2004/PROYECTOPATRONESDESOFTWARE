package com.gobierno.servicio_identidad.domain.ports.out;

import java.util.Optional;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;

public interface PerfilUsuarioRepositoryPort {

    PerfilUsuario guardar(PerfilUsuario perfil);

    Optional<PerfilUsuario> buscarPorUsuarioId(Integer usuarioId);

    boolean existePorUsuarioId(Integer usuarioId);

    PerfilUsuario actualizar(PerfilUsuario perfil);
}
