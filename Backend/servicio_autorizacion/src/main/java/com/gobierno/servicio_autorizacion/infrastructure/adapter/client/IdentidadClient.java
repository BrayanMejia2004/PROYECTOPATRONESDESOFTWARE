package com.gobierno.servicio_autorizacion.infrastructure.adapter.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IdentidadClient { // Cliente HTTP para servicio de identidad

    private final RestTemplate restTemplate = new RestTemplate(); // Cliente HTTP
    private static final String IDENTIDAD_SERVICE_URL = "http://localhost:8082"; // URL del servicio de identidad

    public Integer obtenerIdPorUsername(String username) { // Obtiene el ID de un usuario por su username
        try {
            String url = IDENTIDAD_SERVICE_URL + "/usuarios/" + username + "/id"; // Construye la URL
            return restTemplate.getForObject(url, Integer.class); // Hace petición GET y retorna el ID
        } catch (Exception e) {
            System.err.println("Error al obtener ID del usuario: " + e.getMessage());
            return 0; // Retorna 0 en caso de error
        }
    }
}