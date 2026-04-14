package com.gobierno.servicio_identidad.domain.ports.out;

import java.util.Optional;
import com.gobierno.servicio_identidad.domain.entities.Usuario;

public interface UsuarioRepositorioPort {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorUsername(String username);

    boolean existePorEmail(String email);

    void eliminarPorUsername(String username);
}
