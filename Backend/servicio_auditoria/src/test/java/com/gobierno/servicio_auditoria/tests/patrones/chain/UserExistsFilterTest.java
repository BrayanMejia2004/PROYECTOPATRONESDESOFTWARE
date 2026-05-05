package com.gobierno.servicio_auditoria.tests.patrones.chain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.chain.UserExistsFilter;

class UserExistsFilterTest {

    @Mock
    private RestTemplate restTemplate;

    private UserExistsFilter filter;
    private final String servicioIdentidadUrl = "http://localhost:8082";

    @BeforeEach
    void setUp() {
        // Inicializa mocks manualmente (sin MockitoAnnotations.openMocks)
        restTemplate = mock(RestTemplate.class);
        filter = new UserExistsFilter(restTemplate, servicioIdentidadUrl);
    }

    @Test
    @DisplayName("Debe retornar true cuando usuario existe")
    void debeRetornarTrueCuandoUsuarioExiste() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);

        // Mock: cuando se llame a getForEntity, retorna true
        when(restTemplate.getForEntity(
                eq(servicioIdentidadUrl + "/usuarios/1/existe"),
                eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));

        boolean resultado = filter.doFilter(auditoria);

        assertTrue(resultado);
        verify(restTemplate).getForEntity(anyString(), eq(Boolean.class));
    }

    @Test
    @DisplayName("Debe retornar false cuando usuario no existe")
    void debeRetornarFalseCuandoUsuarioNoExiste() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(999);

        // Mock: retorna false
        when(restTemplate.getForEntity(anyString(), eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(Boolean.FALSE));

        boolean resultado = filter.doFilter(auditoria);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debe retornar false cuando hay error de conexión")
    void debeRetornarFalseCuandoHayErrorConexion() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);

        // Mock: lanza excepción
        when(restTemplate.getForEntity(anyString(), eq(Boolean.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        boolean resultado = filter.doFilter(auditoria);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debe retornar false cuando usuario_id es null")
    void debeRetornarFalseCuandoUsuarioIdNull() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(null);

        boolean resultado = filter.doFilter(auditoria);

        assertFalse(resultado);
        verify(restTemplate, never()).getForEntity(anyString(), any());
    }

    @Test
    @DisplayName("Debe retornar false cuando usuario_id es negativo")
    void debeRetornarFalseCuandoUsuarioIdNegativo() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(-1);

        boolean resultado = filter.doFilter(auditoria);

        assertFalse(resultado);
        verify(restTemplate, never()).getForEntity(anyString(), any());
    }
}
