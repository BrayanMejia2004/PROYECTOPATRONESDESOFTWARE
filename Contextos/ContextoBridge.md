# Patrón Bridge - Servicio de Auditoría

## 1. Descripción General

El **Patrón Bridge** separa la abstracción del procesamiento de auditoría de su implementación. En este servicio, las factories del Abstract Factory actúan como implementación concreta.

---

## 2. Estructura del Patrón

```
┌─────────────────────────────────────────────────────────────────────┐
│                        PATRÓN BRIDGE                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ABSTRACTION                    IMPLEMENTATION                      │
│                                                                     │
│  ┌─────────────────────┐       ┌───────────────────────────────┐  │
│  │ AuditoriaProcessor  │──────▶│ AuditoriaAbsFactory           │  │
│  │ (abstract class)   │       │ (Abstract Factory)            │  │
│  │                     │       │                               │  │
│  │ + procesar()       │       │ BasicaFactory                │  │
│  │ # factory          │──────▶│ SeguridadFactory              │  │
│  └──────────┬─────────┘       │ CompletaFactory               │  │
│             │                 └───────────────────────────────┘  │
│             │                                                   │
│             ▼                                                   │
│  ┌─────────────────────┐                                        │
│  │ RegistrarAuditoria   │                                        │
│  │ Processor            │                                        │
│  │ (concrete)           │                                        │
│  │                     │                                        │
│  │ + procesar()        │                                        │
│  └─────────────────────┘                                        │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. Clases del Patrón

### 3.1 AuditoriaProcessor (Abstraction)

**Ubicación**: `Domain/Bridge/Abstraction/AuditoriaProcessor.java`

Clase abstracta base que define la interfaz para processors de auditoría.

```java
// Abstraction del patron Bridge - define la interfaz base para processors de auditoria
public abstract class AuditoriaProcessor {

    // Referencia a la implementacion (Abstract Factory)
    protected AuditoriaAbsFactory factory;

    public AuditoriaProcessor(AuditoriaAbsFactory factory) {
        this.factory = factory;
    }

    // Metodo abstracto que cada processor debe implementar
    public abstract AuditoriaResponse procesar(Auditoria auditoria);
}
```

---

### 3.2 RegistrarAuditoriaProcessor (Refined Abstraction)

**Ubicación**: `Domain/Bridge/Abstraction/RegistrarAuditoriaProcessor.java`

Implementación concreta que orquesta el flujo de registro de auditoría.

```java
// Refined Abstraction del patron Bridge - implementacion concreta del processor
public class RegistrarAuditoriaProcessor extends AuditoriaProcessor {

    // Puerto de salida para persistir la auditoria
    private final RegistroAuditoria registroAuditoria;

    public RegistrarAuditoriaProcessor(AuditoriaAbsFactory factory,
            RegistroAuditoria registroAuditoria) {
        super(factory);
        this.registroAuditoria = registroAuditoria;
    }

    // Flujo de procesamiento de auditoria
    @Override
    public AuditoriaResponse procesar(Auditoria auditoria) {
        // 1. Obtiene prototipo base segun tipo de auditoria
        Auditoria auditoriaBase = AuditoriaPrototypeRegistry.obtenerPrototipo(auditoria.getTipo());

        // 2. Completa datos del request en el prototipo
        auditoriaBase.setUsuario_id(auditoria.getUsuario_id());
        auditoriaBase.setAccion(auditoria.getAccion());
        auditoriaBase.setDescripcion(auditoria.getDescripcion());
        auditoriaBase.setIp_origen(auditoria.getIp_origen());

        // 3. Crea auditoria procesada usando la factory
        Auditoria procesada = factory.crearAuditoria(auditoriaBase);
        
        // 4. Registra la auditoria en base de datos
        registroAuditoria.registrarAccion(procesada);

        // 5. Crea y retorna la respuesta usando la factory
        return factory.crearRespuesta(procesada);
    }
}
```

---

### 3.3 RegistrarAuditoriaUseCase (Client)

**Ubicación**: `Application/UseCase/RegistrarAuditoriaUseCase.java`

Caso de uso que coordina el flujo de auditoría.

```java
// Caso de uso que coordina el flujo de auditoria
@Service
public class RegistrarAuditoriaUseCase {

