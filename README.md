# Ruleta

Mod de Fabric para Minecraft **26.3**: la ruleta de Hombrosafío como mod independiente.

- Giro sincronizado para todos los jugadores (HUD, sonido, aguja con física).
- Anuncios en chat con brillo animado, títulos, castigos y temporizador.
- Ruleta amarilla: 21 cambios de dificultad que funcionan solo con Minecraft vanilla.
- Pantallas: selector, formularios, ajustes, cambios activos y panel.

## Comandos (nivel gamemaster)
`/ruleta`, `/ruleta ajustes|cambios|panel`, `/ruleta <color> <texto>`, `/ruleta amarillo <cambio>`, `/ruleta amarillo quitar <cambio>`, `/ruleta cancelar`.

## Compilar
Java 25: `./gradlew build` → `build/libs/`. También se compila en GitHub Actions.

## Alcance
No incluye el contenido propio de Hombrosafío (Momentos Revil, bunkers, casino…). Las pantallas de selector/ajustes/panel están reconstruidas a partir de un video de referencia.
