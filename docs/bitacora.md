# Bitácora del proyecto

## Identificación

- Proyecto: Plataforma de Selección de Practicantes
- Repositorio: `Felipe7774/plataforma-seleccion-practicantes`
- Rama de trabajo: `feature/cv-download-and-docs`
- Último hito funcional: aplicación desplegada y verificada en AWS EC2
- Fecha de corte: 15 de noviembre de 2026

## Estado general

Las fases 1 a 9 están implementadas y publicadas en GitHub. La aplicación está desplegada en AWS EC2 y funciona desde su IP pública. Los últimos ajustes de producción siguen pendientes de fusionarse a `main` mediante el Pull Request de la rama actual.

## Trabajo completado

### Fase 0 — Preparación

- Repositorio GitHub creado y conectado.
- Estructura inicial de backend, frontend, documentación y scripts.
- Docker Compose preparado para PostgreSQL y Mailpit.
- Convenciones de ramas y commits definidas.

### Fase 1 — Dominio y persistencia

- Modelo de postulante, usuario, estado y cambio de estado.
- Persistencia con PostgreSQL, JPA y Flyway.
- Tabla de historial de estados.
- Adaptadores de persistencia separados del dominio.

Commit: `f8b5065 feat: add domain model and persistence schema`

### Fase 2 — Registro del postulante

- Registro público mediante `multipart/form-data`.
- Validación de campos y archivo PDF.
- Límite de tamaño de CV.
- Almacenamiento local con nombre UUID.
- Prevención de correos duplicados.
- Pruebas de servicio y validaciones principales.

Commit: `cf54106 feat: add applicant registration flow`

### Fase 3 — Seguridad JWT

- Login con access token y refresh token.
- Rotación y revocación de refresh tokens mediante hash SHA-256.
- Roles `ANALYST` y `ADMIN`.
- Protección de endpoints internos.
- Rate limiting configurable para login y registro.

Commit: `78d9de5 feat: add jwt authentication and role security`

### Fase 4 — Documentación OpenAPI

- Swagger UI y especificación OpenAPI generada desde los controladores.
- Exportación versionada en `docs/openapi.yaml`.
- Documentación de autenticación y endpoints principales.

Commit: `f9fdcae feat: add OpenAPI documentation`

### Fase 5 — Extras de producto

- Listado paginado con `page` y `size`.
- Filtros por estado y carrera.
- Búsqueda por nombre o correo mediante `q`.
- Historial visible en el detalle del postulante.
- Comentario obligatorio para marcar `NOT_VIABLE`.
- Registro del usuario que realiza cada decisión.
- Métricas por estado y carrera en `/api/v1/dashboard/metrics`.
- Notificación de cambios por correo asíncrono.
- Fallos de correo aislados para no revertir decisiones.
- OpenAPI, README y `.env.example` actualizados.

Commit: `82de4e2 feat: add product workflow extras`

### Fase 6 — Interfaz funcional

- Formulario React para el registro público de postulantes.
- Inicio de sesión del equipo de selección.
- Panel de selección con métricas, búsqueda y filtro por estado.
- Consulta de detalle e historial de cada postulación.
- Acciones para marcar postulaciones como viables o no viables.
- Integración mediante `fetch` con la API y token guardado en `localStorage`.
- Validación visual local de la portada y las pantallas de postulación e inicio de sesión.

### Fase 7 — Calidad y trazabilidad

- Historias de usuario completadas con criterios de aceptación Gherkin.
- Matriz de casos de prueba actualizada con evidencias de backend, API y frontend.
- Vitest y Testing Library configurados para el frontend.
- Pruebas de componentes para postulación, autenticación y decisión no viable.

### Fase 8 — Integración continua

- Flujo de GitHub Actions en `.github/workflows/ci.yml`.
- Ejecución de `mvn verify` con Java 21 para el backend.
- Ejecución de pruebas, lint y build con Node 22 para el frontend.
- Caché de Maven y npm para acelerar las ejecuciones.
- Dependabot configurado para actualizar dependencias de Maven, npm y GitHub Actions semanalmente.

### Fase 9 — Preparación para despliegue

- Dockerfile multi-etapa para backend Java 21 y frontend React.
- Nginx entrega la aplicación estática y redirige `/api` al backend interno.
- Compose de producción reutilizable con PostgreSQL, persistencia de CV y configuración por variables de entorno.
- Despliegue en AWS EC2 con Amazon Linux 2023, Docker Compose, Nginx, Spring Boot y PostgreSQL en contenedores.
- El puerto HTTP 80 queda público para la demostración; PostgreSQL se mantiene interno.
- Las credenciales y secretos de producción se conservan en el `.env` de la instancia, fuera de Git.
- Se corrigió la ruta de API del frontend para evitar el prefijo duplicado `/api/api/v1`.
- El registro público se verifica sin JWT y el almacenamiento de CV inicializa permisos para el usuario no privilegiado del backend.
- Verificación en AWS: inicio de sesión administrativo, registro de postulante con PDF y visualización de la postulación en el panel.
- Se centralizó el manejo de errores HTTP con respuestas `ProblemDetail` para validaciones, formatos no soportados y errores inesperados.

## Evidencia de verificación

- Backend: `mvnw.cmd verify` correcto.
- Pruebas backend: 18 ejecutadas, 0 fallos.
- Frontend: `npm run lint` correcto.
- Frontend: `npm run build` correcto.
- Prueba integrada local: login, filtro paginado, rechazo de decisión sin comentario, cambio de estado, historial y métricas.
- Mailpit local recibió correctamente una notificación.
- `git diff --check` sin errores.

## Pendientes identificados

### Prioridad alta

- Revisar y aprobar el Pull Request de mejoras pendientes antes del merge a `main`.
- Añadir cobertura de componentes para errores de red, filtros, historial y cierre de sesión.
- Documentar la actualización de la instancia AWS y conservar los secretos fuera del repositorio.
- Añadir dominio, HTTPS y respaldos antes de considerar un uso productivo.
- Configurar protección de rama para exigir la comprobación de CI antes de fusionar pull requests.

### Prioridad media

- Añadir pruebas de integración de repositorios con PostgreSQL/Testcontainers.
- Añadir pruebas de controlador para autorización, validaciones y respuestas `ProblemDetail`.
- Revisar accesibilidad, estados de carga, errores y diseño responsive.
- Definir política de retención y eliminación de CV almacenados.

### Prioridad baja

- Añadir observabilidad: logs estructurados, métricas técnicas y trazas.
- Añadir auditoría de acciones administrativas.
- Preparar despliegue continuo y análisis de seguridad de dependencias.
- Revisar configuración de producción: CORS, límites de subida, SMTP, almacenamiento y backups.

## Próximo paso recomendado

La siguiente actividad recomendada es fusionar el Pull Request de producción a `main`, ejecutar la CI y hacer un ensayo de presentación: registrar un postulante, ingresar como administrador, consultar el detalle y cambiar su estado. Después de la presentación, se deben detener o revisar los recursos de AWS para controlar el consumo.

