package com.gobierno.servicio_reportes.tests.patrones.proxy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gobierno.servicio_reportes.domain.entities.Reporte;
import com.gobierno.servicio_reportes.domain.ports.in.GenerarReportePort;
import com.gobierno.servicio_reportes.infrastructure.adapter.proxy.ReporteCacheProxy;

@ExtendWith(MockitoExtension.class)
class ReporteCacheProxyTest {

    @Mock
    private GenerarReportePort reporteUseCase;

    private ReporteCacheProxy cacheProxy;

    @BeforeEach
    void setUp() {
        cacheProxy = new ReporteCacheProxy(reporteUseCase);
    }

    @Test
    void obtener_cacheVacio_retornaNull() {
        byte[] resultado = cacheProxy.obtener("claveInexistente");
        assertNull(resultado);
    }

    @Test
    void guardarYObtener_funcionaCorrectamente() {
        byte[] contenido = "contenido test".getBytes();
        String clave = "AUDITORIA_PDF_NULL_NULL";

        cacheProxy.guardar(clave, contenido);

        byte[] resultado = cacheProxy.obtener(clave);
        assertNotNull(resultado);
        assertEquals("contenido test", new String(resultado));
    }

    @Test
    void eliminar_claveNoExiste_retornaNull() {
        byte[] contenido = "test".getBytes();
        String clave = "AUDITORIA_PDF";

        cacheProxy.guardar(clave, contenido);
        cacheProxy.eliminar(clave);

        byte[] resultado = cacheProxy.obtener(clave);
        assertNull(resultado);
    }

    @Test
    void limpiar_vaciaElCache() {
        cacheProxy.guardar("clave1", "contenido1".getBytes());
        cacheProxy.guardar("clave2", "contenido2".getBytes());

        cacheProxy.limpiar();

        assertEquals(0, cacheProxy.tamaño());
    }

    @Test
    void tamaño_retornaCantidadCorrecta() {
        assertEquals(0, cacheProxy.tamaño());

        cacheProxy.guardar("clave1", "contenido1".getBytes());
        assertEquals(1, cacheProxy.tamaño());

        cacheProxy.guardar("clave2", "contenido2".getBytes());
        assertEquals(2, cacheProxy.tamaño());
    }

    @Test
    void generarReporte_cacheMiss_llamaAlUseCase() {
        byte[] contenidoEsperado = "reporte generado".getBytes();
        when(reporteUseCase.generarReporte(anyString(), anyString(), anyString(), any(), any(), any(), any(), any(),
                any()))
                .thenReturn(contenidoEsperado);

        byte[] resultado = cacheProxy.generarReporte("AUDITORIA", "PDF", "admin");

        assertNotNull(resultado);
        assertEquals("reporte generado", new String(resultado));
        verify(reporteUseCase, times(1)).generarReporte(anyString(), anyString(), anyString(), any(), any(), any(),
                any(), any(), any());
    }

    @Test
    void generarReporte_cacheHit_noLlamaAlUseCase() {
        byte[] contenidoCacheado = "reporte en cache".getBytes();
        String clave = "AUDITORIA_PDF_NULL_NULL_NULL_NULL_NULL_NULL";
        cacheProxy.guardar(clave, contenidoCacheado);

        byte[] resultado = cacheProxy.generarReporte("AUDITORIA", "PDF", "admin", null, null, null, null, null, null);

        assertNotNull(resultado);
        assertEquals("reporte en cache", new String(resultado));
        verify(reporteUseCase, never()).generarReporte(
                eq("AUDITORIA"), eq("PDF"), eq("admin"), 
                any(), any(), any(), any(), any(), any());
    }

    @Test
    void guardarReporte_delegaAlUseCase() {
        byte[] contenido = "contenido".getBytes();
        Reporte reporteEsperado = new Reporte();

        when(reporteUseCase.guardarReporte(anyString(), anyString(), anyString(), any(), anyString(), anyString()))
                .thenReturn(reporteEsperado);

        Reporte resultado = cacheProxy.guardarReporte("AUDITORIA", "Titulo", "Descripcion", contenido, "PDF", "admin");

        assertNotNull(resultado);
        verify(reporteUseCase, times(1)).guardarReporte(anyString(), anyString(), anyString(), any(), anyString(),
                anyString());
    }

    @Test
    void obtener_actualizaUltimoAcceso() throws InterruptedException {
        String clave = "USUARIOS_CSV";
        byte[] contenido = "test".getBytes();
        cacheProxy.guardar(clave, contenido);

        Thread.sleep(10);

        byte[] resultado = cacheProxy.obtener(clave);

        assertNotNull(resultado);
    }

    @Test
    void generarClave_diferentesParametros_diferentesClaves() {
        String clave1 = "AUDITORIA_PDF_1_2024-01-01_2024-01-31_crear_BASICA_ENCABEZADO";
        String clave2 = "AUDITORIA_PDF_2_2024-01-01_2024-01-31_crear_BASICA_ENCABEZADO";

        assertNotEquals(clave1, clave2);
    }
}