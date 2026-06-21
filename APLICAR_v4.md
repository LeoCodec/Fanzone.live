# FanZone Live — v4: Boleto QR + Aporte/Cuota del host

Descomprime sobre tu repo:
  Expand-Archive -Path "$env:USERPROFILE\Downloads\FanzoneLive_v4_qr.zip" -DestinationPath "C:\Users\Leo\AndroidStudioProjects\Fanzone.live" -Force
Luego: Sync (baja la librería ZXing del QR)  ->  Run.

## Qué trae
- "Mi Entrada (QR)": boleto con código QR para usuarios aceptados / host.
  En el Detalle del evento aparece el botón "🎟️ MI ENTRADA (QR)".
- Aporte / Cuota por evento: en Crear Evento ahora defines "Gratis",
  "Lleva botana 🍿", "Coopera $50", etc. Se muestra en el detalle.
- Nueva dependencia: com.google.zxing:core:3.5.3 (se descarga sola al Sync).

## Candado
El botón de boleto y el de chat solo funcionan si el host te aceptó
(o si eres el host). Si no, sale "El anfitrión debe aceptarte 🔒".
