# FanZone Live — Archivos de la Etapa 3 (CRUD completo + rediseño)

Reemplaza / agrega estos archivos en tu repo clonado
`C:\Users\Leo\AndroidStudioProjects\Fanzone.live`
(respetando exactamente las mismas rutas) y luego en Android Studio:
**File → Sync Project with Gradle Files**.

## Qué se agregó / cambió

CRUD COMPLETO (Agregar, Recuperar, Actualizar, Borrar):
- CreateEventActivity.kt  → ahora también EDITA (UPDATE) si recibe un eventId
- HostPanelActivity.kt     → lista tus eventos con botones EDITAR y BORRAR (DELETE),
                             y administra solicitudes (Aceptar/Rechazar = UPDATE)
- MyEventAdapter.kt        → (nuevo) tarjetas de "mis eventos"
- RequestAdapter.kt        → (nuevo) tarjetas de solicitudes pendientes
- NotificationsActivity.kt → ahora SÍ muestra el estado de tus solicitudes
- NotificationAdapter.kt   → (nuevo)
- EventDetailActivity.kt   → guarda solicitud con más datos y bloquea auto-solicitud
- EventAdapter.kt          → pasa hostId/sport al detalle

BUG CORREGIDO:
- SportActivity.kt → getString("away") -> getString("name") (el equipo visitante salía mal)

SEGURIDAD:
- firestore.rules → reglas de producción (pégalas en Firebase Console → Firestore → Reglas)

REDISEÑO (se ve más "producto real"):
- drawable: bg_card, bg_button (degradado), bg_header_gradient, bg_chip_accept, bg_chip_reject, bg_input
- Tarjetas redondeadas, botones con degradado, headers con degradado rojo→negro

## Generar el APK (entregable de la rúbrica)
En la terminal del proyecto:
    .\gradlew assembleDebug
El APK queda en:
    app\build\outputs\apk\debug\app-debug.apk
