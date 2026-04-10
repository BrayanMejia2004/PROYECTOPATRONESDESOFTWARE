package com.gobierno.servicio_reportes.tests.patrones.decorator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_reportes.Application.UseCase.GenerarReporteUseCase;
import com.gobierno.servicio_reportes.Domain.ReporteData;

// Test para verificar la generacion de reportes con Decorator
class ReporteDecoratorTest {
    
    private GenerarReporteUseCase useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new GenerarReporteUseCase();
    }
    
    @Test
    @DisplayName("Debe generar reporte de auditoria en PDF")
    void debeGenerarReporteAuditoriaPdf() {
        // Act
        byte[] resultado = useCase.ejecutar("AUDITORIA", "PDF");
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
        assertTrue(resultado.length > 100); // Debe tener contenido
    }
    
    @Test
    @DisplayName("Debe generar reporte de usuarios en CSV")
    void debeGenerarReporteUsuariosCsv() {
        // Act
        byte[] resultado = useCase.ejecutar("USUARIOS", "CSV");
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
        
        // Verificar que contiene el formato CSV
        String contenido = new String(resultado);
        assertTrue(contenido.contains("# Reporte de Usuarios del Sistema"));
        assertTrue(contenido.contains("ID,Nombre,Email"));
    }
    
    @Test
    @DisplayName("Debe generar reporte de roles en PDF")
    void debeGenerarReporteRolesPdf() {
        // Act
        byte[] resultado = useCase.ejecutar("ROLES", "PDF");
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }
    
    @Test
    @DisplayName("Debe generar reporte en formato CSV con ZIP")
    void debeGenerarReporteCsvConZip() {
        // Act
        byte[] resultado = useCase.ejecutar("AUDITORIA", "CSV_ZIP");
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
        
        // Verificar que inicia con PK (cabecera ZIP)
        assertEquals(0x50, resultado[0] & 0xFF); // 'P'
        assertEquals(0x4B, resultado[1] & 0xFF); // 'K'
    }
    
    @Test
    @DisplayName("Debe generar reporte con formato por defecto PDF")
    void debeGenerarConFormatoPorDefecto() {
        // Act
        byte[] resultado = useCase.ejecutar("AUDITORIA", "");
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }
    
    @Test
    @DisplayName("Debe lanzar excepcion para tipo invalido")
    void debeLanzarExcepcionParaTipoInvalido() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.ejecutar("INVALIDO", "PDF")
        );
    }
}
