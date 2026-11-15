# Casos de prueba

| ID | Historia | Caso | Nivel | Estado | Evidencia |
| --- | --- | --- | --- | --- |
| CP-01 | HU-01 | Registro exitoso de postulante con PDF válido | Servicio/API | Aprobado | Suite backend |
| CP-02 | HU-01 | Rechazo de correo duplicado | Servicio/API | Aprobado | Suite backend |
| CP-03 | HU-01 | Rechazo de archivo que no es PDF o supera 5 MB | Servicio/API | Aprobado | Suite backend |
| CP-04 | HU-01 | El endpoint público de postulación no exige JWT | Integración web | Aprobado | `BackendApplicationTests` |
| CP-05 | HU-02 | JSON mal formado devuelve `ProblemDetail` | Integración web | Aprobado | `BackendApplicationTests` |
| CP-06 | HU-03 | Descarga del CV con nombre original | Servicio | Aprobado | `DownloadApplicantCvServiceTest` |
| CP-10 | HU-02 | Login con credenciales válidas | Servicio | Aprobado | `AuthenticationServiceTest` |
| CP-11 | HU-02 | Rechazo de contraseña inválida | Servicio | Aprobado | `AuthenticationServiceTest` |
| CP-21 | HU-03 | Paginación, filtros y búsqueda | Servicio | Aprobado | `ListApplicantsServiceTest` |
| CP-27 | HU-04 | Detalle con historial de decisiones | Integración local | Aprobado | Verificación API fase 5 |
| CP-29 | HU-04 | Registro de cambio de estado | Servicio | Aprobado | `ChangeApplicantStatusServiceTest` |
| CP-30 | HU-04 | Comentario obligatorio para NOT_VIABLE | Servicio | Aprobado | `ChangeApplicantStatusServiceTest` |
| CP-38 | HU-05 | Fallo de correo no revierte decisión | Servicio | Aprobado | `ChangeApplicantStatusServiceTest` |
| CP-40 | HU-05 | Métricas agrupadas por estado y carrera | Servicio | Aprobado | `GetDashboardMetricsServiceTest` |
| CP-41 | HU-05 | Métricas vacías sin error | Servicio | Aprobado | `GetDashboardMetricsServiceTest` |
| CP-FE-01 | HU-01 | Navegación desde portada al formulario público | Componente | Aprobado | `App.test.tsx` |
| CP-FE-02 | HU-02 | Login redirige al panel interno | Componente | Aprobado | `App.test.tsx` |
| CP-FE-03 | HU-04 | Decisión NOT_VIABLE envía comentario al API | Componente | Aprobado | `App.test.tsx` |

## Ejecución más reciente

- Fecha: 15 de noviembre de 2026.
- Backend: 21 pruebas aprobadas con `mvnw.cmd verify`.
- Frontend: 4 pruebas aprobadas con `npm test`.
- Calidad estática: `npm run lint` y `npm run build` ejecutados correctamente.

