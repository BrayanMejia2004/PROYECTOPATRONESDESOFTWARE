package com.gobierno.servicio_auditoria.domain.chain;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public class IpValidationFilter extends AbstractAuditoriaFilter {  // Valida el formato de la IP de origen
    
    @Override
    public boolean doFilter(Auditoria auditoria) {  // Valida que la IP sea correcta
        if (auditoria.getIp_origen() == null || auditoria.getIp_origen().isEmpty()) {
            return false;  // IP no válida
        }
        
        String ip = auditoria.getIp_origen().trim();
        
        if (isValidIPv4(ip)) {  // Valida IPv4
            return doNext(auditoria);  // IP válida, pasa al siguiente filtro
        }
        
        if (isValidIPv6(ip)) {  // Valida IPv6
            return doNext(auditoria);  // IP válida, pasa al siguiente filtro
        }
        
        return false;  // IP no válida
    }
    
    private boolean isValidIPv4(String ip) {  // Valida formato IPv4
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isValidIPv6(String ip) {  // Valida formato IPv6 básico
        if (ip.contains(":")) {
            String[] parts = ip.split(":", -1);
            return parts.length >= 3 && parts.length <= 8;
        }
        return false;
    }
}
