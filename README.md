# Ruleta – Menú G (Minecraft 26.3, Fabric)

Recreación del menú que se abre con la **G** en *DEDsafío 4 Ruleta Fan Project* (v1.3.0, para 1.21.1), hecha a partir del video de referencia para **Minecraft 26.3 + Fabric**.

## Cómo compilarlo

Necesitas **Java 25** (JDK) e internet. En esta carpeta ejecuta:

```
gradlew build        (Windows)
./gradlew build      (Linux / macOS)
```

El mod queda en `build/libs/ruleta-menu-g-26.3-1.0.0.jar`. Ponlo en `mods/` junto con **Fabric API 0.166.2+26.3** (o más nueva) y **Fabric Loader 0.20.1** o más nuevo. Va en el servidor y en los clientes.

Las versiones de Loom, Loader y Fabric API son las del `fabric-example-mod` oficial para 26.3. Si Gradle no encuentra alguna, cámbiala en `gradle.properties` por la que indique https://fabricmc.net/develop.

## Qué incluye

- **Ruleta (G)**: las 8 casillas con las texturas originales (la casilla bajo el ratón sale hacia afuera y su nombre aparece en su color). Abajo están Ajustes, Cambios activos, Bunkers, Panel y Detener ruleta, con sus textos de ayuda.
- **Formulario de cada casilla**: título, mensaje (hasta 1000 caracteres), ¿para quién?, tamaño mini/normal y la sección 5/6 de cada tipo (temporizador, qué sale, castigo o extras). LANZAR manda el aviso a los jugadores.
- **Ajustes**: las 12 categorías y los 112 ajustes con sus textos y valores por defecto. Tiene buscador, deslizadores, botones − / + (Mayús = de 10 en 10), interruptores, el punto azul y Restablecer sección/todo. Se guardan en `config/ruleta_ajustes.json` y se sincronizan con todos.
- **Cambios activos**: la lista con el botón Desactivar.
- **Bunkers**: la pantalla vacía tal cual el original.
- **Panel**: Casino, Dulce azar y Pedestal de intercambio. Se guardan en `config/ruleta_casino.json` y `config/ruleta_pedestal_intercambio.json`.
- La interfaz se dibuja en un lienzo de 1280×720 que se escala a la ventana, así que se ve igual con cualquier escala de interfaz.

En un servidor dedicado, solo los administradores (nivel de permisos 2 o más) pueden cambiar ajustes y lanzar casillas. En un mundo propio o abierto en LAN, puede cualquiera.

## Con Hombrosafío

Si el servidor también tiene **Hombrosafío** (Micosafio v5), al pulsar LANZAR gira la **ruleta de Hombrosafío** en el color de la casilla, con sus sonidos. Cuando se para, sale el aviso de este menú.

En la casilla roja, «Momentos Reviil» muestra los **14 Momentos Revil de Hombrosafío** (refugio, intercambio de posiciones, los dos gases tóxicos, dimensión jurásica, bombas nucleares, lluvia ácida, bioma, votación, creepers eléctricos, Gleba, Limbo, agua del Limbo y montaña). Se pueden elegir varios. Los que necesitan datos piden el plazo, el bioma o las muertes. Con «REVIIL: ENCENDIDO», al parar la ruleta se activan de verdad en Hombrosafío. «MENSAJE» no pone el cielo rojo; «ANIMACIÓN» y «CIELO» sí.

En la ruleta roja, MENSAJE, ANIMACIÓN y CIELO se encienden y apagan por separado: el aviso de este menú, la animación de Revil de Hombrosafío y el cielo rojo.

**Cambios de dificultad**: son los 31 de Hombrosafío. Al parar la ruleta se activan en Hombrosafío, así que salen en los cambios activos de la M. «Cambios activos» del menú muestra esos mismos y «Desactivar» los quita en Hombrosafío.

**Pedestal de intercambio**: edita los comercios del Pedestal de canje de Hombrosafío. Muestra los que trae (4 fichas azules → 1 ficha naranja y 1 Dilitio → 1 ficha azul) y al guardar el pedestal usa los nuevos.

**Casino**: edita los premios de la tragamonedas de **CasinoHS**. Muestra sus 21 premios con el peso, la cantidad, el jackpot y los libros encantados, y la ficha con la que se juega. Al guardar, la máquina entrega y muestra los nuevos. Máximo 21 premios.

«Detener ruleta» cancela la ruleta de Hombrosafío y termina sus Momentos Revil y su cielo.

Sin Hombrosafío, el menú funciona igual pero el aviso sale sin girar ninguna ruleta y los Momentos Revil no se activan.

## Lo que no es idéntico

- Este mod es **solo el menú**. Los ajustes se guardan y se sincronizan, pero no hay bunkers, agujeros negros, tornados, Herobrine, meteoritos, etc. que los usen, porque eso es el resto del mod original.
- En el video no se ven **Momento Reviil**, **Mobs nuevos**, **Cambios**, las pestañas **Premios/Cobro** del casino ni el interior de **Dulce azar**. Esas partes están hechas con el mismo estilo, pero su contenido es una reconstrucción.
- El texto de ayuda de «Detener ruleta» y algunas descripciones que en el video aparecen cortadas con «...» los completé yo.
- El aviso que aparece en pantalla al pulsar LANZAR es diseño propio, porque en el video no se ve.

## Si falla la compilación

La API de 26.3 se comprobó contra el código de Fabric API 26.3 y su documentación oficial. Estos puntos vienen de versiones anteriores y podrían haber cambiado de nombre:

- `MultiLineEditBox.builder()...build(...)` en `PantallaLanzar`
- `EditBox.setHint/setBordered`
- `GuiGraphicsExtractor.blit(..., color)` e `item(...)`
- `PlayerInfo.getProfile().name()` en `PantallaLanzar.jugadoresConectados`
- `MouseButtonEvent.hasShiftDown()`

Si `gradlew build` da error, pásame el mensaje y lo corrijo.

## Licencia y créditos

Basado en **DEDsafío 4 Ruleta Fan Project** de *DarkPlayer y colaboradores*, licencia **CC BY-NC-SA 4.0** (texto en `LICENSE`). Las texturas de la ruleta, los íconos y las fuentes de píxeles son las del mod original. Serie DEDsafío 4: Eufonia Studios.

Esta adaptación se distribuye con la misma licencia: no comercial, dando crédito y compartiendo igual.
