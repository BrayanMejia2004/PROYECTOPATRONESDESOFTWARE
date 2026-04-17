package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AutorizacionClient { // Cliente HTTP para comunicarse con el servicio de autorización

    private final RestTemplate restTemplate; // Cliente HTTP para hacer peticiones al servicio externo
    private static final String AUTORIZACION_SERVICE_URL = "http://localhost:8083"; // URL base del servicio de
                                                                                    // autorización

    public AutorizacionClient() { // Constructor
        this.restTemplate = new RestTemplate(); // Inicializa el cliente HTTP
    }

    @SuppressWarnings("unchecked") // Suprime warnings de cast genérico
    public List<String> obtenerRolesDeUsuario(String username) { // Obtiene los roles de un usuario
        try { // Try-catch para manejar errores de conexión
            String url = AUTORIZACION_SERVICE_URL + "/usuarios/" + username + "/roles"; // Construye la URL
            List<String> roles = restTemplate.getForObject(url, List.class); // Hace petición GET
            return roles != null ? roles : List.of(); // Retorna roles o lista vacía si es null
        } catch (Exception e) { // Si hay algún error
            System.err.println("Error al obtener roles del usuario: " + e.getMessage()); // Imprime el error
            return List.of(); // Retorna lista vacía como fallback
        }
    }

    public void asignarRolAUsuario(String username, String tipoRol) { // Asigna un rol a un usuario
        try { // Try-catch para manejar errores de conexión
            String url = AUTORIZACION_SERVICE_URL + "/usuarios/" + username + "/roles/" + tipoRol; // Construye la URL
            restTemplate.postForObject(url, null, String.class); // Hace petición POST para asignar rol
        } catch (Exception e) { // Si hay algún error
            System.err.println("Error al asignar rol: " + e.getMessage()); // Imprime el error
            throw new RuntimeException("Error al asignar rol: " + e.getMessage()); // Lanza excepción
        }
    }

    public void quitarRolAUsuario(String username, String tipoRol) { // Quita un rol a un usuario
        try { // Try-catch para manejar errores de conexión
            String url = AUTORIZACION_SERVICE_URL + "/usuarios/" + username + "/roles/" + tipoRol; // Construye la URL
            restTemplate.delete(url); // Hace petición DELETE para quitar rol
        } catch (Exception e) { // Si hay algún error
            System.err.println("Error al quitar rol: " + e.getMessage()); // Imprime el error
            throw new RuntimeException("Error al quitar rol: " + e.getMessage()); // Lanza excepción
        }
    }

    public void quitarTodosLosRolesDeUsuario(String username) { // Quita todos los roles de un usuario
        try { // Try-catch para manejar errores de conexión
            String url = AUTORIZACION_SERVICE_URL + "/usuarios/" + username + "/roles/todos"; // Construye la URL
            restTemplate.delete(url); // Hace petición DELETE para quitar todos los roles
        } catch (Exception e) { // Si hay algún error
            System.err.println("Error al quitar todos los roles: " + e.getMessage()); // Imprime el error (no lanza
                                                                                      // excepción)
        }
    }

    @SuppressWarnings("unchecked") // Suprime warnings de cast genérico
    public List<String> obtenerListaRoles() { // Obtiene la lista de todos los roles disponibles
        try { // Try-catch para manejar errores de conexión
            String url = AUTORIZACION_SERVICE_URL + "/roles/lista"; // Construye la URL
            List<String> roles = restTemplate.getForObject(url, List.class); // Hace petición GET
            return roles != null ? roles : List.of("ADMIN", "USER", "AUDITOR"); // Retorna roles o valores por defecto
        } catch (Exception e) { // Si hay algún error
            System.err.println("Error al obtener lista de roles: " + e.getMessage()); // Imprime el error
            return List.of("ADMIN", "USER", "AUDITOR"); // Retorna valores por defecto como fallback
        }
    }
}