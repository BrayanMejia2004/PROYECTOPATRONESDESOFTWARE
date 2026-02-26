package com.gobierno.servicio_autorizacion.Domain.Model;

public class RolFactory {
    
    public static Rol crearRol(String nombre) {

        switch (nombre.toUpperCase()) {

            case "ADMIN":
                return new Rol("ADMIN", "Rol con acceso total al sistema");

            case "USER":
                return new Rol("USER", "Rol con acceso básico");

            case "AUDITOR":
                return new Rol("AUDITOR", "Rol con acceso a auditorías");

            default:
                throw new IllegalArgumentException("Tipo de rol no válido");
        }
    }
}
