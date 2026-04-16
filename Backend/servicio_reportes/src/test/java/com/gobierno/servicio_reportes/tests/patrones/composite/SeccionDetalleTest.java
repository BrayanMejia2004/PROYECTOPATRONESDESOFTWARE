package com.gobierno.servicio_reportes.tests.patrones.composite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.gobierno.servicio_reportes.domain.composite.SeccionDetalle;
import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

class SeccionDetalleTest {

    private ReporteData crearDatos(String[] headers, String[][] filas) {
        ReporteData datos = new ReporteData();
        datos.setHeaders(headers);
        datos.setFilas(filas);
        return datos;
    }

    // Genera tabla con headers y filas, verifica contenido no vacío
    @Test
    void generar_conHeadersYFilas_contenidoNoVacio() {
        SeccionDetalle seccion = new SeccionDetalle();
        String[] headers = {"ID", "Nombre"};
        String[][] filas = {{"1", "Juan"}};
        byte[] resultado = seccion.generar(crearDatos(headers, filas));
        assertTrue(resultado.length > 0);
    }

    // Verifica que los headers aparecen en la tabla
    @Test
    void generar_conHeaders_contieneHeadersEnOutput() {
        SeccionDetalle seccion = new SeccionDetalle();
        String[] headers = {"ID", "Nombre", "Email"};
        String[][] filas = {{"1", "Juan", "j@test.com"}};
        String contenido = new String(seccion.generar(crearDatos(headers, filas)));
        assertTrue(contenido.contains("ID"));
        assertTrue(contenido.contains("Nombre"));
        assertTrue(contenido.contains("Email"));
    }

    // Verifica que los datos de las filas aparecen
    @Test
    void generar_conFilas_contieneDatosEnOutput() {
        SeccionDetalle seccion = new SeccionDetalle();
        String[] headers = {"ID", "Nombre"};
        String[][] filas = {{"1", "Juan"}, {"2", "Maria"}};
        String contenido = new String(seccion.generar(crearDatos(headers, filas)));
        assertTrue(contenido.contains("Juan"));
        assertTrue(contenido.contains("Maria"));
    }

    // Verifica mensaje cuando no hay headers
    @Test
    void generar_sinHeaders_muestraMensajeNoHayDatos() {
        SeccionDetalle seccion = new SeccionDetalle();
        String contenido = new String(seccion.generar(crearDatos(null, null)));
        assertTrue(contenido.contains("No hay datos disponibles"));
    }

    // Verifica mensaje cuando no hay filas
    @Test
    void generar_sinFilas_muestraMensajeNoHayRegistros() {
        SeccionDetalle seccion = new SeccionDetalle();
        String[] headers = {"ID", "Nombre"};
        String[][] filas = {};
        String contenido = new String(seccion.generar(crearDatos(headers, filas)));
        assertTrue(contenido.contains("No hay registros para mostrar"));
    }

    // Verifica que muestra el conteo total de registros
    @Test
    void generar_contieneTotalDeRegistros() {
        SeccionDetalle seccion = new SeccionDetalle();
        String[] headers = {"ID"};
        String[][] filas = {{"1"}, {"2"}, {"3"}};
        String contenido = new String(seccion.generar(crearDatos(headers, filas)));
        assertTrue(contenido.contains("Total: 3 registros"));
    }

    // Verifica uso de separadores de tabla
    @Test
    void generar_contieneSeparadoresDeTabla() {
        SeccionDetalle seccion = new SeccionDetalle();
        String[] headers = {"A", "B"};
        String[][] filas = {{"1", "2"}};
        String contenido = new String(seccion.generar(crearDatos(headers, filas)));
        assertTrue(contenido.contains("=") || contenido.contains("|"));
    }

    // Verifica identificador "DETALLE"
    @Test
    void getTipo_retornaDETALLE() {
        SeccionDetalle seccion = new SeccionDetalle();
        assertEquals("DETALLE", seccion.getTipo());
    }

    // Verifica que tiene orden 3
    @Test
    void getOrden_retorna3() {
        SeccionDetalle seccion = new SeccionDetalle();
        assertEquals(3, seccion.getOrden());
    }

    // Verifica que no genera nada si está deshabilitada
    @Test
    void generar_deshabilitado_retornaVacio() {
        SeccionDetalle seccion = new SeccionDetalle();
        seccion.setHabilitada(false);
        byte[] resultado = seccion.generar(crearDatos(new String[]{"A"}, new String[][]{{"1"}}));
        assertEquals(0, resultado.length);
    }
}
