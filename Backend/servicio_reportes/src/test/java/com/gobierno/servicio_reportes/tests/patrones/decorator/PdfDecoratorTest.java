package com.gobierno.servicio_reportes.tests.patrones.decorator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_reportes.Domain.ReporteConcreteComponent;
import com.gobierno.servicio_reportes.Domain.ReporteData;
import com.gobierno.servicio_reportes.Domain.Decorator.PdfDecorator;

// Test para el decorator PDF
class PdfDecoratorTest {
    
    private ReporteData datosPrueba;
    
    @BeforeEach
    void setUp() {
        // Crear datos de prueba
        String[] headers = {"ID", "Nombre", "Valor"};
        String[][] filas = {
            {"1", "Item A", "100"},
            {"2", "Item B", "200"}
        };
        
        datosPrueba = new ReporteData();
        datosPrueba.setTipo("PRUEBA");
        datosPrueba.setTitulo("Reporte de Prueba");
        datosPrueba.setHeaders(headers);
        datosPrueba.setFilas(filas);
    }
    
    @Test
    @DisplayName("Debe generar PDF con datos correctos")
    void debeGenerarPdfConDatosCorrectos() {
        // Arrange - usa la clase real ReporteBase
        ReporteConcreteComponent reporteBase = new ReporteConcreteComponent();
        PdfDecorator decorator = new PdfDecorator(reporteBase);
        
        // Act
        byte[] resultado = decorator.generar(datosPrueba);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
        
        // Verificar contenido
        String contenido = new String(resultado);
        assertTrue(contenido.contains("Reporte de Prueba"));
        assertTrue(contenido.contains("ID"));
    }
    
    @Test
    @DisplayName("Debe retornar bytes no nulos")
    void debeRetornarBytesNoNulos() {
        // Arrange
        ReporteConcreteComponent reporteBase = new ReporteConcreteComponent();
        PdfDecorator decorator = new PdfDecorator(reporteBase);
        
        // Act
        byte[] resultado = decorator.generar(datosPrueba);
        
        // Assert
        assertNotNull(resultado);
    }
}
