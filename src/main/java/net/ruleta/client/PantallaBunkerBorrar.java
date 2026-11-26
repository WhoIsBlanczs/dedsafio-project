package net.ruleta.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.ruleta.BunkerAccionPayload;
import net.ruleta.client.ui.Iconos;
import net.ruleta.client.ui.Suave;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiFila;
import net.ruleta.client.ui.UiLista;
import net.ruleta.client.ui.UiPantalla;

public class PantallaBunkerBorrar extends UiPantalla {
   private final List<PantallaBunkerBorrar.Datos> datos = new ArrayList<>();
   private final Set<String> confirmando = new HashSet<>();
   private final UiLista lista = new UiLista();

   public PantallaBunkerBorrar(Screen padre, String crudo) {
      super("Bunkers", padre);
      this.leer(crudo);
   }

   private void leer(String crudo) {
      this.datos.clear();

      for (String linea : crudo.split("\n")) {
         String[] p = linea.split("\t", -1);
         if (p.length >= 8) {
            try {
               this.datos
                  .add(
                     new PantallaBunkerBorrar.Datos(
                        p[0], p[1], p[2], Integer.parseInt(p[3]), Integer.parseInt(p[4]), Integer.parseInt(p[5]), Long.parseLong(p[6]), Integer.parseInt(p[7])
                     )
                  );
            } catch (NumberFormatException var8) {
            }
         }
      }

      this.datos
         .sort(
            Comparator.<PantallaBunkerBorrar.Datos>comparingInt(d -> d.porcentaje() >= 0 ? 1 : 0)
               .thenComparingLong(d -> d.distancia() < 0L ? Long.MAX_VALUE : d.distancia())
         );
   }

   public void actualizar(String crudo) {
      this.leer(crudo);
      this.reconstruir(false);
   }

   private void reconstruir(boolean animar) {
      List<UiFila> filas = new ArrayList<>();

      for (PantallaBunkerBorrar.Datos d : this.datos) {
         filas.add(new PantallaBunkerBorrar.FilaBunker(d));
      }

      this.lista.poner(filas, animar);
   }

