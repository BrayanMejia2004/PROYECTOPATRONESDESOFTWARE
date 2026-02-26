package com.gobierno.servicio_identidad.Ports.Output;

import java.util.Optional;
import com.gobierno.servicio_identidad.Domain.Model.Usuario;

public interface UsuarioRepositorio {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorUsername(String username);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorUsername(String username);

    boolean existePorEmail(String email);

}
