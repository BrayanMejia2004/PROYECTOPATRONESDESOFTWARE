package com.gobierno.servicio_reportes.domain.composite;

/**
 * Builder para construcción fluida de ReporteCompuesto.
 * Permite agregar/quitar secciones con métodos encadenados.
 */
public class ReporteBuilder {
    
    private final ReporteCompuesto reporte;
    
    private ReporteBuilder(String nombre) {
        this.reporte = new ReporteCompuesto(nombre);
    }
    
    // Punto de entrada del builder
    public static ReporteBuilder crear(String nombre) {
        return new ReporteBuilder(nombre);
    }
    
    public ReporteBuilder conEncabezado() {
        reporte.agregarSeccion(new SeccionEncabezado());
        return this;
    }
    
    public ReporteBuilder sinEncabezado() {
        reporte.quitarSeccionPorTipo("ENCABEZADO");
        return this;
    }
    
    public ReporteBuilder conResumen() {
        reporte.agregarSeccion(new SeccionResumen());
        return this;
    }
    
    public ReporteBuilder sinResumen() {
        reporte.quitarSeccionPorTipo("RESUMEN");
        return this;
    }
    
    public ReporteBuilder conDetalle() {
        reporte.agregarSeccion(new SeccionDetalle());
        return this;
    }
    
    public ReporteBuilder sinDetalle() {
        reporte.quitarSeccionPorTipo("DETALLE");
        return this;
    }
    
    public ReporteBuilder conPie() {
        reporte.agregarSeccion(new SeccionPie());
        return this;
    }
    
    public ReporteBuilder sinPie() {
        reporte.quitarSeccionPorTipo("PIE");
        return this;
    }
    
    // Agrega sección personalizada
    public ReporteBuilder agregarSeccion(SeccionComponent seccion) {
        reporte.agregarSeccion(seccion);
        return this;
    }
    
    // Finaliza construcción
    public ReporteCompuesto construir() {
        return reporte;
    }
    
    // Fábrica: reporte con encabezado, detalle y pie
    public static ReporteCompuesto reporteEstandar() {
        return crear("Reporte Estandar")
                .conEncabezado()
                .conDetalle()
                .conPie()
                .construir();
    }
    
    // Fábrica: reporte completo con resumen estadístico
    public static ReporteCompuesto reporteConResumen() {
        return crear("Reporte con Resumen")
                .conEncabezado()
                .conResumen()
                .conDetalle()
                .conPie()
                .construir();
    }
    
    // Fábrica: solo detalle sin adornos
    public static ReporteCompuesto reporteSimple() {
        return crear("Reporte Simple")
                .conDetalle()
                .construir();
    }
}
