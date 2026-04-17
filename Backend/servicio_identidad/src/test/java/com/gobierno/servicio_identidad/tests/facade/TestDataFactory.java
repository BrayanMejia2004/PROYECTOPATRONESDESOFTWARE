package com.gobierno.servicio_identidad.tests.facade;

import com.gobierno.servicio_identidad.domain.entities.PerfilUsuario;
import com.gobierno.servicio_identidad.domain.entities.Usuario;

import java.sql.Timestamp;

public class TestDataFactory {

    public static Usuario crearUsuario(Long id, String username, String email) {
        return new Usuario.Builder()
                .id(id)
                .username(username)
                .email(email)
                .password("$2a$10$encodedPassword1234567890123456789012345678901234567890")
                .estado(true)
                .fechaCreacion(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public static Usuario crearUsuario(String username, String email) {
        return crearUsuario(1L, username, email);
    }

    public static Usuario crearUsuarioInactivo(Long id, String username, String email) {
        return new Usuario.Builder()
                .username(username)
                .email(email)
                .password("$2a$10$encodedPassword1234567890123456789012345678901234567890")
                .estado(false)
                .fechaCreacion(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public static PerfilUsuario crearPerfil(Integer usuarioId, String nombre, String apellido, String telefono) {
        return new PerfilUsuario.Builder()
                .usuarioId(usuarioId)
                .nombre(nombre)
                .apellido(apellido)
                .telefono(telefono)
                .build();
    }

    public static PerfilUsuario crearPerfil(String nombre, String apellido, String telefono) {
        return crearPerfil(1, nombre, apellido, telefono);
    }
}