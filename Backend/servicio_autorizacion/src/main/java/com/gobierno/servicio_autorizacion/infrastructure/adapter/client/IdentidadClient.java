package com.gobierno.servicio_autorizacion.infrastructure.adapter.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IdentidadClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String IDENTIDAD_SERVICE_URL = "http://localhost:8082";

    public Integer obtenerIdPorUsername(String username) {
        try {
            String url = IDENTIDAD_SERVICE_URL + "/usuarios/" + username + "/id";
            return restTemplate.getForObject(url, Integer.class);
        } catch (Exception e) {
            System.err.println("Error al obtener ID del usuario: " + e.getMessage());
            return 0;
        }
    }
}
