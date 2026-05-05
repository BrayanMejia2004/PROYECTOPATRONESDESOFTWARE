package com.gobierno.servicio_auditoria.tests.patrones.chain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.chain.ActionAllowedFilter;

class ActionAllowedFilterTest {
    
    private final ActionAllowedFilter filter = new ActionAllowedFilter();
    
    @Test
    @DisplayName("Debe aceptar acción LOGIN")
    void debeAceptarAccionLogin() {
        Auditoria auditoria = new Auditoria();
        auditoria.setAccion("LOGIN");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertTrue(resultado);
    }
    
    @Test
    @DisplayName("Debe aceptar acción REGISTRO_USUARIO")
    void debeAceptarAccionRegistroUsuario() {
        Auditoria auditoria = new Auditoria();
        auditoria.setAccion("REGISTRO_USUARIO");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertTrue(resultado);
    }
    
    @Test
    @DisplayName("Debe aceptar acción con método HTTP")
    void debeAceptarAccionConMetodoHTTP() {
        Auditoria auditoria = new Auditoria();
        auditoria.setAccion("POST /auditoria/registrar/BASICA");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertTrue(resultado);
    }
    
    @Test
    @DisplayName("Debe rechazar acción nula")
    void debeRechazarAccionNula() {
        Auditoria auditoria = new Auditoria();
        auditoria.setAccion(null);
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertFalse(resultado);
    }
    
    @Test
    @DisplayName("Debe rechazar acción vacía")
    void debeRechazarAccionVacia() {
        Auditoria auditoria = new Auditoria();
        auditoria.setAccion("");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertFalse(resultado);
    }
    
    @Test
    @DisplayName("Debe rechazar acción no permitida")
    void debeRechazarAccionNoPermitida() {
        Auditoria auditoria = new Auditoria();
        auditoria.setAccion("HACK_SISTEMA");
        
        boolean resultado = filter.doFilter(auditoria);
        
        assertFalse(resultado);
    }
}
