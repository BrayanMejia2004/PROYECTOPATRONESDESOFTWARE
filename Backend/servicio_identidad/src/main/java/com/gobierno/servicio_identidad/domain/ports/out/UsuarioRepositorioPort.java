package com.gobierno.servicio_identidad.domain.ports.out;

import java.util.Optional;
import com.gobierno.servicio_identidad.domain.entities.Usuario;

public interface UsuarioRepositorioPort { // Interfaz que define el contrato para repositorio de usuarios

    Usuario guardar(Usuario usuario); // Persiste un usuario y retorna el usuario guardado con su ID

    Optional<Usuario> buscarPorId(Long id); // Busca un usuario por su ID, retorna Optional

    Optional<Usuario> buscarPorUsername(String username); // Busca un usuario por username, retorna Optional

    Optional<Usuario> buscarPorEmail(String email); // Busca un usuario por email, retorna Optional

    boolean existePorUsername(String username); // Verifica si existe un usuario con el username dado

    boolean existePorEmail(String email); // Verifica si existe un usuario con el email dado

    void eliminarPorUsername(String username); // Elimina un usuario por su username
}