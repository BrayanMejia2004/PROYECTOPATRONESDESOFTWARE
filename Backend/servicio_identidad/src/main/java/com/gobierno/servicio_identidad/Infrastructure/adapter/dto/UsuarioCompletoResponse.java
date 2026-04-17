package com.gobierno.servicio_identidad.infrastructure.adapter.dto;

import java.util.List;

public class UsuarioCompletoResponse { // DTO con información completa del usuario

    private Long id; // ID único del usuario
    private String username; // Nombre de usuario
    private String email; // Correo electrónico del usuario
    private List<String> roles; // Lista de roles del usuario (USER, ADMIN, etc.)
    private PerfilInfo perfil; // Información del perfil del usuario

    public UsuarioCompletoResponse() {
    } // Constructor vacío para deserialización JSON

    public UsuarioCompletoResponse(Long id, String username, String email, List<String> roles, PerfilInfo perfil) { // Constructor
                                                                                                                    // con
                                                                                                                    // parámetros
        this.id = id; // Asigna el ID
        this.username = username; // Asigna el username
        this.email = email; // Asigna el email
        this.roles = roles; // Asigna la lista de roles
        this.perfil = perfil; // Asigna el perfil
    }

    public Long getId() { // Getter para obtener el ID
        return id;
    }

    public void setId(Long id) { // Setter para modificar el ID
        this.id = id;
    }

    public String getUsername() { // Getter para obtener el username
        return username;
    }

    public void setUsername(String username) { // Setter para modificar el username
        this.username = username;
    }

    public String getEmail() { // Getter para obtener el email
        return email;
    }

    public void setEmail(String email) { // Setter para modificar el email
        this.email = email;
    }

    public List<String> getRoles() { // Getter para obtener la lista de roles
        return roles;
    }

    public void setRoles(List<String> roles) { // Setter para modificar la lista de roles
        this.roles = roles;
    }

    public PerfilInfo getPerfil() { // Getter para obtener el perfil
        return perfil;
    }

    public void setPerfil(PerfilInfo perfil) { // Setter para modificar el perfil
        this.perfil = perfil;
    }

    public static class PerfilInfo { // Clase interna para datos del perfil
        private String nombre; // Nombre del usuario
        private String apellido; // Apellido del usuario
        private String telefono; // Teléfono de contacto
        private String email; // Correo electrónico del usuario

        public PerfilInfo() {
        } // Constructor vacío para deserialización JSON

        public PerfilInfo(String nombre, String apellido, String telefono, String email) { // Constructor con parámetros
            this.nombre = nombre; // Asigna el nombre
            this.apellido = apellido; // Asigna el apellido
            this.telefono = telefono; // Asigna el teléfono
            this.email = email; // Asigna el email
        }

        public String getNombre() { // Getter para obtener el nombre
            return nombre;
        }

        public void setNombre(String nombre) { // Setter para modificar el nombre
            this.nombre = nombre;
        }

        public String getApellido() { // Getter para obtener el apellido
            return apellido;
        }

        public void setApellido(String apellido) { // Setter para modificar el apellido
            this.apellido = apellido;
        }

        public String getTelefono() { // Getter para obtener el teléfono
            return telefono;
        }

        public void setTelefono(String telefono) { // Setter para modificar el teléfono
            this.telefono = telefono;
        }

        public String getEmail() { // Getter para obtener el email
            return email;
        }

        public void setEmail(String email) { // Setter para modificar el email
            this.email = email;
        }
    }
}