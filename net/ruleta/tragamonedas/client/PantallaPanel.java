package net.ruleta.tragamonedas.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.ruleta.client.RedCliente;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiPantalla;
import net.ruleta.estatua.PedestalPedirPayload;
import net.ruleta.tragamonedas.CasinoPedirPayload;

public class PantallaPanel extends UiPantalla {
   public PantallaPanel(Screen padre) {
      super("Panel", padre);
   }

   protected void init() {
      this.panel(600, 300);
      this.cabecera(true);
      this.subtitulo("Configura aquí lo que vaya añadiendo el mod");
      int y = this.yContenido() + 16;
      this.addRenderableWidget(
         new UiBoton(this.px + 20, y, 170, 56, "Casino", UiBoton.Estilo.PRIMARIO, () -> RedCliente.enviar(new CasinoPedirPayload()))
            .ayuda("Tragamonedas: símbolos, probabilidades y premios")
      );
      this.addRenderableWidget(
         new UiBoton(
               this.px + 20 + 170 + 14, y, 170, 56, "Dulce azar", UiBoton.Estilo.PRIMARIO, () -> Minecraft.getInstance().setScreen(new PantallaDulceAzar(this))
            )
            .ayuda("Dimensión DEDsafío: ir, volver, olas y escena")
      );
      this.addRenderableWidget(
         new UiBoton(this.px + 20 + 368, y, 170, 56, "Nutria Pedestal", UiBoton.Estilo.PRIMARIO, () -> RedCliente.enviar(new PedestalPedirPayload()))
            .ayuda("Comercio del pedestal de nutrias: qué se da, qué se recibe y cuántos")
      );
      this.addRenderableWidget(new UiBoton(this.px + this.pw - 110, this.py + this.ph - 38, 90, 26, "Volver", UiBoton.Estilo.FANTASMA, this::onClose));
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      Ui.texto(g, this.font, "Más opciones próximamente…", this.px + 20, this.py + this.ph - 30, 9344683, 1.0F);
   }
}
