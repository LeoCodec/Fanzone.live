# FanZone Live — v3: Chat en tiempo real + Crear Evento rediseñado

Descomprime sobre tu repo (igual que siempre):
  Expand-Archive -Path "$env:USERPROFILE\Downloads\FanzoneLive_v3_chat.zip" -DestinationPath "C:\Users\Leo\AndroidStudioProjects\Fanzone.live" -Force
Luego: Sync  ->  Run.

## IMPORTANTE: publica las nuevas reglas de Firestore
Firebase Console -> Firestore -> Reglas -> pega TODO el contenido de firestore.rules -> Publicar.
(Trae la regla nueva del chat. Sin esto, el chat no deja escribir.)

## CHAT EN TIEMPO REAL (demo de 2 teléfonos)
Flujo para la prueba con el profe:
  1. Teléfono A (HOST): crea un evento (o usa uno tuyo).
  2. Teléfono B (USUARIO, otra cuenta): abre ese evento -> "SOLICITAR UNIRSE".
  3. Teléfono A: Perfil -> Panel de Anfitrión -> ACEPTAR la solicitud de B.
  4. Teléfono B: vuelve a abrir el evento -> "CHAT DEL EVENTO" -> ya entra.
  5. Ambos escriben y los mensajes aparecen al instante en los dos teléfonos.
El candado: si B NO ha sido aceptado, el chat le dice "El anfitrión debe aceptarte 🔒".

## Tema Material (arreglo de fondo)
Se cambió el tema a Material Bridge para que la barra inferior y los componentes
nuevos no truenen. No afecta tus botones/inputs (siguen con su diseño).

## CREAR EVENTO REDISEÑADO
- Slider de cupo con número grande en vivo.
- 3 pickers de tipo de espacio (Hogar / Establecimiento / Espacio Deportivo).
- Botón grande "CREAR EVENTO Y TRANSMITIR LIVE".
