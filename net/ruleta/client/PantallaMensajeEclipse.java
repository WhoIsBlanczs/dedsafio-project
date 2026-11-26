package net.ruleta.client;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.ruleta.Momentos;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiCampo;
import net.ruleta.client.ui.UiPantalla;

public class PantallaMensajeEclipse extends UiPantalla {
   public static final int MAX = 400;
   private final int indice;
   private final Consumer<String> alGuardar;
   private String valor;
   private UiCampo campo;

   public PantallaMensajeEclipse(Screen padre, int indice, String valor, Consumer<String> alGuardar) {
      super("Eclipse", padre);
      this.indice = indice;
      this.valor = valor == null ? "" : valor;
      this.alGuardar = alGuardar;
   }

   protected void init() {
      if (this.campo != null) {
         this.valor = this.campo.getValue();
      }

      this.panel(780, 270);
      this.cabecera(true);
      this.subtitulo("Mensaje " + (this.indice + 1) + " de 4 · vacío = el mensaje por defecto");
      int x = this.px + 20;
      int w = this.pw - 40;
      this.campo = (UiCampo)this.addRenderableWidget(new UiCampo(this.font, x, this.py + 74, w, 24, Component.literal("Mensaje")));
      this.campo.setMaxLength(400);
      this.campo.setValue(this.valor.isEmpty() ? Momentos.ECLIPSE_TEXTOS[this.indice] : this.valor);
      this.campo.setCursorPosition(0);
      this.campo.setHighlightPos(0);
      this.setInitialFocus(this.campo);
      int yb = this.py + this.ph - 14 - 30;
      this.addRenderableWidget(new UiBoton(this.px + this.pw - 24 - 130, yb, 130, 30, "Guardar", UiBoton.Estilo.PRIMARIO, this::guardar));
      this.addRenderableWidget(new UiBoton(this.px + this.pw - 24 - 130 - 10 - 100, yb, 100, 30, "Cancelar", UiBoton.Estilo.NORMAL, this::onClose));
      this.addRenderableWidget(
         new UiBoton(x, yb, 120, 30, "Por defecto", UiBoton.Estilo.NORMAL, () -> this.campo.setValue(Momentos.ECLIPSE_TEXTOS[this.indice]))
      );
   }

   private void guardar() {
      String t = this.campo.getValue().trim();
      this.alGuardar.accept(t.equals(Momentos.ECLIPSE_TEXTOS[this.indice]) ? "" : t);
      this.onClose();
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      int x = this.px + 20;
      int w = this.pw - 40;
      String titulo = Momentos.ECLIPSE_TITULOS[this.indice].replace("Momento ReviiL — ", "");
      Ui.textoN(g, this.font, titulo, x, this.py + 52, 16762967, 1.0F);
      Ui.texto(g, this.font, this.campo.getValue().length() + " / 400", x + w - 60, this.py + 52, 9344683, 1.0F);
      Ui.texto(g, this.font, "Así se verá:", x, this.py + 110, 9344683, 1.0F);
      String t = this.campo.getValue().replace("{bunker}", "X: ? // Z: ? (las del bunker)");
      List<String> ls = Ui.lineas(this.font, t, w, 5);

      for (int k = 0; k < ls.size(); k++) {
         Ui.texto(g, this.font, ls.get(k), x, this.py + 124 + k * 10, 15330549, 0.95F);
      }

      if (this.indice == 3) {
         Ui.texto(g, this.font, "{bunker} se cambia por las coordenadas del bunker.", x, this.py + this.ph - 14 - 54, 9344683, 0.9F);
      }
   }
}
