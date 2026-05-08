package com.gobierno.servicio_identidad.infrastructure.adapter.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gobierno.servicio_identidad.domain.entities.Usuario;
import com.gobierno.servicio_identidad.infrastructure.adapter.client.AutorizacionClient;
import com.gobierno.servicio_identidad.infrastructure.persistence.repository.UsuarioJpaRepository;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRolesController {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final AutorizacionClient autorizacionClient;

    public UsuarioRolesController(UsuarioJpaRepository usuarioJpaRepository,
            AutorizacionClient autorizacionClient) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.autorizacionClient = autorizacionClient;
    }

    @GetMapping("/todos-roles")
    public ResponseEntity<Map<String, List<String>>> obtenerTodosLosRoles() {
        List<Usuario> usuarios = usuarioJpaRepository.findAll();
        Map<String, List<String>> rolesMap = new HashMap<>();
        for (Usuario usuario : usuarios) {
            List<String> roles = autorizacionClient.obtenerRolesDeUsuario(usuario.getUsername());
            rolesMap.put(usuario.getUsername(), roles);
        }
        return ResponseEntity.ok(rolesMap);
    }
}
