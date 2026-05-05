package com.gobierno.servicio_auditoria.tests.patrones.chain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.gobierno.servicio_auditoria.domain.entities.Auditoria;
import com.gobierno.servicio_auditoria.domain.chain.*;
import com.gobierno.servicio_auditoria.infrastructure.persistence.repository.AuditoriaJpaRepository;
import java.sql.Timestamp;
import java.util.Arrays;

class ChainOfResponsibilityTest {

    private RestTemplate restTemplate;
    private AuditoriaJpaRepository auditoriaJpaRepository;

    private AuditoriaFilter chain;
    private final String servicioIdentidadUrl = "http://localhost:8082";

    @BeforeEach
    void setUp() {
        // Inicializa mocks manualmente
        restTemplate = mock(RestTemplate.class);
        auditoriaJpaRepository = mock(AuditoriaJpaRepository.class);

        // Construir cadena manualmente
        UserExistsFilter userExistsFilter = new UserExistsFilter(restTemplate, servicioIdentidadUrl);
        IpValidationFilter ipValidationFilter = new IpValidationFilter();
        ActionAllowedFilter actionAllowedFilter = new ActionAllowedFilter();
        DuplicateCheckFilter duplicateCheckFilter = new DuplicateCheckFilter(auditoriaJpaRepository);

        // Ensamblar cadena: userExists -> ipValidation -> actionAllowed ->
        // duplicateCheck
        userExistsFilter.setNext(ipValidationFilter);
        ipValidationFilter.setNext(actionAllowedFilter);
        actionAllowedFilter.setNext(duplicateCheckFilter);

        chain = userExistsFilter;
    }

    @Test
    @DisplayName("Debe pasar toda la cadena cuando todo es válido")
    void debePasarTodaLaCadena() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);
        auditoria.setIp_origen("192.168.1.1");
        auditoria.setAccion("LOGIN");

        // Mock: usuario existe
        when(restTemplate.getForEntity(
                eq(servicioIdentidadUrl + "/usuarios/1/existe"),
                eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));

        // Mock: no hay duplicados
        when(auditoriaJpaRepository.findByUsuarioId(1))
                .thenReturn(Arrays.asList());

        boolean resultado = chain.doFilter(auditoria);

        assertTrue(resultado);
        verify(restTemplate).getForEntity(eq(servicioIdentidadUrl + "/usuarios/1/existe"), eq(Boolean.class));
        verify(auditoriaJpaRepository).findByUsuarioId(1);
    }

    @Test
    @DisplayName("Debe fallar en UserExists cuando usuario no existe")
    void debeFallarEnUserExists() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(999);
        auditoria.setIp_origen("192.168.1.1");
        auditoria.setAccion("LOGIN");

        // Mock: usuario no existe
        when(restTemplate.getForEntity(
                eq(servicioIdentidadUrl + "/usuarios/999/existe"),
                eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(Boolean.FALSE));

        boolean resultado = chain.doFilter(auditoria);

        assertFalse(resultado);
        verify(restTemplate).getForEntity(eq(servicioIdentidadUrl + "/usuarios/999/existe"), eq(Boolean.class));
        verify(auditoriaJpaRepository, never()).findByUsuarioId(anyInt());
    }

    @Test
    @DisplayName("Debe fallar en IpValidation cuando IP es inválida")
    void debeFallarEnIpValidation() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);
        auditoria.setIp_origen("999.999.999.999"); // IP inválida
        auditoria.setAccion("LOGIN");

        // Mock: usuario existe (pasa el primer filtro)
        when(restTemplate.getForEntity(
                eq(servicioIdentidadUrl + "/usuarios/1/existe"),
                eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));

        boolean resultado = chain.doFilter(auditoria);

        assertFalse(resultado);
        verify(restTemplate).getForEntity(eq(servicioIdentidadUrl + "/usuarios/1/existe"), eq(Boolean.class));
        verify(auditoriaJpaRepository, never()).findByUsuarioId(anyInt());
    }

    @Test
    @DisplayName("Debe fallar en ActionAllowed cuando acción no permitida")
    void debeFallarEnActionAllowed() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);
        auditoria.setIp_origen("192.168.1.1");
        auditoria.setAccion("HACK_SISTEMA"); // Acción no permitida

        // Mock: usuario existe y IP válida (pasa primeros filtros)
        when(restTemplate.getForEntity(
                eq(servicioIdentidadUrl + "/usuarios/1/existe"),
                eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));

        boolean resultado = chain.doFilter(auditoria);

        assertFalse(resultado);
        verify(restTemplate).getForEntity(eq(servicioIdentidadUrl + "/usuarios/1/existe"), eq(Boolean.class));
        verify(auditoriaJpaRepository, never()).findByUsuarioId(anyInt());
    }

    @Test
    @DisplayName("Debe fallar en DuplicateCheck cuando hay duplicado reciente")
    void debeFallarEnDuplicateCheck() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(1);
        auditoria.setIp_origen("192.168.1.1");
        auditoria.setAccion("LOGIN");

        // Mock: usuario existe
        when(restTemplate.getForEntity(
                eq(servicioIdentidadUrl + "/usuarios/1/existe"),
                eq(Boolean.class)))
                .thenReturn(ResponseEntity.ok(Boolean.TRUE));

        // Mock: hay duplicado hace 1 segundo
        Auditoria duplicado = new Auditoria();
        duplicado.setAccion("LOGIN");
        duplicado.setFecha(new Timestamp(System.currentTimeMillis() - 1000));

        when(auditoriaJpaRepository.findByUsuarioId(1))
                .thenReturn(Arrays.asList(duplicado));

        boolean resultado = chain.doFilter(auditoria);

        assertFalse(resultado);
        verify(restTemplate).getForEntity(eq(servicioIdentidadUrl + "/usuarios/1/existe"), eq(Boolean.class));
        verify(auditoriaJpaRepository).findByUsuarioId(1);
    }

    @Test
    @DisplayName("Debe fallar cuando usuario_id es null")
    void debeFallarCuandoUsuarioIdNull() {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario_id(null);
        auditoria.setIp_origen("192.168.1.1");
        auditoria.setAccion("LOGIN");

        boolean resultado = chain.doFilter(auditoria);

        assertFalse(resultado);
        verify(restTemplate, never()).getForEntity(anyString(), any());
        verify(auditoriaJpaRepository, never()).findByUsuarioId(anyInt());
    }
}
