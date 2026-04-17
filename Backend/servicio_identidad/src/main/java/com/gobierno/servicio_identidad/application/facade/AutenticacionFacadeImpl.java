package com.gobierno.servicio_identidad.application.facade;

import com.gobierno.servicio_identidad.application.usecases.RegistroUsuarioUseCase;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.AutenticadorPort;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AuditoriaClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.GeneradorJwtAdapter;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.LoginRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.SolicitudAutenticacion;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioResponse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutenticacionFacadeImpl implements AutenticacionFacade { // Implementación del Facade de autenticación

    private final AutenticadorPort autenticadorPort; // Puerto de autenticación
    private final UsuarioRepositorioPort usuarioRepositorioPort; // Puerto de repositorio de usuarios
    private final RegistroUsuarioUseCase registroUsuarioUseCase; // Caso de uso para registro
    private final AutorizacionClient autorizacionClient; // Cliente de autorización
    private final AuditoriaClient auditoriaClient; // Cliente de auditoría
    private final GeneradorJwtAdapter generadorJwtAdapter; // Generador de tokens JWT

    public AutenticacionFacadeImpl( // Constructor con inyección de dependencias
            AutenticadorPort autenticadorPort, // Inyecta el puerto de autenticación
            UsuarioRepositorioPort usuarioRepositorioPort, // Inyecta el puerto de repositorio
            RegistroUsuarioUseCase registroUsuarioUseCase, // Inyecta el caso de uso de registro
            AutorizacionClient autorizacionClient, // Inyecta el cliente de autorización
            AuditoriaClient auditoriaClient, // Inyecta el cliente de auditoría
            GeneradorJwtAdapter generadorJwtAdapter) { // Inyecta el generador de JWT
        this.autenticadorPort = autenticadorPort; // Asigna el puerto de autenticación
        this.usuarioRepositorioPort = usuarioRepositorioPort; // Asigna el repositorio de usuarios
        this.registroUsuarioUseCase = registroUsuarioUseCase; // Asigna el caso de uso de registro
        this.autorizacionClient = autorizacionClient; // Asigna el cliente de autorización
        this.auditoriaClient = auditoriaClient; // Asigna el cliente de auditoría
        this.generadorJwtAdapter = generadorJwtAdapter; // Asigna el generador de JWT
    }

    @Override // Sobrescribe el método de la interfaz
    public String login(LoginRequest request) { // Método para autenticar usuario
        SolicitudAutenticacion solicitud = new SolicitudAutenticacion( // Crea solicitud con credenciales
                request.getUsername(), // Obtiene el username del request
                request.getPassword(), // Obtiene el password del request
                null // No hay token inicialmente
        );
        autenticadorPort.autenticar(solicitud); // Autentica al usuario con las credenciales

        Usuario usuario = usuarioRepositorioPort.buscarPorUsername(request.getUsername()) // Busca el usuario en BD
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Lanza excepción si no existe

        List<String> roles = autorizacionClient.obtenerRolesDeUsuario(request.getUsername()); // Obtiene los roles del
                                                                                              // usuario

        if (roles.isEmpty()) { // Si el usuario no tiene roles
            autorizacionClient.asignarRolAUsuario(request.getUsername(), "USER"); // Asigna rol USER por defecto
            roles = autorizacionClient.obtenerRolesDeUsuario(request.getUsername()); // Vuelve a obtener los roles
        }

        String token = generadorJwtAdapter.generarToken(usuario, roles); // Genera el token JWT con los roles

        auditoriaClient.registrarAuditoria( // Registra la auditoría del login
                usuario.getId().intValue(), // ID del usuario
                "LOGIN", // Acción realizada
                "Usuario " + request.getUsername() + " inici\u00f3 sesi\u00f3n", // Descripción
                "BASICA" // Tipo de auditoría
        );

        return token; // Retorna el token JWT al cliente
    }

    @Override // Sobrescribe el método de la interfaz
    public UsuarioResponse registrarUsuario(RegistroUsuarioRequest request) { // Método para registrar nuevo usuario
        Usuario nuevoUsuario = registroUsuarioUseCase.ejecutar( // Ejecuta el caso de uso de registro
                request.getUsername(), // Pasa el username
                request.getPassword(), // Pasa el password
                request.getEmail()); // Pasa el email

        try { // Try-catch para manejar posibles errores al asignar rol
            autorizacionClient.asignarRolAUsuario(request.getUsername(), "USER"); // Asigna rol USER al nuevo usuario
        } catch (Exception e) { // Si falla la asignación de rol
            System.err.println("No se pudo asignar rol USER: " + e.getMessage()); // Imprime el error (no lanzar
                                                                                  // excepción)
        }

        auditoriaClient.registrarAuditoria( // Registra la auditoría del registro
                nuevoUsuario.getId().intValue(), // ID del nuevo usuario
                "REGISTRO_USUARIO", // Acción realizada
                "Usuario " + request.getUsername() + " se registr\u00f3", // Descripción
                "BASICA" // Tipo de auditoría
        );

        return new UsuarioResponse( // Retorna los datos del usuario registrado
                nuevoUsuario.getId(), // ID del nuevo usuario
                nuevoUsuario.getUsername(), // Username del nuevo usuario
                nuevoUsuario.getEmail()); // Email del nuevo usuario
    }

    @Override // Sobrescribe el método de la interfaz
    public Object validarToken(String token) { // Método para validar un token JWT
        if (token.startsWith("Bearer ")) { // Si el token tiene el prefijo "Bearer "
            token = token.substring(7); // Extrae solo el token (sin "Bearer ")
        }
        SolicitudAutenticacion solicitud = new SolicitudAutenticacion(null, null, token); // Crea solicitud con el token
        return autenticadorPort.autenticar(solicitud); // Autentica usando el token y retorna el resultado
    }
}