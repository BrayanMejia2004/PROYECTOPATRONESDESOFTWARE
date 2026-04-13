package com.gobierno.servicio_reportes.domain.valueobjects;

public class ReporteData {
    
    private String tipo;
    private String titulo;
    private String[] headers;
    private String[][] filas;
    private String descripcion;
    
    public ReporteData() {}
    
    public ReporteData(String tipo, String titulo, String[] headers, String[][] filas) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.headers = headers;
        this.filas = filas;
    }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String[] getHeaders() { return headers; }
    public void setHeaders(String[] headers) { this.headers = headers; }
    public String[][] getFilas() { return filas; }
    public void setFilas(String[][] filas) { this.filas = filas; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tipo: ").append(tipo).append("\n");
        sb.append("Titulo: ").append(titulo).append("\n");
        if (headers != null) {
            for (String header : headers) sb.append(header).append(",");
            sb.append("\n");
        }
        if (filas != null) {
            for (String[] fila : filas) {
                for (String celda : fila) sb.append(celda).append(",");
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
