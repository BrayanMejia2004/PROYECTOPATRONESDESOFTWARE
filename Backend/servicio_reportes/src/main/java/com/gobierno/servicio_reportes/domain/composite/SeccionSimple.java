package com.gobierno.servicio_reportes.domain.composite;

/**
 * Clase base abstracta para secciones Leaf del Composite.
 * Implementa comportamiento común (tipo, habilitación, orden).
 */
public abstract class SeccionSimple implements SeccionComponent {
    
    protected String tipo;      // Identificador de la sección
    protected boolean habilitada;  // Si está activa para generar
    protected int orden;           // Posición en el reporte
    
    protected SeccionSimple(String tipo, int orden) {
        this.tipo = tipo;
        this.habilitada = true;
        this.orden = orden;
    }
    
    @Override
    public String getTipo() {
        return tipo;
    }
    
    @Override
    public boolean estaHabilitada() {
        return habilitada;
    }
    
    @Override
    public int getOrden() {
        return orden;
    }
    
    // Activa/desactiva la sección
    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }
    
    // Trunca texto largo y agrega "..." si supera el ancho
    protected String formatearLinea(String texto, int ancho) {
        if (texto == null) texto = "";
        if (texto.length() > ancho) {
            return texto.substring(0, ancho - 3) + "...";
        }
        return texto;
    }
    
    // Genera una cadena de caracteres repetidos
    protected String repetirCaracter(char c, int veces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
