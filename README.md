# Plataforma de Selección de Practicantes

Aplicación para registrar postulaciones de practicantes y apoyar su revisión por parte del equipo de selección.

## Estado del proyecto

Fase 0 en curso: estructura inicial, servicios locales y verificación del entorno.

## Tecnologías

- Java 21 y Spring Boot 4.1
- React 19, TypeScript y Vite
- PostgreSQL 17
- Flyway
- Docker Compose
- Mailpit para correo local

> El plan original indicaba Spring Boot 3. Spring Initializr ya ofrece Boot 4 como versión estable mínima, por lo que el proyecto usa Boot 4.1 con Java 21.

## Estructura

```text
backend/     API Spring Boot
frontend/    aplicación React
docs/        decisiones y documentación del proyecto
scripts/     verificación y automatización
```

## Requisitos

- Java 21
- Node.js 22 o superior
- Docker Desktop con Docker Compose

## Inicio local

1. Copia `.env.example` como `.env` y cambia las claves locales si lo necesitas.
2. Levanta los servicios con `docker compose up -d`.
3. Ejecuta el backend con `cd backend` y `./mvnw spring-boot:run`.
   En Windows PowerShell usa `./mvnw.cmd spring-boot:run`.
4. Ejecuta el frontend con `cd frontend`, `npm install` y `npm run dev`.

## URLs locales

- Frontend: <http://localhost:5173>
- Health check: <http://localhost:8080/actuator/health>
- Mailpit: <http://localhost:8025>

## API disponible

`POST /api/v1/applicants` es público y recibe una solicitud `multipart/form-data` con:

- `fullName`
- `email`
- `career`
- `semester` entre 1 y 12
- `cv`, un archivo PDF de máximo 5 MB

La API verifica el tipo de contenido y la firma `%PDF-`, guarda el archivo con un UUID y responde `409` si el correo ya existe. Las rutas de listado, detalle y cambio de estado ya existen, pero se habilitarán para analistas cuando se implemente JWT en la Fase 3.

## Verificación

- PowerShell: `./scripts/verify.ps1`
- Bash: `./scripts/verify.sh`

## Flujo Git

- Rama principal: `main`
- Ramas de trabajo: `feature/<descripcion>` y `fix/<descripcion>`
- Commits con Conventional Commits
- Pull requests pequeños y revisables

