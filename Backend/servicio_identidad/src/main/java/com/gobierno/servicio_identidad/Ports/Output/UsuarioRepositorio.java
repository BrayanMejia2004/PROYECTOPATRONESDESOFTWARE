package com.gobierno.servicio_identidad.Ports.Output;

import java.util.Optional;
import com.gobierno.servicio_identidad.Domain.Model.Usuario;

// Interfaz del repositorio de usuarios para la persistencia de datos
public interface UsuarioRepositorio {

    // Método para guardar un usuario en la base de datos
    Usuario guardar(Usuario usuario);

    // Método para buscar un usuario por su ID
    Optional<Usuario> buscarPorUsername(String username);

    // Método para buscar un usuario por su email
    Optional<Usuario> buscarPorEmail(String email);

    // Método para verificar si un usuario existe por su username
    boolean existePorUsername(String username);

    // Método para verificar si un usuario existe por su email
    boolean existePorEmail(String email);

    // Método para eliminar un usuario por su username
    void eliminarPorUsername(String username);

}
