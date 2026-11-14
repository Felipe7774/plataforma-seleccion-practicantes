# ADR 0001: Stack y arquitectura inicial

- Estado: Aceptado
- Fecha: 2026-11-14

## Contexto

La plataforma debe registrar practicantes, almacenar su hoja de vida y permitir que analistas revisen y actualicen el estado de cada postulación. Debe ser sencilla de ejecutar localmente y permitir una evolución posterior hacia AWS.

## Decisión

Se utilizará un monorepositorio con:

- Spring Boot 4.1 y Java 21 para la API.
- React, TypeScript y Vite para la interfaz.
- PostgreSQL como base de datos y Flyway para migraciones.
- Una arquitectura hexagonal pragmática en el backend, separando dominio, aplicación e infraestructura.
- Puertos para almacenamiento de archivos y notificaciones, con adaptadores locales inicialmente y adaptadores AWS en una fase posterior.
- Docker Compose para PostgreSQL y Mailpit durante el desarrollo.

## Consecuencias

- El dominio podrá probarse sin depender de Spring ni de infraestructura externa.
- La estructura exige disciplina para no introducir dependencias de infraestructura en el dominio.
- Se priorizará completar flujos verticales antes de agregar integraciones de nube.

