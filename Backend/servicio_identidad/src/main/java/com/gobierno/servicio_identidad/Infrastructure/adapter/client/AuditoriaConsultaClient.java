package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuditoriaConsultaClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String AUDITORIA_SERVICE_URL = "http://localhost:8081";

    public List<Map<String, Object>> obtenerEventosRecientes(Integer usuarioId, int limite) {
        try {
            String url = AUDITORIA_SERVICE_URL + "/auditoria/usuario/" + usuarioId + "/timeline?limite=" + limite;
            var response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al obtener eventos recientes: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
