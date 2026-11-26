package net.ruleta.tragamonedas.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.ruleta.client.JugadoresPantalla;
import net.ruleta.client.dedsafio.PanelEnvio;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiPantalla;
import net.ruleta.dedsafio.DulceAzarAccionPayload;

public class PantallaDulceAzar extends UiPantalla {
   private String objetivo = "";

   public PantallaDulceAzar(Screen padre) {
      super("Dulce azar", padre);
   }

   private String textoObjetivo() {
      return this.objetivo.isEmpty() ? "TODOS LOS JUGADORES" : "SOLO: " + this.objetivo;
   }

   private void mandar(int accion) {
      PanelEnvio.enviar(new DulceAzarAccionPayload(accion, this.objetivo, 0.0F, 0.0F, 0.0F));
   }

   protected void init() {
      this.panel(300, 340);
      this.cabecera(true);
      this.subtitulo("Dimensión Dulce Azar: viaje y escena");
      int y = this.yContenido() + 14;
      int izq = this.px + 20;
      int ancho = this.pw - 40;
      ((UiBoton)this.addRenderableWidget(
            new UiBoton(
               izq,
               y + 14,
               ancho,
               26,
               this.textoObjetivo(),
               UiBoton.Estilo.NORMAL,
               () -> Minecraft.getInstance().setScreen(new JugadoresPantalla(this, true, nombre -> this.objetivo = nombre))
            )
         ))
         .ayuda("Elige un jugador (o todos) para el viaje y la visibilidad");
      ((UiBoton)this.addRenderableWidget(new UiBoton(izq, y + 48, ancho / 2 - 4, 34, "Ir a Dulce Azar", UiBoton.Estilo.PRIMARIO, () -> {
         this.mandar(0);
         Minecraft.getInstance().setScreen(null);
      }))).ayuda("Lleva a la playa al jugador elegido (o a todos)");
      ((UiBoton)this.addRenderableWidget(new UiBoton(izq + ancho / 2 + 4, y + 48, ancho / 2 - 4, 34, "Volver", UiBoton.Estilo.NORMAL, () -> this.mandar(1))))
         .ayuda("Los devuelve a donde estaban antes de entrar");
      ((UiBoton)this.addRenderableWidget(new UiBoton(izq, y + 126, ancho / 2 - 4, 34, "Iniciar escena", UiBoton.Estilo.PRIMARIO, () -> this.mandar(5))))
         .ayuda("Empieza la canción de Dulce Azar para los que están dentro; al terminar llega la ola");
      this.addRenderableWidget(new UiBoton(izq + ancho / 2 + 4, y + 126, ancho / 2 - 4, 34, "Parar escena", UiBoton.Estilo.PELIGRO, () -> this.mandar(6)));
      ((UiBoton)this.addRenderableWidget(new UiBoton(izq, y + 190, ancho / 2 - 4, 34, "Ocultar jugadores", UiBoton.Estilo.NORMAL, () -> this.mandar(7))))
         .ayuda("Cada jugador se ve solo a sí mismo dentro de Dulce Azar");
      ((UiBoton)this.addRenderableWidget(
            new UiBoton(izq + ancho / 2 + 4, y + 190, ancho / 2 - 4, 34, "Hacer visible", UiBoton.Estilo.PRIMARIO, () -> this.mandar(8))
         ))
         .ayuda("El jugador elegido se ve para todos. Con 'Todos los jugadores' elegido, todos vuelven a verse");
      this.addRenderableWidget(new UiBoton(this.px + this.pw - 110, this.py + this.ph - 38, 90, 26, "Volver", UiBoton.Estilo.FANTASMA, this::onClose));
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      int y = this.yContenido() + 14;
      int izq = this.px + 20;
      Ui.texto(g, this.font, "VIAJE", izq, y, 9344683, 1.0F);
      Ui.texto(g, this.font, "ESCENA", izq, y + 112, 9344683, 1.0F);
      Ui.texto(g, this.font, "VISIBILIDAD", izq, y + 176, 9344683, 1.0F);
      Ui.texto(g, this.font, "Solo los operadores pueden usar este panel.", izq, this.py + this.ph - 30, 9344683, 1.0F);
   }
}
