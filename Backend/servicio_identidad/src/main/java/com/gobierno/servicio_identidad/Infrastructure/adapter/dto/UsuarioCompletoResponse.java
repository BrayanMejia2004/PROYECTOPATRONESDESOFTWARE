package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

import java.util.List;

public class UsuarioCompletoResponse {

    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private PerfilInfo perfil;

    public UsuarioCompletoResponse() {
    }

    public UsuarioCompletoResponse(Long id, String username, String email, List<String> roles, PerfilInfo perfil) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.perfil = perfil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public PerfilInfo getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilInfo perfil) {
        this.perfil = perfil;
    }

    public static class PerfilInfo {
        private String nombre;
        private String apellido;
        private String telefono;
        private String email;

        public PerfilInfo() {
        }

        public PerfilInfo(String nombre, String apellido, String telefono, String email) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.telefono = telefono;
            this.email = email;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
