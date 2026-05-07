package com.gobierno.servicio_auditoria.infrastructure.adapter.client;

import com.gobierno.servicio_auditoria.domain.ports.out.IdentidadClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IdentidadClient implements IdentidadClientPort {

    private static final Logger log = LoggerFactory.getLogger(IdentidadClient.class);

    private final RestTemplate restTemplate;
    private final String servicioIdentidadUrl;

    public IdentidadClient(RestTemplate restTemplate,
                           @Value("${servicio.identidad.url:http://localhost:8082}") String servicioIdentidadUrl) {
        this.restTemplate = restTemplate;
        this.servicioIdentidadUrl = servicioIdentidadUrl;
    }

    @Override
    public List<Long> obtenerTodosLosUsuarioIds() {
        String url = servicioIdentidadUrl + "/usuarios/lista?page=0&size=10000";
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("content")) {
                log.warn("Respuesta inesperada de identidad: no contiene 'content'");
                return new ArrayList<>();
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) body.get("content");
            List<Long> ids = new ArrayList<>();
            for (Map<String, Object> item : content) {
                Object idObj = item.get("id");
                if (idObj instanceof Number) {
                    ids.add(((Number) idObj).longValue());
                }
            }
            return ids;
        } catch (Exception e) {
            log.warn("Error al obtener usuarios desde identidad: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Map<Long, String> obtenerMapaUsuarios() {
        String url = servicioIdentidadUrl + "/usuarios/lista?page=0&size=10000";
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});
            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("content")) {
                log.warn("Respuesta inesperada de identidad: no contiene 'content'");
                return new HashMap<>();
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) body.get("content");
            Map<Long, String> mapa = new HashMap<>();
            for (Map<String, Object> item : content) {
                Object idObj = item.get("id");
                Object usernameObj = item.get("username");
                if (idObj instanceof Number && usernameObj instanceof String) {
                    mapa.put(((Number) idObj).longValue(), (String) usernameObj);
                }
            }
            return mapa;
        } catch (Exception e) {
            log.warn("Error al obtener mapa de usuarios desde identidad: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
