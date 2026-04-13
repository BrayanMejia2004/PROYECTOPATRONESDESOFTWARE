package com.gobierno.servicio_reportes.tests.patrones.decorator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.gobierno.servicio_reportes.application.usecases.GenerarReporteUseCase;
import com.gobierno.servicio_reportes.domain.entities.Reporte;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteDataProviderPort;
import com.gobierno.servicio_reportes.domain.ports.out.ReporteRepositoryPort;
import com.gobierno.servicio_reportes.domain.valueobjects.ReporteData;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReporteDecoratorTest {
    
    @Mock
    private ReporteDataProviderPort dataProviderPort;
    
    @Mock
    private ReporteRepositoryPort repositoryPort;
    
    private GenerarReporteUseCase useCase;
    
    private ReporteData crearDatosAuditoria() {
        ReporteData datos = new ReporteData();
        datos.setTipo("AUDITORIA");
        datos.setTitulo("Reporte de Auditoria del Sistema");
        datos.setHeaders(new String[]{"ID", "Usuario", "Accion", "Fecha"});
        datos.setFilas(new String[][]{{"1", "Juan", "LOGIN", "2026-04-10"}});
        return datos;
    }
    
    private ReporteData crearDatosUsuarios() {
        ReporteData datos = new ReporteData();
        datos.setTipo("USUARIOS");
        datos.setTitulo("Reporte de Usuarios del Sistema");
        datos.setHeaders(new String[]{"ID", "Nombre", "Email"});
        datos.setFilas(new String[][]{{"1", "Juan Perez", "juan@test.com"}});
        return datos;
    }
    
    private ReporteData crearDatosRoles() {
        ReporteData datos = new ReporteData();
        datos.setTipo("ROLES");
        datos.setTitulo("Reporte de Roles del Sistema");
        datos.setHeaders(new String[]{"ID", "Nombre", "Descripcion"});
        datos.setFilas(new String[][]{{"1", "ADMIN", "Administrador"}});
        return datos;
    }
    
    @BeforeEach
    void setUp() {
        useCase = new GenerarReporteUseCase(repositoryPort, dataProviderPort);
        
        when(dataProviderPort.obtenerDatos("AUDITORIA")).thenReturn(crearDatosAuditoria());
        when(dataProviderPort.obtenerDatos("USUARIOS")).thenReturn(crearDatosUsuarios());
        when(dataProviderPort.obtenerDatos("ROLES")).thenReturn(crearDatosRoles());
        when(repositoryPort.guardar(any(Reporte.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
    
    @Test
    void debeGenerarReporteAuditoriaPdf() {
        byte[] resultado = useCase.generarReporte("AUDITORIA", "PDF", "SYSTEM");
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }
    
    @Test
    void debeGenerarReporteUsuariosCsv() {
        byte[] resultado = useCase.generarReporte("USUARIOS", "CSV", "SYSTEM");
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
        String contenido = new String(resultado);
        assertTrue(contenido.contains("Reporte de Usuarios del Sistema"));
    }
    
    @Test
    void debeGenerarReporteRolesPdf() {
        byte[] resultado = useCase.generarReporte("ROLES", "PDF", "SYSTEM");
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }
    
    @Test
    void debeGenerarReporteCsvConZip() {
        byte[] resultado = useCase.generarReporte("AUDITORIA", "CSV_ZIP", "SYSTEM");
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
        assertEquals(0x50, resultado[0] & 0xFF);
        assertEquals(0x4B, resultado[1] & 0xFF);
    }
    
    @Test
    void debeGenerarReportePdfConZip() {
        byte[] resultado = useCase.generarReporte("AUDITORIA", "PDF_ZIP", "SYSTEM");
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
        assertEquals(0x50, resultado[0] & 0xFF);
        assertEquals(0x4B, resultado[1] & 0xFF);
    }
}
