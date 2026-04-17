package com.gobierno.servicio_identidad.infrastructure.adapter.client;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditoriaClient { // Cliente HTTP para comunicarse con el servicio de auditoría

    private final RestTemplate restTemplate = new RestTemplate(); // Cliente HTTP para hacer peticiones
    private static final String AUDITORIA_SERVICE_URL = "http://localhost:8081"; // URL base del servicio de auditoría

    @Async // Ejecuta este método de forma asíncrona (en un hilo separado)
    public void registrarAuditoria(Integer usuarioId, String accion, String descripcion, String tipo) { // Registra
                                                                                                        // auditoría
        try { // Try-catch para manejar errores de conexión
            Map<String, Object> body = new HashMap<>(); // Crea el cuerpo de la petición
            body.put("usuario_id", usuarioId); // Agrega el ID del usuario
            body.put("accion", accion); // Agrega la acción realizada
            body.put("descripcion", descripcion); // Agrega la descripción

            restTemplate.postForObject( // Hace petición POST al servicio de auditoría
                    AUDITORIA_SERVICE_URL + "/auditoria/registrar/" + tipo, // URL con el tipo de auditoría
                    body, // Cuerpo de la petición
                    String.class // Tipo de respuesta esperado
            );
        } catch (Exception e) { // Si hay algún error
            System.err.println("Error al registrar auditoría: " + e.getMessage()); // Imprime el error (no lanza
                                                                                   // excepción)
        }
    }
}