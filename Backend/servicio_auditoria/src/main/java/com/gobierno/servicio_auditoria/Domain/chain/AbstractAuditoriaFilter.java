package com.gobierno.servicio_auditoria.domain.chain;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public abstract class AbstractAuditoriaFilter implements AuditoriaFilter {  // Clase abstracta base para filtros
    
    protected AuditoriaFilter next;  // Siguiente filtro en la cadena
    
    public void setNext(AuditoriaFilter next) {  // Asigna el siguiente filtro
        this.next = next;
    }
    
    protected boolean doNext(Auditoria auditoria) {  // Ejecuta el siguiente filtro si existe
        if (next != null) {
            return next.doFilter(auditoria);
        }
        return true;  // Si no hay siguiente, se considera válido
    }
}
