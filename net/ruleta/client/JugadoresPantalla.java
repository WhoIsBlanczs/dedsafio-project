package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.ruleta.client.ui.Suave;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiCampo;
import net.ruleta.client.ui.UiFila;
import net.ruleta.client.ui.UiLista;
import net.ruleta.client.ui.UiPantalla;

public class JugadoresPantalla extends UiPantalla {
   private final boolean conTodos;
   private final Consumer<String> alElegir;
   private final UiLista lista = new UiLista();
   private UiCampo buscar;

   public JugadoresPantalla(Screen padre, boolean conTodos, Consumer<String> alElegir) {
      super("Elegir jugador", padre);
      this.conTodos = conTodos;
      this.alElegir = alElegir;
   }

   private List<PlayerInfo> jugadores() {
      List<PlayerInfo> out = new ArrayList<>();
      if (Minecraft.getInstance().getConnection() != null) {
         out.addAll(Minecraft.getInstance().getConnection().getOnlinePlayers());
      }

      out.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.getProfile().getName(), b.getProfile().getName()));
      return out;
   }

   protected void init() {
      this.panel(420, 420);
      this.cabecera(true);
      this.subtitulo("Jugadores conectados");
      String previo = this.buscar == null ? "" : this.buscar.getValue();
      this.buscar = (UiCampo)this.addRenderableWidget(new UiCampo(this.font, this.px + 16, this.py + 50, this.pw - 32, 24, Component.literal("Buscar")));
      this.buscar.setHint(Component.literal("Buscar jugador…"));
      this.buscar.setValue(previo);
      this.buscar.setResponder(s -> this.reconstruir(false));
      this.lista.x = this.px + 16;
      this.lista.y = this.py + 84;
      this.lista.w = this.pw - 32;
      this.lista.h = this.ph - 84 - 56;
      this.addRenderableWidget(new UiBoton(this.px + this.pw - 16 - 110, this.py + this.ph - 14 - 28, 110, 28, "Volver", UiBoton.Estilo.NORMAL, this::onClose));
      this.reconstruir(true);
   }

   private void reconstruir(boolean animar) {
      String q = this.buscar == null ? "" : this.buscar.getValue().trim().toLowerCase(Locale.ROOT);
      List<UiFila> filas = new ArrayList<>();
      if (this.conTodos && q.isEmpty()) {
         filas.add(new JugadoresPantalla.Fila("", null));
      }

      for (PlayerInfo i : this.jugadores()) {
         String n = i.getProfile().getName();
         if (q.isEmpty() || n.toLowerCase(Locale.ROOT).contains(q)) {
            filas.add(new JugadoresPantalla.Fila(n, i));
         }
      }

      this.lista.poner(filas, animar);
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      this.lista.dibujar(g, this.font, mx, my);
      if (this.lista.tamano() == 0) {
         Ui.textoCentro(g, this.font, "Nadie coincide", this.px + this.pw / 2, this.py + 110, 9344683, 1.0F, false);
      }
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      return super.mouseClicked(mx, my, boton) || this.lista.mouseClicked(mx, my, boton);
   }

   public boolean mouseReleased(double mx, double my, int boton) {
      return this.lista.mouseReleased() || super.mouseReleased(mx, my, boton);
   }

   public boolean mouseScrolled(double mx, double my, double deltaX, double delta) {
      return this.lista.mouseScrolled(mx, my, deltaX, delta) || super.mouseScrolled(mx, my, deltaX, delta);
   }

   private final class Fila extends UiFila {
      private final String nombre;
      private final PlayerInfo info;
      private final Suave hover = new Suave();

      Fila(String nombre, PlayerInfo info) {
         this.nombre = nombre;
         this.info = info;
         this.alto = 38;
      }

      @Override
      public void dibujar(GuiGraphics g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         float h = this.hover.hacia(hover ? 1.0F : 0.0F, 14.0F);
         Ui.cajaBorde(g, x, y, w, this.alto, 7, Ui.c(Ui.mezcla(1711656, 2369846, h * 0.7F)), Ui.c(Ui.mezcla(3028037, 7113983, h * 0.6F)));
         boolean todos = this.nombre.isEmpty();
         if (!todos && this.info != null) {
            Ui.caja(g, x + 7, y + 6, 26, 26, 5, Ui.c(Ui.mezcla(3028037, 7113983, h * 0.8F)));
            PlayerFaceRenderer.draw(g, this.info.getSkin(), x + 9, y + 8, 22);
         } else {
            Ui.caja(g, x + 8, y + 7, 24, 24, 12, Ui.c(16762967, 0.2F));
            Ui.textoCentro(g, f, "*", x + 20, y + 15, 16762967, 1.0F, true);
         }

         Ui.textoN(g, f, todos ? "Todos" : this.nombre, x + 42, y + 15, 15330549, 1.0F);
      }

      @Override
      public boolean clic(double mx, double my, int boton, int x, int y, int w) {
         if (boton != 0) {
            return false;
         } else {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.2F, 0.5F));
            JugadoresPantalla.this.alElegir.accept(this.nombre);
            JugadoresPantalla.this.onClose();
            return true;
         }
      }
   }
}