    // Mapa de processors por tipo de auditoria
    private final Map<String, AuditoriaProcessor> processors;

    // Constructor que recibe factories de Spring y crea processors asociados
    public RegistrarAuditoriaUseCase(Map<String, AuditoriaAbsFactory> factories, 
        RegistroAuditoria registroAuditoria) {
        this.processors = new HashMap<>();
        // Por cada factory crea un processor asociado
        factories.forEach((tipo, factory) ->
                processors.put(tipo, new RegistrarAuditoriaProcessor(factory, registroAuditoria))
        );
    }

    // Ejecuta el caso de uso - selecciona y ejecuta el processor correcto
    public AuditoriaResponse ejecutar(Auditoria auditoria, String tipo) {
        AuditoriaProcessor processor = processors.get(tipo.toUpperCase());

        if (processor == null) {
            throw new IllegalArgumentException("Tipo de auditoria invalido");
        }

        return processor.procesar(auditoria);
    }
}
```

---

## 4. Flujo de Ejecución

```
1. POST /auditoria/registrar/{tipo}
       │
       ▼
2. AuditoriaController (extrae IP)
       │
       ▼
3. RegistrarAuditoriaUseCase.ejecutar()
       │
       ▼
4. RegistrarAuditoriaProcessor.procesar()
       │
       ├── 4.1 PrototypeRegistry.obtenerPrototipo() → clone()
       ├── 4.2 Completar datos (usuario, accion, descripcion, IP)
       ├── 4.3 Factory.crearAuditoria() → establece tipo
       ├── 4.4 RegistroAuditoria.registrarAccion() → guarda en BD
       └── 4.5 Factory.crearRespuesta() → retorna DTO
       │
       ▼
5. Response JSON
```

---

## 5. Beneficios

| Beneficio | Descripción |
|-----------|-------------|
| Separación de responsabilidades | Procesamiento separado de creación de objetos |
| Reutilización | Las factories se usan en ambos patrones |
| Extensibilidad | Nuevos tipos solo requieren nueva factory |
| Testabilidad | Cada componente se prueba independientemente |

---

## 6. Relación con Abstract Factory

```
Abstract Factory                          Bridge
┌──────────────────────┐      ┌──────────────────────┐
│ AuditoriaAbsFactory  │──────│ AuditoriaProcessor   │
│   BasicaFactory     │      │   (abstraction)      │
│   SeguridadFactory  │      └──────────┬───────────┘
│   CompletaFactory   │                 │
└──────────────────────┘                 │ usa como
                                          │ implementacion
                                          ▼
                                 RegistrarAuditoriaProcessor
                                    (refined abstraction)
```

- **Abstract Factory**: Crea objetos de auditoría según el tipo
- **Bridge**: Usa las factories como implementación para separar la lógica de procesamiento

---

## 7. Estructura de Archivos

```
servicio_auditoria/
├── Domain/
│   ├── Bridge/Abstraction/
│   │   ├── AuditoriaProcessor.java              ← Abstraction
│   │   └── RegistrarAuditoriaProcessor.java    ← Refined Abstraction
│   ├── AbsFactory/
│   │   ├── AuditoriaAbsFactory.java            ← Implementation
│   │   └── FactoryConcret/
│   │       ├── AuditoriaBasicaFactory.java
│   │       ├── AuditoriaSeguridadFactory.java
│   │       └── AuditoriaCompletaFactory.java
│   ├── Prototype/
│   │   └── AuditoriaPrototypeRegistry.java
│   └── Model/
│       └── Auditoria.java
├── Application/UseCase/
│   └── RegistrarAuditoriaUseCase.java         ← Client
└── Infrastructure/
    └── Controller/
        └── AuditoriaController.java
```

---

## 8. Versión

- **Fecha**: Abril 2026
- **Versión**: 1.2
