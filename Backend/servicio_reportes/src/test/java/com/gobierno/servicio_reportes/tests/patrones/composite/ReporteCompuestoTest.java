package com.gobierno.servicio_reportes.tests.patrones.composite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gobierno.servicio_reportes.domain.composite.ReporteCompuesto;
import com.gobierno.servicio_reportes.domain.composite.SeccionDetalle;
import com.gobierno.servicio_reportes.domain.composite.SeccionEncabezado;
import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

class ReporteCompuestoTest {

    private ReporteData datosPrueba;

    @BeforeEach
    void setUp() {
        datosPrueba = new ReporteData();
        datosPrueba.setTitulo("Test");
        datosPrueba.setHeaders(new String[]{"Col1", "Col2"});
        datosPrueba.setFilas(new String[][]{{"A", "B"}});
    }

    // Verifica creación con nombre y sin secciones
    @Test
    void constructor_inicializaConNombreYListaVacia() {
        ReporteCompuesto reporte = new ReporteCompuesto("Mi Reporte");
        assertEquals("Mi Reporte", reporte.getNombre());
        assertEquals(0, reporte.getTotalSecciones());
    }

    // Verifica que agregar una sección aumenta el total
    @Test
    void agregarSeccion_aumentaContador() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        reporte.agregarSeccion(new SeccionEncabezado());
        assertEquals(1, reporte.getTotalSecciones());
    }

    // Verifica que agregar varias secciones aumenta el total
    @Test
    void agregarSeccion_multiple_aumentaContador() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        reporte.agregarSeccion(new SeccionEncabezado());
        reporte.agregarSeccion(new SeccionDetalle());
        assertEquals(2, reporte.getTotalSecciones());
    }

    // Verifica que las secciones se reordenan automáticamente por orden
    @Test
    void agregarSeccion_reordenaAutomaticamente() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        reporte.agregarSeccion(new SeccionDetalle());
        reporte.agregarSeccion(new SeccionEncabezado());
        assertEquals("ENCABEZADO", reporte.getSecciones().get(0).getTipo());
    }

    // Verifica que quitar una sección reduce el total
    @Test
    void quitarSeccion_disminuyeContador() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        SeccionEncabezado encabezado = new SeccionEncabezado();
        reporte.agregarSeccion(encabezado);
        reporte.quitarSeccion(encabezado);
        assertEquals(0, reporte.getTotalSecciones());
    }

    // Verifica eliminación de secciones por tipo
    @Test
    void quitarSeccionPorTipo_eliminaCorrectamente() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        reporte.agregarSeccion(new SeccionEncabezado());
        reporte.agregarSeccion(new SeccionDetalle());
        reporte.quitarSeccionPorTipo("ENCABEZADO");
        assertEquals(1, reporte.getTotalSecciones());
        assertEquals("DETALLE", reporte.getSecciones().get(0).getTipo());
    }

    // Verifica que concatena el contenido de secciones habilitadas en orden
    @Test
    void generar_conSeccionesHabilitadas_concatenaEnOrden() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        reporte.agregarSeccion(new SeccionEncabezado());
        reporte.agregarSeccion(new SeccionDetalle());
        byte[] resultado = reporte.generar(datosPrueba);
        assertTrue(resultado.length > 0);
        String contenido = new String(resultado);
        assertTrue(contenido.contains("="));
        assertTrue(contenido.contains("DETALLE"));
    }

    // Verifica que ignora secciones deshabilitadas al generar
    @Test
    void generar_conSeccionDeshabilitada_laOmite() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        reporte.agregarSeccion(new SeccionEncabezado());
        SeccionDetalle detalle = new SeccionDetalle();
        detalle.setHabilitada(false);
        reporte.agregarSeccion(detalle);
        String contenido = new String(reporte.generar(datosPrueba));
        assertTrue(contenido.contains("="));
        assertFalse(contenido.contains("DETALLE DE REGISTROS"));
    }

    // Verifica retorno vacío cuando no hay secciones
    @Test
    void generar_sinSecciones_retornaArregloVacio() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        byte[] resultado = reporte.generar(datosPrueba);
        assertEquals(0, resultado.length);
    }

    // Verifica conteo solo de secciones habilitadas
    @Test
    void getTotalSeccionesHabilitadas_cuentaSoloHabilitadas() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        reporte.agregarSeccion(new SeccionEncabezado());
        SeccionDetalle detalle = new SeccionDetalle();
        detalle.setHabilitada(false);
        reporte.agregarSeccion(detalle);
        assertEquals(1, reporte.getTotalSeccionesHabilitadas());
    }

    // Verifica identificador del Composite
    @Test
    void getTipo_retornaREPORTE_COMPUESTO() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        assertEquals("REPORTE_COMPUESTO", reporte.getTipo());
    }

    // Verifica que el Composite siempre está habilitado
    @Test
    void estaHabilitada_siempreRetornaTrue() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        assertTrue(reporte.estaHabilitada());
    }

    // Verifica orden 0 para el Composite
    @Test
    void getOrden_retorna0() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        assertEquals(0, reporte.getOrden());
    }

    // Verifica que la lista devuelta es una copia (inmutable internamente)
    @Test
    void getSecciones_retornaCopiaDefensiva() {
        ReporteCompuesto reporte = new ReporteCompuesto("Test");
        reporte.agregarSeccion(new SeccionEncabezado());
        reporte.getSecciones().clear();
        assertEquals(1, reporte.getTotalSecciones());
    }
}
