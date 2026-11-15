# Plataforma de Selección de Practicantes

Aplicación para registrar postulaciones de practicantes y apoyar su revisión por parte del equipo de selección.

## Estado del proyecto

Fase 8 completada: interfaz funcional, pruebas automatizadas y validación continua en GitHub Actions.

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

> El frontend consume `http://localhost:8080` por defecto. Si la API se ejecuta en otro origen, configura `VITE_API_URL` antes de iniciar Vite.

## URLs locales

- Frontend: <http://localhost:5173>
- Health check: <http://localhost:8080/actuator/health>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Mailpit: <http://localhost:8025>

## Ejecución con Docker

Para ejecutar la aplicación completa en contenedores, define como mínimo `JWT_SECRET` y `APP_ADMIN_PASSWORD` en `.env` y ejecuta:

```powershell
docker compose -f docker-compose.yml -f docker-compose.production.yml up --build
```

La interfaz queda disponible en `http://localhost` (o el puerto definido en `FRONTEND_PORT`), el proxy interno dirige `/api` al backend y `http://localhost/health` expone el health check para monitoreo. En un entorno real configura `MAIL_HOST`, `MAIL_PORT`, `MAIL_FROM`, una contraseña de base de datos segura y un origen HTTPS en `APP_CORS_ALLOWED_ORIGIN`.

## Despliegue en AWS

La demostración se publica en una instancia EC2 con Amazon Linux 2023. Docker Compose ejecuta el frontend Nginx, el backend Spring Boot, PostgreSQL y Mailpit en la misma instancia. Nginx expone el puerto HTTP 80 y redirige `/api` al backend interno; PostgreSQL no se expone a Internet.

Las variables sensibles (`POSTGRES_PASSWORD`, `APP_ADMIN_PASSWORD` y `JWT_SECRET`) se configuran únicamente en el archivo `.env` de la instancia y no se suben al repositorio. Los volúmenes de Docker conservan la base de datos y los CV mientras se mantenga el disco EBS de la instancia.

Para actualizar una instancia ya preparada:

```bash
cd /opt/plataforma-seleccion-practicantes
git pull origin main
docker compose -f docker-compose.yml -f docker-compose.production.yml up -d --build
```

Para una presentación basta con mantener la instancia encendida. Después, detenerla desde EC2 evita consumir créditos. Para un entorno productivo se recomienda añadir dominio, HTTPS, copias de seguridad y una base de datos administrada.

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

La interfaz React incorpora el formulario público de postulación, el inicio de sesión del equipo de selección y un panel para consultar métricas, filtrar postulantes, ver su historial y registrar decisiones.

Para iniciar en local define una clave `JWT_SECRET` de al menos 32 caracteres en `.env`; los valores de caducidad, límite y remitente de correo están documentados en `.env.example`.

La especificación OpenAPI generada se consulta en `/v3/api-docs.yaml` y su exportación versionada está en [`docs/openapi.yaml`](docs/openapi.yaml). El avance, las decisiones y los pendientes se mantienen en la [`bitácora del proyecto`](docs/bitacora.md); el recorrido de demostración está en el [`guion de presentación`](docs/guion-presentacion.md).

## Verificación

- PowerShell: `./scripts/verify.ps1`
- Bash: `./scripts/verify.sh`
- GitHub Actions ejecuta `mvn verify` en el backend y pruebas, lint y build en el frontend para cada pull request dirigido a `main`.
- Dependabot revisa semanalmente las dependencias de Maven, npm y GitHub Actions.

## Flujo Git

- Rama principal: `main`
- Ramas de trabajo: `feature/<descripcion>` y `fix/<descripcion>`
- Commits con Conventional Commits
- Pull requests pequeños y revisables

