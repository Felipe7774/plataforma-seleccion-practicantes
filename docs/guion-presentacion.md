# Guion de presentación

## Objetivo

Demostrar que la plataforma permite registrar postulantes y que el equipo de selección puede revisar y decidir sobre las postulaciones con trazabilidad.

## Recorrido sugerido (5 minutos)

1. Presentar el problema: centralizar postulaciones de practicantes y evitar que la información se pierda entre correos o archivos aislados.
2. Abrir la aplicación pública en la IP de AWS EC2 y mostrar la portada.
3. Seleccionar **Postularme**, completar los datos y adjuntar un PDF. Explicar que el formulario valida correo, semestre, tipo de archivo y tamaño máximo de 5 MB.
4. Mostrar la confirmación del registro y explicar que la postulación inicia con estado `PENDING`.
5. Entrar por **Equipo de selección** con las credenciales administrativas configuradas fuera del repositorio.
6. Mostrar el panel: métricas, filtro, búsqueda, listado y detalle del postulante.
7. Seleccionar un postulante y marcarlo como `VIABLE`. Para `NOT_VIABLE`, mostrar que el comentario es obligatorio y que se conserva en el historial.
8. Cerrar mostrando el repositorio: ramas, Pull Request, pruebas automatizadas y flujo de GitHub Actions.

## Arquitectura para explicar

```text
Navegador
    │ HTTP
Nginx + React (contenedor frontend)
    │ /api
Spring Boot (contenedor backend)
    ├── PostgreSQL: postulantes, usuarios, tokens e historial
    ├── Volumen Docker: hojas de vida PDF
    └── Mailpit/SMTP: notificaciones asíncronas
```

Todo se ejecuta en una instancia AWS EC2. El puerto 80 es el único expuesto públicamente; PostgreSQL permanece dentro de la red de Docker.

## Puntos técnicos clave

- Arquitectura por capas: dominio, aplicación e infraestructura.
- JWT con roles `ADMIN` y `ANALYST`; refresh tokens almacenados como hash.
- Registro público con `multipart/form-data` y validación de PDF.
- Estados `PENDING`, `VIABLE` y `NOT_VIABLE`, con historial de cambios.
- Rate limiting para registro e inicio de sesión.
- Pruebas de backend y frontend, lint, build y CI con GitHub Actions.
- Secretos de producción en el archivo `.env` de AWS, nunca en Git.

## Lista antes de presentar

- Confirmar que la instancia EC2 esté en estado **Running**.
- Abrir la aplicación y probar el botón **Equipo de selección** antes de empezar.
- Tener un PDF de prueba menor de 5 MB disponible.
- Tener a mano las credenciales administrativas, sin mostrarlas en pantalla.
- Abrir GitHub en la pestaña de Actions o del Pull Request para mostrar la trazabilidad.
- Mantener una postulación de demostración por si la red falla durante el registro en vivo.

## Plan B

Si falla el registro durante la presentación, usar la postulación de prueba ya visible en el panel para demostrar filtros, detalle, métricas, decisión e historial. La demostración conserva el flujo funcional principal.
