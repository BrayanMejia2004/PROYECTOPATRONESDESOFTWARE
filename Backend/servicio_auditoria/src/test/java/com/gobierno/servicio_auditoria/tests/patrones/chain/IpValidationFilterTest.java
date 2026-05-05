package com.gobierno.servicio_auditoria.tests.patrones.chain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.chain.IpValidationFilter;

class IpValidationFilterTest {
    
    private final IpValidationFilter filter = new IpValidationFilter();
    
    @Test
    @DisplayName("Debe aceptar IPv4 válida")
    void debeAceptarIPv4Valida() {
        Auditoria auditoria = new Auditoria();
        auditoria.setIp_origen("192.168.1.1");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertTrue(resultado);
    }
    
    @Test
    @DisplayName("Debe aceptar IPv6 válida")
    void debeAceptarIPv6Valida() {
        Auditoria auditoria = new Auditoria();
        auditoria.setIp_origen("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertTrue(resultado);
    }
    
    @Test
    @DisplayName("Debe rechazar IP nula")
    void debeRechazarIPNula() {
        Auditoria auditoria = new Auditoria();
        auditoria.setIp_origen(null);
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertFalse(resultado);
    }
    
    @Test
    @DisplayName("Debe rechazar IP vacía")
    void debeRechazarIPVacia() {
        Auditoria auditoria = new Auditoria();
        auditoria.setIp_origen("");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertFalse(resultado);
    }
    
    @Test
    @DisplayName("Debe rechazar IPv4 inválida")
    void debeRechazarIPv4Invalida() {
        Auditoria auditoria = new Auditoria();
        auditoria.setIp_origen("999.999.999.999");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertFalse(resultado);
    }
    
    @Test
    @DisplayName("Debe rechazar IPv4 con letras")
    void debeRechazarIPv4ConLetras() {
        Auditoria auditoria = new Auditoria();
        auditoria.setIp_origen("192.168.1.a");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertFalse(resultado);
    }
}
