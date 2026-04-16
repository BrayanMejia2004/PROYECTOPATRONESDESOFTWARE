package com.gobierno.servicio_reportes.tests.patrones.composite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.gobierno.servicio_reportes.domain.composite.ReporteBuilder;
import com.gobierno.servicio_reportes.domain.composite.ReporteCompuesto;

class ReporteBuilderTest {

    // Verifica que crear() no devuelve null
    @Test
    void crear_retornaBuilderNoNulo() {
        ReporteBuilder builder = ReporteBuilder.crear("Test");
        assertNotNull(builder);
    }

    // Verifica que construir() devuelve un reporte válido
    @Test
    void construir_retornaReporteValido() {
        ReporteCompuesto reporte = ReporteBuilder.crear("Mi Reporte").construir();
        assertNotNull(reporte);
        assertEquals("Mi Reporte", reporte.getNombre());
    }

    // Verifica que agrega la sección ENCABEZADO
    @Test
    void conEncabezado_agregaSeccionEncabezado() {
        ReporteCompuesto reporte = ReporteBuilder.crear("Test")
                .conEncabezado()
                .construir();
        assertTrue(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("ENCABEZADO")));
    }

    // Verifica que agrega la sección DETALLE
    @Test
    void conDetalle_agregaSeccionDetalle() {
        ReporteCompuesto reporte = ReporteBuilder.crear("Test")
                .conDetalle()
                .construir();
        assertTrue(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("DETALLE")));
    }

    // Verifica que agrega la sección RESUMEN
    @Test
    void conResumen_agregaSeccionResumen() {
        ReporteCompuesto reporte = ReporteBuilder.crear("Test")
                .conResumen()
                .construir();
        assertTrue(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("RESUMEN")));
    }

    // Verifica que agrega la sección PIE
    @Test
    void conPie_agregaSeccionPie() {
        ReporteCompuesto reporte = ReporteBuilder.crear("Test")
                .conPie()
                .construir();
        assertTrue(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("PIE")));
    }

    // Verifica que sin*() elimina secciones
    @Test
    void sinEncabezado_quitaSeccionEncabezado() {
        ReporteCompuesto reporte = ReporteBuilder.crear("Test")
                .conEncabezado()
                .conDetalle()
                .sinEncabezado()
                .construir();
        assertFalse(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("ENCABEZADO")));
    }

    // Verifica encadenamiento de métodos (fluent interface)
    @Test
    void encadenarMetodos_fluentInterface() {
        ReporteCompuesto reporte = ReporteBuilder.crear("Test")
                .conEncabezado()
                .conResumen()
                .conDetalle()
                .conPie()
                .construir();
        assertEquals(4, reporte.getTotalSecciones());
    }

    // Verifica fábrica: reporte con Encabezado + Detalle + Pie
    @Test
    void reporteEstandar_contieneEncabezadoDetallePie() {
        ReporteCompuesto reporte = ReporteBuilder.reporteEstandar();
        assertTrue(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("ENCABEZADO")));
        assertTrue(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("DETALLE")));
        assertTrue(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("PIE")));
        assertEquals(3, reporte.getTotalSecciones());
    }

    // Verifica fábrica completa con todas las secciones
    @Test
    void reporteConResumen_contieneTodasLasSecciones() {
        ReporteCompuesto reporte = ReporteBuilder.reporteConResumen();
        assertTrue(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("RESUMEN")));
        assertEquals(4, reporte.getTotalSecciones());
    }

    // Verifica fábrica mínima (solo Detalle)
    @Test
    void reporteSimple_contieneSoloDetalle() {
        ReporteCompuesto reporte = ReporteBuilder.reporteSimple();
        assertTrue(reporte.getSecciones().stream()
                .anyMatch(s -> s.getTipo().equals("DETALLE")));
        assertEquals(1, reporte.getTotalSecciones());
    }

    // Verifica orden correcto: Encabezado → Detalle → Pie
    @Test
    void reporteEstandar_ordenCorrecto() {
        ReporteCompuesto reporte = ReporteBuilder.reporteEstandar();
        assertEquals("ENCABEZADO", reporte.getSecciones().get(0).getTipo());
        assertEquals("DETALLE", reporte.getSecciones().get(1).getTipo());
        assertEquals("PIE", reporte.getSecciones().get(2).getTipo());
    }

    // Verifica poder quitar y volver a agregar la misma sección
    @Test
    void quitarYAgregar_mismaSeccion_funciona() {
        ReporteCompuesto reporte = ReporteBuilder.crear("Test")
                .conEncabezado()
                .sinEncabezado()
                .conEncabezado()
                .construir();
        assertEquals(1, reporte.getTotalSecciones());
    }

    // Verifica robustez al quitar sección inexistente
    @Test
    void quitarSeccionNoExistente_noFalla() {
        ReporteCompuesto reporte = ReporteBuilder.crear("Test")
                .sinEncabezado()
                .sinDetalle()
                .construir();
        assertEquals(0, reporte.getTotalSecciones());
    }
}
