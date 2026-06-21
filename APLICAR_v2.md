# FanZone Live — Mejora visual v2 (Home lleno + más deportes + bottom nav)

Descomprime ESTE zip sobre tu repo (igual que la vez pasada):
  Expand-Archive -Path "$env:USERPROFILE\Downloads\FanzoneLive_v2_diseno.zip" -DestinationPath "C:\Users\Leo\AndroidStudioProjects\Fanzone.live" -Force
Luego: Sync Project with Gradle Files  →  Run ▶

## Qué cambia
- Home se llena solo: la primera vez siembra ~14 eventos de muchos deportes en Firestore
  (datos reales, así tu CRUD sigue siendo legítimo). Son tuyos, los puedes editar/borrar.
- Deportes nuevos: Fútbol, Basket, F1, Box, UFC/MMA, Béisbol, Jiu-jitsu, Hockey, Tenis, NFL + Otro.
- Filtros (chips) que SÍ filtran en pantalla, sin depender de la API externa.
- Barra de navegación inferior (Home · Buscar · Crear · Solicitudes · Perfil).
- Tarjetas más pro: franja roja lateral + barra de cupo (ocupados/total).
- Login con saludo "¡Hola, fanático!".

## Nota
- Si ya tenías eventos en Firestore, NO siembra (solo siembra si está vacío).
- Si quieres re-sembrar: borra la colección "events" en Firebase Console y vuelve a abrir la app.
