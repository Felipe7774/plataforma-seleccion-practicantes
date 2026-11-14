# Historias de usuario

## HU-01 — Registro de postulante

Como estudiante, quiero registrar mis datos académicos y mi hoja de vida para participar en el proceso de selección.

```gherkin
Escenario: Registro exitoso con hoja de vida válida
  Dado que el estudiante completa nombre, correo, carrera y semestre válido
  Y adjunta una hoja de vida PDF de máximo 5 MB
  Cuando envía la postulación
  Entonces el sistema registra al postulante con estado PENDING
  Y muestra una confirmación de registro exitoso

Escenario: Archivo no válido
  Dado que el estudiante adjunta un archivo que no es PDF
  Cuando envía la postulación
  Entonces el sistema rechaza la solicitud con un mensaje de validación
```

## HU-02 — Acceso del equipo de selección

Como analista, quiero iniciar sesión de forma segura para consultar únicamente la información autorizada.

```gherkin
Escenario: Inicio de sesión válido
  Dado que el analista tiene credenciales vigentes
  Cuando inicia sesión
  Entonces el sistema entrega un token de acceso
  Y muestra el panel de selección

Escenario: Inicio de sesión inválido
  Dado que el analista ingresa una contraseña incorrecta
  Cuando intenta iniciar sesión
  Entonces el sistema informa que las credenciales son inválidas
  Y no concede acceso al panel
```

## HU-03 — Consulta y filtro de postulantes

Como analista, quiero buscar, filtrar y paginar postulantes para revisar el proceso con eficiencia.

```gherkin
Escenario: Filtrar por estado y buscar por texto
  Dado que existen postulantes registrados
  Cuando el analista filtra por estado PENDING y busca un nombre o correo
  Entonces el sistema muestra solo los postulantes que cumplen ambos criterios
  Y presenta los metadatos de paginación
```

## HU-04 — Decisión con trazabilidad

Como analista, quiero registrar una decisión sobre un postulante para dejar evidencia del resultado del proceso.

```gherkin
Escenario: Marcar postulante como viable
  Dado que el analista consulta el detalle de un postulante
  Cuando lo marca como VIABLE
  Entonces el sistema actualiza su estado
  Y registra la decisión en el historial con usuario y fecha

Escenario: Marcar postulante como no viable
  Dado que el analista consulta el detalle de un postulante
  Cuando lo marca como NOT_VIABLE sin comentario
  Entonces el sistema rechaza la decisión
  Cuando lo marca como NOT_VIABLE con un comentario
  Entonces el sistema registra la decisión y su justificación
```

## HU-05 — Seguimiento por métricas y notificación

Como responsable de selección, quiero consultar métricas y notificar cambios para hacer seguimiento al proceso.

```gherkin
Escenario: Consulta de métricas
  Dado que el responsable tiene acceso autenticado
  Cuando consulta el panel de métricas
  Entonces el sistema muestra el total de postulaciones
  Y muestra agrupaciones por estado y carrera

Escenario: Fallo en el envío de correo
  Dado que un analista cambia el estado de un postulante
  Y el servidor de correo no está disponible
  Cuando el sistema intenta notificar el cambio
  Entonces conserva la decisión y el historial
  Y registra el fallo de notificación sin interrumpir el proceso
```

