package com.gobierno.servicio_identidad.tests.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gobierno.servicio_identidad.application.facade.AutenticacionFacadeImpl;
import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.domain.ports.out.AutenticadorPort;
import com.gobierno.servicio_identidad.domain.ports.out.UsuarioRepositorioPort;
import com.gobierno.servicio_identidad.application.usecases.RegistroUsuarioUseCase;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AuditoriaClient;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.GeneradorJwtAdapter;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.LoginRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.RegistroUsuarioRequest;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.SolicitudAutenticacion;
import com.gobierno.servicio_identidad.infrastructure.adapter.dto.UsuarioResponse;

@ExtendWith(MockitoExtension.class)
class AutenticacionFacadeTest {

    @Mock
    private AutenticadorPort autenticadorPort;

    @Mock
    private UsuarioRepositorioPort usuarioRepositorioPort;

    @Mock
    private RegistroUsuarioUseCase registroUsuarioUseCase;

    @Mock
    private AutorizacionClient autorizacionClient;

    @Mock
    private AuditoriaClient auditoriaClient;

    @Mock
    private GeneradorJwtAdapter generadorJwtAdapter;

    private AutenticacionFacadeImpl facade;

    @BeforeEach
    void setUp() {
        facade = new AutenticacionFacadeImpl(
                autenticadorPort,
                usuarioRepositorioPort,
                registroUsuarioUseCase,
                autorizacionClient,
                auditoriaClient,
                generadorJwtAdapter);
    }

    @Test
    void login_conCredencialesValidas_retornaToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        Usuario usuario = TestDataFactory.crearUsuario(1L, "testuser", "test@test.com");

        when(autenticadorPort.autenticar(any(SolicitudAutenticacion.class))).thenReturn(new Object());
        when(usuarioRepositorioPort.buscarPorUsername("testuser")).thenReturn(Optional.of(usuario));
        when(autorizacionClient.obtenerRolesDeUsuario("testuser")).thenReturn(List.of("USER"));
        when(generadorJwtAdapter.generarToken(usuario, List.of("USER"))).thenReturn("jwt-token-123");

        String token = facade.login(request);

        assertNotNull(token);
        assertEquals("jwt-token-123", token);
        verify(auditoriaClient).registrarAuditoria(eq(1), eq("LOGIN"), anyString(), eq("BASICA"));
    }

    @Test
    void login_usuarioNoExiste_lanzaExcepcion() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        when(autenticadorPort.autenticar(any(SolicitudAutenticacion.class))).thenReturn(new Object());
        when(usuarioRepositorioPort.buscarPorUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> facade.login(request));
    }

    @Test
    void login_usuarioSinRoles_asignaRolUser() {
        LoginRequest request = new LoginRequest();
        request.setUsername("newuser");
        request.setPassword("password123");

        Usuario usuario = TestDataFactory.crearUsuario(1L, "newuser", "new@test.com");

        when(autenticadorPort.autenticar(any(SolicitudAutenticacion.class))).thenReturn(new Object());
        when(usuarioRepositorioPort.buscarPorUsername("newuser")).thenReturn(Optional.of(usuario));
        when(autorizacionClient.obtenerRolesDeUsuario("newuser")).thenReturn(List.of());
        when(generadorJwtAdapter.generarToken(eq(usuario), anyList())).thenReturn("jwt-token");

        String token = facade.login(request);

        assertNotNull(token);
        verify(autorizacionClient).asignarRolAUsuario("newuser", "USER");
    }

    @Test
    void registrarUsuario_datosValidos_retornaUsuarioResponse() {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@test.com");

        Usuario nuevoUsuario = TestDataFactory.crearUsuario(1L, "newuser", "new@test.com");

        when(registroUsuarioUseCase.ejecutar("newuser", "password123", "new@test.com")).thenReturn(nuevoUsuario);
        doNothing().when(autorizacionClient).asignarRolAUsuario("newuser", "USER");
        doNothing().when(auditoriaClient).registrarAuditoria(anyInt(), anyString(), anyString(), anyString());

        UsuarioResponse response = facade.registrarUsuario(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("newuser", response.getUsername());
        assertEquals("new@test.com", response.getEmail());
    }

    @Test
    void validarToken_tokenValido_retornaAutenticacion() {
        String token = "Bearer jwt-token-123";
        Object autenticacion = new Object();

        when(autenticadorPort.autenticar(any(SolicitudAutenticacion.class))).thenReturn(autenticacion);

        Object result = facade.validarToken(token);

        assertNotNull(result);
        assertEquals(autenticacion, result);
    }

    @Test
    void validarToken_sinBearer_retornaAutenticacion() {
        String token = "jwt-token-123";
        Object autenticacion = new Object();

        when(autenticadorPort.autenticar(any(SolicitudAutenticacion.class))).thenReturn(autenticacion);

        Object result = facade.validarToken(token);

        assertNotNull(result);
    }
}