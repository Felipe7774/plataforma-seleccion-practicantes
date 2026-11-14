# Plataforma de Selección de Practicantes

Aplicación para registrar postulaciones de practicantes y apoyar su revisión por parte del equipo de selección.

## Estado del proyecto

Fase 5 completada: filtros y paginación, trazabilidad de decisiones, métricas y notificaciones por correo.

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
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Mailpit: <http://localhost:8025>

## API disponible

`POST /api/v1/applicants` es público y recibe una solicitud `multipart/form-data` con:

- `fullName`
- `email`
- `career`
- `semester` entre 1 y 12
- `cv`, un archivo PDF de máximo 5 MB

La API verifica el tipo de contenido y la firma `%PDF-`, guarda el archivo con un UUID y responde `409` si el correo ya existe. El registro y el inicio de sesión se limitan a cinco intentos por minuto y por IP de forma configurable.

Las rutas de autenticación son:

- `POST /api/v1/auth/login`: recibe `username` y `password`; responde access token JWT y refresh token.
- `POST /api/v1/auth/refresh`: rota un refresh token válido y responde un nuevo par de tokens.
- `POST /api/v1/auth/logout`: requiere `Authorization: Bearer <accessToken>` y revoca el refresh token enviado.

`GET /api/v1/applicants`, `GET /api/v1/applicants/{id}` y `PATCH /api/v1/applicants/{id}/status` requieren un JWT con el rol `ANALYST` o `ADMIN`. Los refresh tokens se guardan únicamente como hashes SHA-256 y se invalidan al rotarlos o cerrar sesión.

El listado admite `status`, `career`, `q`, `page` y `size`; devuelve elementos y metadatos de paginación. El detalle incluye el historial de decisiones. Para asignar `NOT_VIABLE` se debe enviar un `comment`; cada cambio queda registrado y dispara un correo asíncrono sin afectar la decisión si Mailpit/SMTP no está disponible.

`GET /api/v1/dashboard/metrics` devuelve el total de postulaciones y sus agrupaciones por estado y carrera para los roles `ANALYST` y `ADMIN`.

Para iniciar en local define una clave `JWT_SECRET` de al menos 32 caracteres en `.env`; los valores de caducidad, límite y remitente de correo están documentados en `.env.example`.

La especificación OpenAPI generada se consulta en `/v3/api-docs.yaml` y su exportación versionada está en [`docs/openapi.yaml`](docs/openapi.yaml).

## Verificación

- PowerShell: `./scripts/verify.ps1`
- Bash: `./scripts/verify.sh`

## Flujo Git

- Rama principal: `main`
- Ramas de trabajo: `feature/<descripcion>` y `fix/<descripcion>`
- Commits con Conventional Commits
- Pull requests pequeños y revisables

