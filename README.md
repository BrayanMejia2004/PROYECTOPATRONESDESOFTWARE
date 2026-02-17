# 🏛 Sistema de Gestión de Identidad Digital  
**Proyecto – Sector Gobierno**

## 📌 Descripción
Este proyecto implementa un **Sistema de Gestión de Identidad Digital** orientado al sector gubernamental, desarrollado bajo una **arquitectura de microservicios**.

El sistema permite gestionar la **autenticación**, **autorización** y **auditoría** de usuarios dentro de una plataforma digital, aplicando buenas prácticas de desarrollo y principios de arquitectura limpia.

Este es un **proyecto académico**, cuyo objetivo es demostrar la implementación de un sistema seguro y organizado, separando responsabilidades en distintos servicios.

---

## 🎯 Objetivo del Sistema
Desarrollar una plataforma que permita:

- Registrar usuarios.
- Autenticar mediante usuario y contraseña.
- Controlar el acceso según roles.
- Registrar eventos importantes del sistema.
- Simular la integración con otros servicios.

---

## 🔐 Autenticación
El sistema implementa un mecanismo de autenticación basado en:

1. Usuario y contraseña  
2. Generación de token JWT  

### Flujo de autenticación:
- El usuario ingresa sus credenciales.
- El sistema valida los datos.
- Si las credenciales son correctas, se genera un token JWT.
- El token se utiliza para acceder a los recursos protegidos.

Este enfoque permite manejar sesiones de forma segura **sin mantener estado en el servidor**.

---

## 🛡 Gestión de Permisos y Roles
El sistema cuenta con un microservicio dedicado a la autorización que permite:

- Definir roles (por ejemplo: `ADMIN`, `USER`, `AUDITOR`).
- Asignar permisos específicos.
- Controlar el acceso a recursos protegidos.
- Restringir funcionalidades según el perfil del usuario.

---

## 🌐 Integración con Servicios Gubernamentales
La arquitectura permite la integración con otros servicios mediante:

- API Gateway como punto central de entrada.
- Comunicación entre microservicios.
- Posibilidad de conexión con servicios externos.

---

## 🔒 Seguridad y Buenas Prácticas
El sistema incorpora medidas básicas de seguridad como:

- Encriptación de contraseñas.
- Uso de tokens JWT para autenticación.
- Separación de responsabilidades por microservicios.
- Registro de eventos importantes mediante auditoría.
- Control de acceso basado en roles (RBAC).

### Eventos auditados:
- Inicio de sesión.
- Creación o modificación de usuarios.
- Cambios de roles.
- Acciones administrativas.

Esto permite mantener **trazabilidad y control** dentro del sistema.

---

## 🏗 Arquitectura del Sistema

### 🔹 Backend (Microservicios)
- **API Gateway** → Punto único de entrada y enrutamiento.
- **Servicio de Identidad** → Registro y autenticación de usuarios.
- **Servicio de Autorización** → Gestión de roles y permisos.
- **Servicio de Auditoría** → Registro de eventos del sistema.

Cada microservicio cuenta con su **propia base de datos**, lo que garantiza independencia y mejor organización.

---

### 🎨 Frontend (Aplicación Web)
Desarrollado en **React**, permite:

- Inicio de sesión.
- Gestión visual de usuarios.
- Administración de roles.
- Consulta de auditoría.
- Acceso a funcionalidades según permisos.

El frontend se comunica **exclusivamente con el API Gateway**.

---
