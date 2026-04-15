package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuditoriaClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String AUDITORIA_SERVICE_URL = "http://localhost:8081";

    @Async
    public void registrarAuditoria(Integer usuarioId, String accion, String descripcion, String tipo) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("usuario_id", usuarioId);
            body.put("accion", accion);
            body.put("descripcion", descripcion);

            restTemplate.postForObject(
                AUDITORIA_SERVICE_URL + "/auditoria/registrar/" + tipo,
                body,
                String.class
            );
        } catch (Exception e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }
}
