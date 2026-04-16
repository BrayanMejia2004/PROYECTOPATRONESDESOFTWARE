package com.gobierno.servicio_reportes.tests.patrones.composite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.gobierno.servicio_reportes.domain.composite.SeccionEncabezado;
import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

class SeccionEncabezadoTest {

    private ReporteData crearDatos(String titulo, String usuario) {
        ReporteData datos = new ReporteData();
        datos.setTitulo(titulo);
        datos.setUsuarioSolicitante(usuario);
        return datos;
    }

    // Genera encabezado con título y usuario, verifica contenido no vacío
    @Test
    void generar_conTituloYUsuario_contenidoNoVacio() {
        SeccionEncabezado seccion = new SeccionEncabezado();
        ReporteData datos = crearDatos("Reporte Test", "Juan");
        byte[] resultado = seccion.generar(datos);
        assertTrue(resultado.length > 0);
    }

    // Verifica que el título aparece en el output
    @Test
    void generar_conTitulo_contieneTituloEnOutput() {
        SeccionEncabezado seccion = new SeccionEncabezado();
        ReporteData datos = crearDatos("Mi Reporte", "SYSTEM");
        String contenido = new String(seccion.generar(datos));
        assertTrue(contenido.contains("Mi Reporte"));
    }

    // Verifica que el usuario aparece en el output
    @Test
    void generar_conUsuario_contieneUsuarioEnOutput() {
        SeccionEncabezado seccion = new SeccionEncabezado();
        ReporteData datos = crearDatos("Test", "Admin");
        String contenido = new String(seccion.generar(datos));
        assertTrue(contenido.contains("Admin"));
    }

    // Verifica que la descripción aparece si existe
    @Test
    void generar_conDescripcion_contieneDescripcion() {
        SeccionEncabezado seccion = new SeccionEncabezado();
        ReporteData datos = crearDatos("Test", "USER");
        datos.setDescripcion("Descripcion del reporte");
        String contenido = new String(seccion.generar(datos));
        assertTrue(contenido.contains("Descripcion del reporte"));
    }

    // Verifica "REPORTE" como valor por defecto cuando título es null
    @Test
    void generar_sinTitulo_utilizaValorPorDefecto() {
        SeccionEncabezado seccion = new SeccionEncabezado();
        ReporteData datos = crearDatos(null, "USER");
        String contenido = new String(seccion.generar(datos));
        assertTrue(contenido.contains("REPORTE"));
    }

    // Verifica "SYSTEM" como valor por defecto cuando usuario es null
    @Test
    void generar_sinUsuario_utilizaSYSTEM() {
        SeccionEncabezado seccion = new SeccionEncabezado();
        ReporteData datos = crearDatos("Test", null);
        String contenido = new String(seccion.generar(datos));
        assertTrue(contenido.contains("SYSTEM"));
    }

    // Verifica uso de separadores visuales "="
    @Test
    void generar_contieneLineasSeparadoras() {
        SeccionEncabezado seccion = new SeccionEncabezado();
        ReporteData datos = crearDatos("Test", "USER");
        String contenido = new String(seccion.generar(datos));
        assertTrue(contenido.contains("="));
    }

    // Verifica identificador "ENCABEZADO"
    @Test
    void getTipo_retornaENCABEZADO() {
        SeccionEncabezado seccion = new SeccionEncabezado();
        assertEquals("ENCABEZADO", seccion.getTipo());
    }

    // Verifica que tiene orden 1 (primera sección)
    @Test
    void getOrden_retorna1() {
        SeccionEncabezado seccion = new SeccionEncabezado();
        assertEquals(1, seccion.getOrden());
    }
}
