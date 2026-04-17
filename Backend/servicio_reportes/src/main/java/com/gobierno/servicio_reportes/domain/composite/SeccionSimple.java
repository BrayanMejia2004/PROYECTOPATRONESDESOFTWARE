package com.gobierno.servicio_reportes.domain.composite;

public abstract class SeccionSimple implements SeccionComponent {
    
    protected String tipo;
    protected boolean habilitada;
    protected int orden;
    
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
    
    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }
    
    protected String formatearLinea(String texto, int ancho) {
        if (texto == null) texto = "";
        if (texto.length() > ancho) {
            return texto.substring(0, ancho - 3) + "...";
        }
        return texto;
    }
    
    protected String repetirCaracter(char c, int veces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < veces; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
