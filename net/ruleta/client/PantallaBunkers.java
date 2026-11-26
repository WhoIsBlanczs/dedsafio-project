package net.ruleta.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.ruleta.bunker.Bunkers;
import net.ruleta.client.bunker.BunkerMarcador;
import net.ruleta.client.ui.Iconos;
import net.ruleta.client.ui.Suave;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiFila;
import net.ruleta.client.ui.UiLista;
import net.ruleta.client.ui.UiPantalla;

public class PantallaBunkers extends UiPantalla {
   private final Consumer<String> alElegir;
   private final String actual;
   private final List<Bunkers.Bunker> lista = new ArrayList<>();
   private final UiLista ui = new UiLista();

   public PantallaBunkers(Screen padre, String actual, Consumer<String> alElegir) {
      super("Elegir bunker", padre);
      this.actual = actual == null ? "" : actual;
      this.alElegir = alElegir;
   }

   public static String texto(String clave) {
      for (Bunkers.Bunker b : BunkerMarcador.todos()) {
         if (b.clave().equals(clave)) {
            return "Bunker: " + etiqueta(b);
         }
      }

      return "Bunker: al azar (o uno nuevo)";
   }

   private static double distancia(Bunkers.Bunker b) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.level != null) {
         double d = Math.sqrt(mc.player.distanceToSqr(b.mx(), mc.player.getY(), b.mz()));
         return b.dimension().equals(mc.level.dimension().location().toString()) ? d : Double.MAX_VALUE;
      } else {
         return 0.0;
      }
   }

   private static String distanciaTexto(double d) {
      if (d == Double.MAX_VALUE) {
         return "Otra dimensión";
      } else {
         return d >= 1000.0 ? String.format(Locale.ROOT, "%.1f km", d / 1000.0) : Math.round(d) + " m";
      }
   }

   private static String etiqueta(Bunkers.Bunker b) {
      return b.nombre() + " · " + distanciaTexto(distancia(b));
   }

   protected void init() {
      this.panel(520, 400);
      this.cabecera(true);
      this.subtitulo("Distancia a ti, de más cerca a más lejos");
      this.lista.clear();
      this.lista.addAll(BunkerMarcador.todos());
      this.lista.sort(Comparator.comparingDouble(PantallaBunkers::distancia));
      this.ui.x = this.px + 14;
      this.ui.y = this.py + 52;
      this.ui.w = this.pw - 28;
      this.ui.h = this.ph - 52 - 56;
      List<UiFila> filas = new ArrayList<>();
      filas.add(new PantallaBunkers.Fila(null));

      for (Bunkers.Bunker b : this.lista) {
         filas.add(new PantallaBunkers.Fila(b));
      }

      this.ui.poner(filas, true);
      this.addRenderableWidget(new UiBoton(this.px + this.pw - 14 - 110, this.py + this.ph - 14 - 28, 110, 28, "Volver", UiBoton.Estilo.NORMAL, this::onClose));
   }

   private void elegir(String clave) {
      this.alElegir.accept(clave);
      this.onClose();
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      this.ui.dibujar(g, this.font, mx, my);
      Ui.texto(
         g,
         this.font,
         this.lista.isEmpty() ? "No hay ningún bunker: se construirá uno nuevo." : "«Al azar» elige uno cualquiera al lanzar la ruleta.",
         this.px + 18,
         this.py + this.ph - 30,
         9344683,
         1.0F
      );
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      return super.mouseClicked(mx, my, boton) || this.ui.mouseClicked(mx, my, boton);
   }

   public boolean mouseReleased(double mx, double my, int boton) {
      return this.ui.mouseReleased() || super.mouseReleased(mx, my, boton);
   }

   public boolean mouseScrolled(double mx, double my, double deltaX, double delta) {
      return this.ui.mouseScrolled(mx, my, deltaX, delta) || super.mouseScrolled(mx, my, deltaX, delta);
   }

   private final class Fila extends UiFila {
      private final Bunkers.Bunker b;
      private final Suave hover = new Suave();
      private final Suave sel = new Suave();

      Fila(Bunkers.Bunker b) {
         this.b = b;
         this.alto = 44;
      }

      private String clave() {
         return this.b == null ? "" : this.b.clave();
      }

      @Override
      public void dibujar(GuiGraphics g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         float h = this.hover.hacia(hover ? 1.0F : 0.0F, 14.0F);
         boolean elegido = PantallaBunkers.this.actual.equals(this.clave());
         float s = this.sel.hacia(elegido ? 1.0F : 0.0F, 14.0F);
         Ui.cajaBorde(g, x, y, w, this.alto, 7, Ui.c(Ui.mezcla(1711656, 2369846, h * 0.6F)), Ui.c(Ui.mezcla(Ui.mezcla(3028037, 7113983, h * 0.5F), 7113983, s)));
         Ui.caja(g, x + 10, y + 10, 24, 24, 6, Ui.c(7113983, 0.15F));
         Iconos.dibujar(g, Iconos.BUNKER, x + 14, y + 16, 7113983, 1.0F);
         if (this.b == null) {
            Ui.textoN(g, f, "Al azar", x + 44, y + 11, 15330549, 1.0F);
            Ui.texto(g, f, "Uno cualquiera de los que hay, o uno nuevo si no hay ninguno", x + 44, y + 24, 9344683, 1.0F);
         } else {
            Ui.textoN(g, f, Ui.recortar(f, this.b.nombre(), w - 180), x + 44, y + 11, 15330549, 1.0F);
            Ui.texto(g, f, "X " + (int)Math.floor(this.b.mx()) + "   Z " + (int)Math.floor(this.b.mz()), x + 44, y + 24, 9344683, 1.0F);
            Ui.textoDerecha(g, f, PantallaBunkers.distanciaTexto(PantallaBunkers.distancia(this.b)), x + w - 40, y + 17, 15330549, 1.0F);
         }

         if (s > 0.02F) {
            Ui.caja(g, x + w - 28, y + 14, 16, 16, 8, Ui.c(7113983, s));
            Iconos.dibujar(g, Iconos.CHECK, x + w - 24, y + 18, 16777215, s);
         }
      }

      @Override
      public boolean clic(double mx, double my, int boton, int x, int y, int w) {
         if (boton != 0) {
            return false;
         } else {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.2F, 0.5F));
            PantallaBunkers.this.elegir(this.clave());
            return true;
         }
      }
   }
}
