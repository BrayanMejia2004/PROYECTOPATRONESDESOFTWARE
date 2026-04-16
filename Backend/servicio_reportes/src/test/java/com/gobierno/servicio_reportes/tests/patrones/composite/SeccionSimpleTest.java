package com.gobierno.servicio_reportes.tests.patrones.composite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.gobierno.servicio_reportes.domain.composite.SeccionDetalle;

class SeccionSimpleTest {

    private SeccionDetalle crearSeccionPrueba() {
        return new SeccionDetalle();
    }

    // Verifica que getTipo() devuelve el identificador correcto
    @Test
    void getTipo_retornaIdentificadorCorrecto() {
        SeccionDetalle seccion = crearSeccionPrueba();
        assertEquals("DETALLE", seccion.getTipo());
    }

    // Verifica que toda sección nueva está habilitada por defecto
    @Test
    void estaHabilitada_porDefectoEsTrue() {
        SeccionDetalle seccion = crearSeccionPrueba();
        assertTrue(seccion.estaHabilitada());
    }

    // Verifica que getOrden() devuelve el valor correcto para Detalle
    @Test
    void getOrden_retornaValorCorrecto() {
        SeccionDetalle seccion = crearSeccionPrueba();
        assertEquals(3, seccion.getOrden());
    }

    // Verifica que se puede deshabilitar una sección
    @Test
    void setHabilitada_cambiaEstado() {
        SeccionDetalle seccion = crearSeccionPrueba();
        seccion.setHabilitada(false);
        assertFalse(seccion.estaHabilitada());
    }

    // Verifica que se puede volver a habilitar una sección
    @Test
    void setHabilitada_vuelveAEstadoOriginal() {
        SeccionDetalle seccion = crearSeccionPrueba();
        seccion.setHabilitada(false);
        seccion.setHabilitada(true);
        assertTrue(seccion.estaHabilitada());
    }
}
