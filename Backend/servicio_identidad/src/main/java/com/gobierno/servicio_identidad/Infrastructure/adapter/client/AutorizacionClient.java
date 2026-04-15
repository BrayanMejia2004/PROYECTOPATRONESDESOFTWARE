package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AutorizacionClient {

    private final RestTemplate restTemplate;
    private static final String AUTORIZACION_SERVICE_URL = "http://localhost:8083";

    public AutorizacionClient() {
        this.restTemplate = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    public List<String> obtenerRolesDeUsuario(String username) {
        try {
            String url = AUTORIZACION_SERVICE_URL + "/usuarios/" + username + "/roles";
            List<String> roles = restTemplate.getForObject(url, List.class);
            return roles != null ? roles : List.of();
        } catch (Exception e) {
            System.err.println("Error al obtener roles del usuario: " + e.getMessage());
            return List.of();
        }
    }

    public void asignarRolAUsuario(String username, String tipoRol) {
        try {
            String url = AUTORIZACION_SERVICE_URL + "/usuarios/" + username + "/roles/" + tipoRol;
            restTemplate.postForObject(url, null, String.class);
        } catch (Exception e) {
            System.err.println("Error al asignar rol: " + e.getMessage());
            throw new RuntimeException("Error al asignar rol: " + e.getMessage());
        }
    }

    public void quitarRolAUsuario(String username, String tipoRol) {
        try {
            String url = AUTORIZACION_SERVICE_URL + "/usuarios/" + username + "/roles/" + tipoRol;
            restTemplate.delete(url);
        } catch (Exception e) {
            System.err.println("Error al quitar rol: " + e.getMessage());
            throw new RuntimeException("Error al quitar rol: " + e.getMessage());
        }
    }

    public void quitarTodosLosRolesDeUsuario(String username) {
        try {
            String url = AUTORIZACION_SERVICE_URL + "/usuarios/" + username + "/roles/todos";
            restTemplate.delete(url);
        } catch (Exception e) {
            System.err.println("Error al quitar todos los roles: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> obtenerListaRoles() {
        try {
            String url = AUTORIZACION_SERVICE_URL + "/roles/lista";
            List<String> roles = restTemplate.getForObject(url, List.class);
            return roles != null ? roles : List.of("ADMIN", "USER", "AUDITOR");
        } catch (Exception e) {
            System.err.println("Error al obtener lista de roles: " + e.getMessage());
            return List.of("ADMIN", "USER", "AUDITOR");
        }
    }
}