   protected void init() {
      this.panel(620, 430);
      this.cabecera(true);
      this.subtitulo(
         this.datos.isEmpty()
            ? "No hay ningún bunker construido"
            : this.datos.size() + (this.datos.size() == 1 ? " bunker" : " bunkers") + " · del más cercano al más lejano"
      );
      this.lista.x = this.px + 14;
      this.lista.y = this.py + 52;
      this.lista.w = this.pw - 28;
      this.lista.h = this.ph - 52 - 58;
      this.addRenderableWidget(
         new UiBoton(this.px + this.pw - 14 - 110, this.py + this.ph - 14 - 28, 110, 28, "Cerrar", UiBoton.Estilo.PRIMARIO, this::onClose)
      );
      this.reconstruir(true);
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      Font f = this.font;
      this.subtitulo(
         this.datos.isEmpty()
            ? "No hay ningún bunker construido"
            : this.datos.size() + (this.datos.size() == 1 ? " bunker" : " bunkers") + " · del más cercano al más lejano"
      );
      this.lista.dibujar(g, f, mx, my);
      if (this.datos.isEmpty()) {
         int cx = this.px + this.pw / 2;
         int cy = this.py + this.ph / 2 - 20;
         Iconos.dibujar(g, Iconos.BUNKER, cx - 12, cy - 30, 9344683, 0.5F);
         Ui.textoCentro(g, f, "Todavía no hay bunkers", cx, cy, 15330549, 1.0F, true);
         Ui.textoCentro(g, f, "Se crean con /bunker set y /bunker build", cx, cy + 14, 9344683, 1.0F, false);
      }

      Ui.texto(g, f, "Demoler quita todos los bloques de la estructura, poco a poco.", this.px + 18, this.py + this.ph - 34, 9344683, 1.0F);
      Ui.texto(g, f, "El terreno de alrededor no se toca.", this.px + 18, this.py + this.ph - 23, 9344683, 0.8F);
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

   private static String nombreDimension(String id) {
      return switch (id) {
         case "minecraft:overworld" -> "Overworld";
         case "minecraft:the_nether" -> "Nether";
         case "minecraft:the_end" -> "End";
         default -> id;
      };
   }

   private static String distanciaTexto(long d) {
      if (d < 0L) {
         return "Otra dimensión";
      } else {
         return d >= 1000L ? String.format(Locale.ROOT, "%.1f km", d / 1000.0) : d + " m";
      }
   }

   private record Datos(String clave, String nombre, String dimension, int x, int y, int z, long distancia, int porcentaje) {
   }

   private final class FilaBunker extends UiFila {
      private final PantallaBunkerBorrar.Datos d;
      private final Suave hover = new Suave();
      private final Suave confirmar = new Suave();
      private final PantallaBunkerBorrar.Mini ir = new PantallaBunkerBorrar.Mini("Ir", false);
      private final PantallaBunkerBorrar.Mini demoler = new PantallaBunkerBorrar.Mini("Demoler", true);
      private final PantallaBunkerBorrar.Mini si = new PantallaBunkerBorrar.Mini("Sí, demoler", true);
      private final PantallaBunkerBorrar.Mini no = new PantallaBunkerBorrar.Mini("No", false);

      FilaBunker(PantallaBunkerBorrar.Datos d) {
         this.d = d;
         this.alto = 58;
         this.confirmar.fijar(PantallaBunkerBorrar.this.confirmando.contains(d.clave()) ? 1.0F : 0.0F);
      }

      @Override
      public void dibujar(GuiGraphics g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         float h = this.hover.hacia(hover ? 1.0F : 0.0F, 14.0F);
         boolean demoliendo = this.d.porcentaje() >= 0;
         boolean pide = PantallaBunkerBorrar.this.confirmando.contains(this.d.clave());
         float c = this.confirmar.hacia(pide ? 1.0F : 0.0F, 15.0F);
         int borde = Ui.mezcla(3028037, pide ? 16735340 : 7113983, Math.max(h * 0.4F, c * 0.9F));
         Ui.cajaBorde(g, x, y, w, this.alto, 7, Ui.c(1711656, 0.95F), Ui.c(borde));
         Ui.caja(g, x + 12, y + 13, 32, 32, 7, Ui.c(demoliendo ? 16735340 : 7113983, 0.16F));
         Iconos.dibujar(g, Iconos.BUNKER, x + 20, y + 22, demoliendo ? 16735340 : 7113983, 1.0F);
         int xt = x + 56;
         int anchoTexto = w - 56 - 240;
         Ui.textoN(g, f, Ui.recortar(f, this.d.nombre(), anchoTexto), xt, y + 14, 15330549, 1.0F);
         Ui.texto(
            g,
            f,
            Ui.recortar(
               f,
               "X " + this.d.x() + "   Y " + this.d.y() + "   Z " + this.d.z() + "  ·  " + PantallaBunkerBorrar.nombreDimension(this.d.dimension()),
               anchoTexto
            ),
            xt,
            y + 29,
            9344683,
            1.0F
         );
         int xr = x + w - 12;
         if (demoliendo) {
            int bw = 190;
            int bx = xr - bw;
            Ui.textoDerecha(g, f, "Demoliendo…  " + this.d.porcentaje() + " %", xr, y + 14, 16735340, 1.0F);
            Ui.caja(g, bx, y + 33, bw, 6, 3, Ui.c(2369846));
            Ui.caja(g, bx, y + 33, Math.max(6, bw * this.d.porcentaje() / 100), 6, 3, Ui.c(16735340));
         } else {
            Ui.textoDerecha(g, f, PantallaBunkerBorrar.distanciaTexto(this.d.distancia()), xr, y + 11, 15330549, 1.0F);
            float normal = 1.0F - c;
            if (normal > 0.02F) {
               this.demoler.dibujar(g, f, xr - 76, y + 30, 76, 20, mx, my, normal);
               this.ir.dibujar(g, f, xr - 76 - 8 - 40, y + 30, 40, 20, mx, my, normal);
            }

            if (c > 0.02F) {
               Ui.textoDerecha(g, f, "¿Demoler este bunker?", xr, y + 11, 16735340, c);
               this.si.dibujar(g, f, xr - 84, y + 30, 84, 20, mx, my, c);
               this.no.dibujar(g, f, xr - 84 - 8 - 40, y + 30, 40, 20, mx, my, c);
            }
         }
      }

      @Override
      public boolean clic(double mx, double my, int boton, int x, int y, int w) {
         if (boton == 0 && this.d.porcentaje() < 0) {
            int xr = x + w - 12;
            boolean pide = PantallaBunkerBorrar.this.confirmando.contains(this.d.clave());
            Minecraft mc = Minecraft.getInstance();
            if (!pide) {
               if (Ui.dentro(mx, my, xr - 76, y + 30, 76, 20)) {
                  PantallaBunkerBorrar.this.confirmando.add(this.d.clave());
                  mc.getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 0.8F, 0.6F));
                  return true;
               }

               if (Ui.dentro(mx, my, xr - 76 - 8 - 40, y + 30, 40, 20)) {
                  RedCliente.enviar(new BunkerAccionPayload("ir", this.d.clave()));
                  PantallaBunkerBorrar.this.onClose();
                  return true;
               }
            } else {
               if (Ui.dentro(mx, my, xr - 84, y + 30, 84, 20)) {
                  PantallaBunkerBorrar.this.confirmando.remove(this.d.clave());
                  RedCliente.enviar(new BunkerAccionPayload("borrar", this.d.clave()));
                  mc.getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.35F));
                  return true;
               }

               if (Ui.dentro(mx, my, xr - 84 - 8 - 40, y + 30, 40, 20)) {
                  PantallaBunkerBorrar.this.confirmando.remove(this.d.clave());
                  mc.getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.2F, 0.5F));
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   private static final class Mini {
      private final Suave hover = new Suave();
      private final String texto;
      private final boolean peligro;

      Mini(String texto, boolean peligro) {
         this.texto = texto;
         this.peligro = peligro;
      }

      void dibujar(GuiGraphics g, Font f, int x, int y, int w, int h, int mx, int my, float a) {
         float hv = this.hover.hacia(Ui.dentro(mx, my, x, y, w, h) ? 1.0F : 0.0F, 16.0F);
         int base = this.peligro ? Ui.mezcla(16735340, 1711656, 0.8F) : 2369846;
         int alto = this.peligro ? 16735340 : Ui.mezcla(2369846, 7113983, 0.5F);
         Ui.cajaBorde(g, x, y, w, h, 4, Ui.c(Ui.mezcla(base, alto, hv), a), Ui.c(Ui.mezcla(3028037, this.peligro ? 16735340 : 7113983, hv), a));
         Ui.textoCentro(g, f, this.texto, x + w / 2, y + (h - 8) / 2, hv > 0.5F && this.peligro ? 16777215 : 15330549, a, true);
      }
   }
}
