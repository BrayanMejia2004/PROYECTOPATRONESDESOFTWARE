package com.gobierno.servicio_auditoria.Domain.Prototype;

import java.util.HashMap;
import java.util.Map;
import com.gobierno.servicio_auditoria.Domain.Model.Auditoria;

// Registro de prototipos de auditoria predefinidos
public class AuditoriaPrototypeRegistry {

    // Mapa que almacena los prototipos base por tipo
    private static final Map<String, Auditoria> prototipos = new HashMap<>();

    // Bloque estatico para inicializar los prototipos
    static {
        
        // Prototipo para auditoria basica
        Auditoria basica = new Auditoria();
        basica.setTipo("BASICA");

        // Prototipo para auditoria de seguridad
        Auditoria seguridad = new Auditoria();
        seguridad.setTipo("SEGURIDAD");

        // Prototipo para auditoria completa
        Auditoria completa = new Auditoria();
        completa.setTipo("COMPLETA");

        // Almacena los prototipos en el mapa
        prototipos.put("BASICA", basica);
        prototipos.put("SEGURIDAD", seguridad);
        prototipos.put("COMPLETA", completa);
    }

    // Obtiene y clona el prototipo segun el tipo solicitado
    public static Auditoria obtenerPrototipo(String tipo) {
        // Busca el prototipo por tipo (mayusculas)
        Auditoria prototipo = prototipos.get(tipo.toUpperCase());

        // Lanza excepcion si el tipo no existe
        if (prototipo == null) {
            throw new IllegalArgumentException("Tipo de auditoria invalido");
        }

        // Retorna una copia del prototipo (clone del Prototype Pattern)
        return prototipo.clone();
    }
}
