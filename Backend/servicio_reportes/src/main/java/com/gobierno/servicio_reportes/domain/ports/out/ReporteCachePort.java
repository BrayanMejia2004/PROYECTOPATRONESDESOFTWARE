package com.gobierno.servicio_reportes.domain.ports.out;

public interface ReporteCachePort {
    
    void guardar(String clave, byte[] contenido);
    
    byte[] obtener(String clave);
    
    void eliminar(String clave);
    
    void limpiar();
    
    int tamaño();
}