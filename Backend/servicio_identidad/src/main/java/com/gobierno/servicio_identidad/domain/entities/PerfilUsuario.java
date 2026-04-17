package com.gobierno.servicio_identidad.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfiles_usuario")
public class PerfilUsuario {

    @Id // Define este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento en PostgreSQL
    private Integer id; // ID único del perfil (PK)

    @Column(name = "usuario_id", unique = true, nullable = false) // FK al usuario, único y obligatorio
    private Integer usuarioId; // ID del usuario al que pertenece este perfil

    @Column(name = "nombre", length = 100, nullable = false) // Columna nombre, obligatorio
    private String nombre; // Nombre del usuario

    @Column(name = "apellido", length = 100, nullable = false) // Columna apellido, obligatorio
    private String apellido; // Apellido del usuario

    @Column(name = "telefono", length = 20, nullable = false) // Columna telefono, obligatorio
    private String telefono; // Teléfono de contacto del usuario

    protected PerfilUsuario() {
    } // Constructor protegido para que JPA pueda instanciar la entidad

    private PerfilUsuario(Builder builder) { // Constructor privado que recibe el Builder
        this.usuarioId = builder.usuarioId; // Asigna el usuarioId desde el builder
        this.nombre = builder.nombre; // Asigna el nombre desde el builder
        this.apellido = builder.apellido; // Asigna el apellido desde el builder
        this.telefono = builder.telefono; // Asigna el telefono desde el builder
    }

    public static class Builder { // Clase interna para patrón Builder
        private Integer usuarioId; // Campo obligatorio para ID de usuario
        private String nombre; // Campo obligatorio para nombre
        private String apellido; // Campo obligatorio para apellido
        private String telefono; // Campo obligatorio para teléfono

        public Builder usuarioId(Integer usuarioId) { // Setter fluent para usuarioId
            this.usuarioId = usuarioId;
            return this; // Retorna this para encadenar llamadas
        }

        public Builder nombre(String nombre) { // Setter fluent para nombre
            this.nombre = nombre;
            return this;
        }

        public Builder apellido(String apellido) { // Setter fluent para apellido
            this.apellido = apellido;
            return this;
        }

        public Builder telefono(String telefono) { // Setter fluent para telefono
            this.telefono = telefono;
            return this;
        }

        public PerfilUsuario build() { // Método que construye el objeto PerfilUsuario
            return new PerfilUsuario(this); // Crea una nueva instancia con los datos del builder
        }
    }

    public Integer getId() { // Getter para obtener el ID
        return id;
    }

    public void setId(Integer id) { // Setter para modificar el ID
        this.id = id;
    }

    public Integer getUsuarioId() { // Getter para obtener el ID del usuario
        return usuarioId;
    }

    public String getNombre() { // Getter para obtener el nombre
        return nombre;
    }

    public String getApellido() { // Getter para obtener el apellido
        return apellido;
    }

    public String getTelefono() { // Getter para obtener el teléfono
        return telefono;
    }

    public void setNombre(String nombre) { // Setter para modificar el nombre
        this.nombre = nombre;
    }

    public void setApellido(String apellido) { // Setter para modificar el apellido
        this.apellido = apellido;
    }

    public void setTelefono(String telefono) { // Setter para modificar el teléfono
        this.telefono = telefono;
    }
}