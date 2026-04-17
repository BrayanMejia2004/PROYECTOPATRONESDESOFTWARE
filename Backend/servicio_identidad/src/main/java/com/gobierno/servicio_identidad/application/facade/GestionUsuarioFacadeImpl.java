package com.gobierno.servicio_identidad.application.facade;

import com.gobierno.servicio_identidad.application.usecases.ActualizarPerfilUseCase;
import com.gobierno.servicio_identidad.application.usecases.ActualizarUsuarioUseCase;
import com.gobierno.servicio_identidad.application.usecases.EliminarUsuarioUseCase;
import com.gobierno.servicio_identidad.application.usecases.RegistrarPerfilUseCase;
import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AuditoriaClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.PerfilResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioCompletoResponse;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioListaResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GestionUsuarioFacadeImpl implements GestionUsuarioFacade { // Implementación del Facade de gestión de
                                                                        // usuarios

    private final UsuarioRepositorioPort usuarioRepositorioPort; // Puerto de repositorio de usuarios
    private final RegistrarPerfilUseCase registrarPerfilUseCase; // Caso de uso para registrar perfil
    private final ActualizarPerfilUseCase actualizarPerfilUseCase; // Caso de uso para actualizar perfil
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase; // Caso de uso para actualizar usuario
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase; // Caso de uso para eliminar usuario
    private final AutorizacionClient autorizacionClient; // Cliente de autorización
    private final AuditoriaClient auditoriaClient; // Cliente de auditoría
    private final PasswordEncoder passwordEncoder; // Encriptador de contraseñas

    public GestionUsuarioFacadeImpl( // Constructor con inyección de dependencias
            UsuarioRepositorioPort usuarioRepositorioPort, // Inyecta el repositorio de usuarios
            RegistrarPerfilUseCase registrarPerfilUseCase, // Inyecta el caso de uso de registro de perfil
            ActualizarPerfilUseCase actualizarPerfilUseCase, // Inyecta el caso de uso de actualización de perfil
            ActualizarUsuarioUseCase actualizarUsuarioUseCase, // Inyecta el caso de uso de actualización de usuario
            EliminarUsuarioUseCase eliminarUsuarioUseCase, // Inyecta el caso de uso de eliminación de usuario
            AutorizacionClient autorizacionClient, // Inyecta el cliente de autorización
            AuditoriaClient auditoriaClient, // Inyecta el cliente de auditoría
            PasswordEncoder passwordEncoder) { // Inyecta el encriptador de contraseñas
        this.usuarioRepositorioPort = usuarioRepositorioPort; // Asigna el repositorio
        this.registrarPerfilUseCase = registrarPerfilUseCase; // Asigna el caso de uso de registro de perfil
        this.actualizarPerfilUseCase = actualizarPerfilUseCase; // Asigna el caso de uso de actualización de perfil
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase; // Asigna el caso de uso de actualización de usuario
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase; // Asigna el caso de uso de eliminación
        this.autorizacionClient = autorizacionClient; // Asigna el cliente de autorización
        this.auditoriaClient = auditoriaClient; // Asigna el cliente de auditoría
        this.passwordEncoder = passwordEncoder; // Asigna el encriptador
    }

    @Override // Sobrescribe el método de la interfaz
    public UsuarioCompletoResponse obtenerUsuarioCompleto(String username) { // Obtiene información completa del usuario
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null); // Busca el usuario en BD
        if (usuario == null) { // Si el usuario no existe
            return null; // Retorna null
        }

        List<String> roles = autorizacionClient.obtenerRolesDeUsuario(username); // Obtiene los roles del usuario
        PerfilUsuario perfil = registrarPerfilUseCase.obtenerPerfilPorUsername(username); // Obtiene el perfil del
                                                                                          // usuario

        UsuarioCompletoResponse.PerfilInfo perfilInfo = null; // Inicializa la información del perfil como null
        if (perfil != null) { // Si el usuario tiene perfil
            perfilInfo = new UsuarioCompletoResponse.PerfilInfo( // Crea el objeto de información del perfil
                    perfil.getNombre(), // Obtiene el nombre del perfil
                    perfil.getApellido(), // Obtiene el apellido del perfil
                    perfil.getTelefono(), // Obtiene el teléfono del perfil
                    usuario.getEmail() // Obtiene el email del usuario
            );
        }

        return new UsuarioCompletoResponse( // Retorna la respuesta completa del usuario
                usuario.getId(), // ID del usuario
                usuario.getUsername(), // Username del usuario
                usuario.getEmail(), // Email del usuario
                roles, // Lista de roles
                perfilInfo); // Información del perfil
    }

    @Override // Sobrescribe el método de la interfaz
    public PerfilResponse obtenerPerfil(String username) { // Obtiene el perfil de un usuario
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null); // Busca el usuario en BD
        if (usuario == null) { // Si el usuario no existe
            return null; // Retorna null
        }

        PerfilUsuario perfil = registrarPerfilUseCase.obtenerPerfilPorUsername(username); // Obtiene el perfil del
                                                                                          // usuario
        if (perfil == null) { // Si el usuario no tiene perfil
            return null; // Retorna null
        }

        return new PerfilResponse( // Retorna la respuesta del perfil
                perfil.getNombre(), // Nombre del perfil
                perfil.getApellido(), // Apellido del perfil
                perfil.getTelefono(), // Teléfono del perfil
                usuario.getEmail()); // Email del usuario
    }

    @Override // Sobrescribe el método de la interfaz
    public PerfilResponse registrarPerfil(String username, PerfilRequest request) { // Registra un nuevo perfil
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null); // Busca el usuario en BD
        if (usuario == null) { // Si el usuario no existe
            throw new RuntimeException("Usuario no encontrado"); // Lanza excepción
        }

        PerfilUsuario perfil = registrarPerfilUseCase.ejecutar( // Ejecuta el caso de uso de registro de perfil
                usuario.getId().intValue(), // Pasa el ID del usuario
                request.getNombre(), // Pasa el nombre del request
                request.getApellido(), // Pasa el apellido del request
                request.getTelefono()); // Pasa el teléfono del request

        auditoriaClient.registrarAuditoria( // Registra la auditoría del registro de perfil
                usuario.getId().intValue(), // ID del usuario
                "REGISTRAR_PERFIL", // Acción realizada
                "Usuario " + username + " cre\u00f3 su perfil", // Descripción
                "COMPLETA" // Tipo de auditoría
        );

        return new PerfilResponse( // Retorna la respuesta del perfil registrado
                perfil.getNombre(), // Nombre del perfil
                perfil.getApellido(), // Apellido del perfil
                perfil.getTelefono(), // Teléfono del perfil
                usuario.getEmail()); // Email del usuario
    }

    @Override // Sobrescribe el método de la interfaz
    public PerfilResponse actualizarPerfil(String username, PerfilRequest request) { // Actualiza el perfil de un
                                                                                     // usuario
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null); // Busca el usuario en BD
        if (usuario == null) { // Si el usuario no existe
            throw new RuntimeException("Usuario no encontrado"); // Lanza excepción
        }

        PerfilUsuario perfil = actualizarPerfilUseCase.ejecutar( // Ejecuta el caso de uso de actualización de perfil
                usuario.getId().intValue(), // Pasa el ID del usuario
                request.getNombre(), // Pasa el nombre del request
                request.getApellido(), // Pasa el apellido del request
                request.getTelefono()); // Pasa el teléfono del request

        auditoriaClient.registrarAuditoria( // Registra la auditoría de la actualización de perfil
                usuario.getId().intValue(), // ID del usuario
                "ACTUALIZAR_PERFIL", // Acción realizada
                "Usuario " + username + " actualiz\u00f3 su perfil", // Descripción
                "COMPLETA" // Tipo de auditoría
        );

        return new PerfilResponse( // Retorna la respuesta del perfil actualizado
                perfil.getNombre(), // Nombre del perfil
                perfil.getApellido(), // Apellido del perfil
                perfil.getTelefono(), // Teléfono del perfil
                usuario.getEmail()); // Email del usuario
    }

    @Override // Sobrescribe el método de la interfaz
    public Usuario actualizarUsuario(String username, String email, String password) { // Actualiza datos del usuario
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null); // Busca el usuario en BD

        Usuario usuarioActualizado = actualizarUsuarioUseCase.ejecutar( // Ejecuta el caso de uso de actualización de
                                                                        // usuario
                username, // Pasa el username
                email, // Pasa el nuevo email (puede ser null)
                password); // Pasa la nueva password (puede ser null)

        auditoriaClient.registrarAuditoria( // Registra la auditoría de la actualización de usuario
                usuario.getId().intValue(), // ID del usuario
                "ACTUALIZAR_USUARIO", // Acción realizada
                "Usuario " + username + " actualiz\u00f3 sus datos", // Descripción
                "BASICA" // Tipo de auditoría
        );

        return usuarioActualizado; // Retorna el usuario actualizado
    }

    @Override // Sobrescribe el método de la interfaz
    public void eliminarUsuario(String username) { // Elimina la cuenta del propio usuario
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null); // Busca el usuario en BD
        if (usuario != null) { // Si el usuario existe
            auditoriaClient.registrarAuditoria( // Registra la auditoría de eliminación
                    usuario.getId().intValue(), // ID del usuario
                    "ELIMINAR_USUARIO", // Acción realizada
                    "Usuario " + username + " elimin\u00f3 su cuenta", // Descripción
                    "SEGURIDAD" // Tipo de auditoría
            );
        }

        eliminarUsuarioUseCase.ejecutar(username); // Ejecuta el caso de uso de eliminación de usuario
    }

    @Override // Sobrescribe el método de la interfaz
    public void eliminarUsuarioAdmin(String adminUsername, String username) { // Admin elimina un usuario
        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null); // Busca el usuario a
                                                                                           // eliminar
        Integer usuarioId = (usuario != null) ? usuario.getId().intValue() : 0; // Obtiene el ID del usuario o 0 si no
                                                                                // existe

        auditoriaClient.registrarAuditoria( // Registra la auditoría de eliminación por admin
                usuarioId, // ID del usuario eliminado
                "ELIMINAR_USUARIO", // Acción realizada
                "Admin " + adminUsername + " elimin\u00f3 usuario " + username, // Descripción
                "SEGURIDAD" // Tipo de auditoría
        );

        autorizacionClient.quitarTodosLosRolesDeUsuario(username); // Quita todos los roles del usuario
        eliminarUsuarioUseCase.ejecutar(username); // Elimina el usuario de la base de datos
    }

    @Override // Sobrescribe el método de la interfaz
    public void asignarRol(String username, String tipoRol) { // Asigna un rol a un usuario
        autorizacionClient.asignarRolAUsuario(username, tipoRol); // Llama al servicio de autorización
    }

    @Override // Sobrescribe el método de la interfaz
    public void quitarRol(String username, String tipoRol) { // Quita un rol a un usuario
        autorizacionClient.quitarRolAUsuario(username, tipoRol); // Llama al servicio de autorización
    }

    @Override // Sobrescribe el método de la interfaz
    public List<String> obtenerRoles(String username) { // Obtiene los roles de un usuario
        return autorizacionClient.obtenerRolesDeUsuario(username); // Retorna los roles del servicio de autorización
    }

    @Override // Sobrescribe el método de la interfaz
    public List<String> listarRolesDisponibles() { // Lista los roles disponibles en el sistema
        return autorizacionClient.obtenerListaRoles(); // Retorna la lista de roles del servicio de autorización
    }

    @Override // Sobrescribe el método de la interfaz
    public UsuarioListaResponse actualizarUsuarioAdmin(String adminUsername, String username, // Admin actualiza usuario
            String nuevoUsername, String email, String password) {
        if (adminUsername.equals(username)) { // Si el admin intenta editándose a sí mismo
            throw new RuntimeException("No puedes editarte a ti mismo"); // Lanza excepción
        }

        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(username).orElse(null); // Busca el usuario a editar
        if (usuario == null) { // Si el usuario no existe
            throw new RuntimeException("Usuario no encontrado"); // Lanza excepción
        }

        if (nuevoUsername != null && !nuevoUsername.isEmpty()) { // Si se proporciona un nuevo username
            String nombreActualizado = nuevoUsername; // Asigna el nuevo username
            if (!nombreActualizado.equals(username) && usuarioRepositorioPort.existePorUsername(nombreActualizado)) { // Si
                                                                                                                      // el
                                                                                                                      // nombre
                                                                                                                      // ya
                                                                                                                      // existe
                throw new RuntimeException("Ya existe un usuario con el nombre: " + nombreActualizado); // Lanza
                                                                                                        // excepción
            }
            usuario.setUsername(nombreActualizado); // Actualiza el username
        }

        if (email != null) { // Si se proporciona un nuevo email
            usuario.actualizarEmail(email); // Actualiza el email del usuario
        }

        if (password != null && !password.isEmpty()) { // Si se proporciona una nueva password
            usuario.actualizarPassword(passwordEncoder.encode(password)); // Encripta y actualiza la password
        }

        usuarioRepositorioPort.guardar(usuario); // Persiste los cambios en la base de datos

        return new UsuarioListaResponse(usuario.getId(), usuario.getUsername(), usuario.getEmail()); // Retorna los
                                                                                                     // datos
                                                                                                     // actualizados
    }
}