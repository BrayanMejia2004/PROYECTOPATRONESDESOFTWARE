package com.gobierno.servicio_auditoria.domain.chain;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

public class UserExistsFilter extends AbstractAuditoriaFilter {  // Valida que el usuario exista en servicio_identidad
    
    private final RestTemplate restTemplate;  // Cliente REST para consultar servicio_identidad
    private final String servicioIdentidadUrl;  // URL del servicio de identidad
    
    public UserExistsFilter(RestTemplate restTemplate, String servicioIdentidadUrl) {  // Constructor con dependencias
        this.restTemplate = restTemplate;
        this.servicioIdentidadUrl = servicioIdentidadUrl;
    }
    
    @Override
    public boolean doFilter(Auditoria auditoria) {  // Valida que el usuario exista realmente
        if (auditoria.getUsuario_id() == null || auditoria.getUsuario_id() <= 0) {
            return false;  // ID de usuario inválido
        }
        
        try {  // Consulta real al servicio de identidad usando el endpoint existente
            // Convertir Integer a Long para la consulta (el servicio usa Long)
            Long usuarioIdLong = auditoria.getUsuario_id().longValue();
            String url = servicioIdentidadUrl + "/usuarios/" + usuarioIdLong + "/existe";
            
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            Boolean existe = response.getBody();
            
            if (existe == null || !existe) {
                return false;  // Usuario no existe
            }
            
            return doNext(auditoria);  // Usuario existe, pasa al siguiente filtro
            
        } catch (Exception e) {  // Si hay error de conexión o respuesta inválida
            return false;  // No se puede validar, se rechaza
        }
    }
}
