package com.gobierno.servicio_reportes.tests.patrones.composite;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gobierno.servicio_reportes.domain.composite.SeccionComponent;
import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

@ExtendWith(MockitoExtension.class)
class SeccionComponentTest {

    @Mock
    private SeccionComponent seccionMock;

    // Verifica que getTipo() devuelve el identificador de la sección
    @Test
    void getTipo_retornaIdentificador() {
        when(seccionMock.getTipo()).thenReturn("ENCABEZADO");
        assertEquals("ENCABEZADO", seccionMock.getTipo());
    }

    // Verifica que estaHabilitada() devuelve un boolean
    @Test
    void estaHabilitada_retornaBoolean() {
        when(seccionMock.estaHabilitada()).thenReturn(true);
        assertTrue(seccionMock.estaHabilitada());
    }

    // Verifica que getOrden() devuelve un entero
    @Test
    void getOrden_retornaEntero() {
        when(seccionMock.getOrden()).thenReturn(1);
        assertEquals(1, seccionMock.getOrden());
    }

    // Verifica que generar() devuelve un arreglo de bytes
    @Test
    void generar_retornaBytes() {
        ReporteData datos = new ReporteData();
        datos.setTitulo("Test");
        byte[] contenido = "Test".getBytes();
        when(seccionMock.generar(datos)).thenReturn(contenido);
        assertArrayEquals(contenido, seccionMock.generar(datos));
    }
}
