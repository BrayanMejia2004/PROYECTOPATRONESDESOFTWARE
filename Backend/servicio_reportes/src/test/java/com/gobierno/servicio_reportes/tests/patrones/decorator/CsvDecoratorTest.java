package com.gobierno.servicio_reportes.tests.patrones.decorator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.gobierno.servicio_reportes.Domain.ReporteConcreteComponent;
import com.gobierno.servicio_reportes.Domain.ReporteData;
import com.gobierno.servicio_reportes.Domain.Decorator.CsvDecorator;

// Test para el decorator CSV
class CsvDecoratorTest {
    
    private ReporteData datosPrueba;
    
    @BeforeEach
    void setUp() {
        // Crear datos de prueba
        String[] headers = {"ID", "Nombre", "Email"};
        String[][] filas = {
            {"1", "Juan Perez", "juan@test.com"},
            {"2", "Maria Lopez", "maria@test.com"}
        };
        
        datosPrueba = new ReporteData();
        datosPrueba.setTipo("USUARIOS");
        datosPrueba.setTitulo("Reporte de Usuarios");
        datosPrueba.setHeaders(headers);
        datosPrueba.setFilas(filas);
    }
    
    @Test
    @DisplayName("Debe generar CSV con headers")
    void debeGenerarCsvConHeaders() {
        // Arrange - usa la clase real ReporteBase
        ReporteConcreteComponent reporteBase = new ReporteConcreteComponent();
        CsvDecorator decorator = new CsvDecorator(reporteBase);
        
        // Act
        byte[] resultado = decorator.generar(datosPrueba);
        
        // Assert
        assertNotNull(resultado);
        String contenido = new String(resultado);
        
        // Verificar que contiene los headers
        assertTrue(contenido.contains("ID,Nombre,Email"));
    }
    
    @Test
    @DisplayName("Debe generar CSV con filas separadas por comas")
    void debeSepararCamposPorComas() {
        // Arrange
        ReporteConcreteComponent reporteBase = new ReporteConcreteComponent();
        CsvDecorator decorator = new CsvDecorator(reporteBase);
        
        // Act
        byte[] resultado = decorator.generar(datosPrueba);
        
        // Assert
        assertNotNull(resultado);
        String contenido = new String(resultado);
        
        // Verificar que las filas tienen comas
        assertTrue(contenido.contains("1,Juan Perez,juan@test.com"));
        assertTrue(contenido.contains("2,Maria Lopez,maria@test.com"));
    }
    
    @Test
    @DisplayName("Debe incluir titulo como comentario")
    void debeIncluirTituloComoComentario() {
        // Arrange
        ReporteConcreteComponent reporteBase = new ReporteConcreteComponent();
        CsvDecorator decorator = new CsvDecorator(reporteBase);
        
        // Act
        byte[] resultado = decorator.generar(datosPrueba);
        
        // Assert
        String contenido = new String(resultado);
        assertTrue(contenido.contains("# Reporte de Usuarios"));
    }
    
    @Test
    @DisplayName("Debe escapar comas en campos")
    void debeEscaparComasEnCampos() {
        // Arrange
        String[] headers = {"ID", "Nombre", "Direccion"};
        String[][] filas = {
            {"1", "Test", "Calle 123, Ciudad"}
        };
        
        ReporteData datosConComa = new ReporteData();
        datosConComa.setTipo("PRUEBA");
        datosConComa.setTitulo("Prueba");
        datosConComa.setHeaders(headers);
        datosConComa.setFilas(filas);
        
        ReporteConcreteComponent reporteBase = new ReporteConcreteComponent();
        CsvDecorator decorator = new CsvDecorator(reporteBase);
        
        // Act
        byte[] resultado = decorator.generar(datosConComa);
        
        // Assert
        String contenido = new String(resultado);
        // Campo con coma debe estar entre comillas
        assertTrue(contenido.contains("\"Calle 123, Ciudad\""));
    }
}
