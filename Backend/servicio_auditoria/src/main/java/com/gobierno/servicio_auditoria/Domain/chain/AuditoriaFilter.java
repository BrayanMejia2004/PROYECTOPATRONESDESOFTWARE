package com.gobierno.servicio_auditoria.domain.chain;

import com.gobierno.servicio_auditoria.domain.entities.Auditoria;

public interface AuditoriaFilter {  // Interfaz para el patrón Chain of Responsibility
    
    void setNext(AuditoriaFilter next);  // Establece el siguiente filtro en la cadena
    
    boolean doFilter(Auditoria auditoria);  // Ejecuta el filtro y pasa al siguiente si corresponde
}
