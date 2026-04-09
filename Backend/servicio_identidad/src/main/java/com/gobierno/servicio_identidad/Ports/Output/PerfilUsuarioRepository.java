package com.gobierno.servicio_identidad.Ports.Output;

import java.util.Optional;

import com.gobierno.servicio_identidad.Domain.Model.PerfilUsuario;

public interface PerfilUsuarioRepository {

    PerfilUsuario guardar(PerfilUsuario perfil);

    Optional<PerfilUsuario> buscarPorUsuarioId(Integer usuarioId);

    boolean existePorUsuarioId(Integer usuarioId);

    PerfilUsuario actualizar(PerfilUsuario perfil);
}
