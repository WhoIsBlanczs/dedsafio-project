package net.ruleta.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.ruleta.AjustePayload;
import net.ruleta.Ajustes;
import net.ruleta.client.ui.Iconos;
import net.ruleta.client.ui.Suave;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiBarra;
import net.ruleta.client.ui.UiBoton;
import net.ruleta.client.ui.UiCampo;
import net.ruleta.client.ui.UiFila;
import net.ruleta.client.ui.UiLista;
import net.ruleta.client.ui.UiPantalla;

public class PantallaAjustes extends UiPantalla {
   private static String ultimaSeccion;
   private final List<String> secciones = new ArrayList<>();
   private int seleccion;
   private UiCampo buscar;
   private final UiLista lista = new UiLista();
   private final Suave indicador = new Suave();
   private final boolean puedeEditar;
   private String filtroAnterior = "";
   private int xSide;
   private int ySide;
   private static final int ANCHO_SIDE = 150;

   public PantallaAjustes(Screen padre) {
      super("Ajustes", padre);
      this.puedeEditar = Minecraft.getInstance().player != null && Minecraft.getInstance().player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);
      this.secciones.addAll(Ajustes.categorias());
      this.seleccion = Math.max(0, this.secciones.indexOf(ultimaSeccion));
      this.indicador.fijar(this.seleccion);
   }

   protected void init() {
      this.panel(740, 440);
      this.cabecera(true);
      this.subtitulo(
         this.puedeEditar ? "Los cambios se aplican al instante y se guardan en el servidor" : "Solo lectura: hacen falta permisos de administrador"
      );
      this.xSide = this.px + 14;
      this.ySide = this.py + 52;
      int x0 = this.xSide + 150 + 16;
      int w0 = this.px + this.pw - 14 - x0;
      String previo = this.buscar == null ? "" : this.buscar.getValue();
      this.buscar = (UiCampo)this.addRenderableWidget(new UiCampo(this.font, x0, this.py + 50, w0, 22, Component.literal("Buscar")));
      this.buscar.setHint(Component.literal("Buscar un ajuste…"));
      this.buscar.setValue(previo);
      this.buscar.setResponder(s -> this.reconstruir(true));
      int yPie = this.py + this.ph - 40;
      this.lista.x = x0;
      this.lista.y = this.py + 80;
      this.lista.w = w0;
      this.lista.h = yPie - this.lista.y - 24;
      this.addRenderableWidget(new UiBoton(x0, yPie, 150, 26, "Restablecer sección", UiBoton.Estilo.FANTASMA, () -> {
         if (this.puedeEditar && !this.secciones.isEmpty()) {
            RedCliente.enviar(new AjustePayload(this.secciones.get(this.seleccion), 0.0, true));

            for (Ajustes.Def d : Ajustes.todas()) {
               if (d.categoria().equals(this.secciones.get(this.seleccion))) {
                  Ajustes.poner(d.id(), d.def());
               }
            }
         }
      }).ayuda("Devuelve los ajustes de esta sección a sus valores originales"));
      this.addRenderableWidget(new UiBoton(x0 + 158, yPie, 130, 26, "Restablecer todo", UiBoton.Estilo.PELIGRO, () -> {
         if (this.puedeEditar) {
            RedCliente.enviar(new AjustePayload("", 0.0, true));
            Ajustes.restablecer(null);
         }
      }).ayuda("Devuelve TODOS los ajustes a sus valores originales"));
      this.addRenderableWidget(new UiBoton(x0 + w0 - 110, yPie, 110, 26, "Listo", UiBoton.Estilo.PRIMARIO, this::onClose));
      this.reconstruir(true);
   }

   private void reconstruir(boolean animar) {
      String q = this.buscar == null ? "" : this.buscar.getValue().trim().toLowerCase(Locale.ROOT);
      boolean cambioFiltro = !q.equals(this.filtroAnterior);
      this.filtroAnterior = q;
      List<UiFila> filas = new ArrayList<>();
      String sec = this.secciones.isEmpty() ? "" : this.secciones.get(this.seleccion);

      for (Ajustes.Def d : Ajustes.todas()) {
         boolean ok = q.isEmpty()
            ? d.categoria().equals(sec)
            : d.nombre().toLowerCase(Locale.ROOT).contains(q)
               || d.ayuda().toLowerCase(Locale.ROOT).contains(q)
               || d.categoria().toLowerCase(Locale.ROOT).contains(q);
         if (ok) {
            filas.add(new PantallaAjustes.FilaAjuste(d));
         }
      }

      this.lista.poner(filas, animar);
      if (cambioFiltro || animar) {
         this.lista.volverArriba();
      }
   }

   private void elegirSeccion(int i) {
      if (i != this.seleccion || !this.buscar.getValue().isEmpty()) {
         this.seleccion = i;
         ultimaSeccion = this.secciones.get(i);
         this.buscar.setValue("");
         this.reconstruir(true);
      }
   }

   private static int modificados(String seccion) {
      int n = 0;

      for (Ajustes.Def d : Ajustes.todas()) {
         if (d.categoria().equals(seccion) && Math.abs(Ajustes.valor(d.id()) - d.def()) > 1.0E-6) {
            n++;
         }
      }

      return n;
   }

   @Override
   protected void contenido(GuiGraphicsExtractor g, int mx, int my, float pt) {
      Font f = this.font;
      boolean buscando = !this.buscar.getValue().trim().isEmpty();
      float y0 = this.indicador.hacia(this.seleccion, 16.0F);
      if (!buscando) {
         Ui.caja(g, this.xSide, this.ySide + Math.round(y0 * 30.0F), 150, 26, 5, Ui.c(7113983, 0.16F));
         Ui.caja(g, this.xSide, this.ySide + Math.round(y0 * 30.0F) + 5, 3, 16, 1, Ui.c(7113983));
      }

      for (int i = 0; i < this.secciones.size(); i++) {
         int y = this.ySide + i * 30;
         boolean sobre = Ui.dentro(mx, my, this.xSide, y, 150, 26);
         boolean sel = i == this.seleccion && !buscando;
         if (sobre && !sel) {
            Ui.caja(g, this.xSide, y, 150, 26, 5, Ui.c(2369846, 0.7F));
         }

         String nombre = Ui.recortar(f, this.secciones.get(i), 110);
         if (sel) {
            Ui.textoN(g, f, nombre, this.xSide + 12, y + 9, 16777215, 1.0F);
         } else {
            Ui.texto(g, f, nombre, this.xSide + 12, y + 9, sobre ? 15330549 : 9344683, 1.0F);
         }

         int mod = modificados(this.secciones.get(i));
         if (mod > 0) {
            Ui.caja(g, this.xSide + 150 - 22, y + 8, 14, 11, 5, Ui.c(7113983, 0.9F));
            Ui.textoCentro(g, f, String.valueOf(mod), this.xSide + 150 - 15, y + 9, 16777215, 1.0F, false);
         }
      }

      g.fill(this.xSide + 150 + 7, this.ySide, this.xSide + 150 + 8, this.py + this.ph - 18, Ui.c(3028037, 0.7F));
      Ui.texto(g, f, Ui.recortar(f, "Un punto azul = distinto del original", this.lista.w), this.lista.x, this.lista.y + this.lista.h + 1, 9344683, 0.85F);
      this.lista.dibujar(g, f, mx, my);
      if (this.lista.tamano() == 0) {
         Ui.textoCentro(g, f, "Ningún ajuste coincide con la búsqueda", this.lista.x + this.lista.w / 2, this.lista.y + 30, 9344683, 1.0F, false);
      }
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      if (boton == 0) {
         for (int i = 0; i < this.secciones.size(); i++) {
            if (Ui.dentro(mx, my, this.xSide, this.ySide + i * 30, 150, 26)) {
               this.elegirSeccion(i);
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.4F, 0.4F));
               return true;
            }
         }
      }

      return super.mouseClicked(mx, my, boton) ? true : this.lista.mouseClicked(mx, my, boton);
   }

   public boolean mouseDragged(double mx, double my, int boton, double dx, double dy) {
      return this.lista.mouseDragged(mx, my) || super.mouseDragged(mx, my, boton, dx, dy);
   }

   public boolean mouseReleased(double mx, double my, int boton) {
      return this.lista.mouseReleased() || super.mouseReleased(mx, my, boton);
   }

   public boolean mouseScrolled(double mx, double my, double deltaX, double delta) {
      return this.lista.mouseScrolled(mx, my, deltaX, delta) || super.mouseScrolled(mx, my, deltaX, delta);
   }

   public void alActualizar() {
   }

   static String formato(Ajustes.Def d, double v) {
      if (d.tipo() == Ajustes.Tipo.BOOL) {
         return v >= 0.5 ? "Activado" : "Desactivado";
      }

      if ("s".equals(d.unidad())) {
         long s = Math.round(v);
         if (s >= 3600L) {
            return s / 3600L + ":" + String.format("%02d", s / 60L % 60L) + ":" + String.format("%02d", s % 60L) + " h";
         } else {
            return s >= 60L ? s / 60L + ":" + String.format("%02d", s % 60L) + " min" : s + " s";
         }
      } else {
         String num = v == Math.rint(v) ? String.valueOf((long)Math.rint(v)) : String.format(Locale.ROOT, "%.1f", v);
         return d.unidad().isEmpty() ? num : num + " " + d.unidad();
      }
   }

   private final class FilaAjuste extends UiFila {
      private final Ajustes.Def d;
      private final UiBarra barra;
      private final Suave hover = new Suave();
      private final Suave menos = new Suave();
      private final Suave mas = new Suave();
      private final Suave reset = new Suave();
      private final Suave interr = new Suave();
      private boolean primera = true;
      private boolean desdeArrastre;

      FilaAjuste(Ajustes.Def d) {
         this.d = d;
         this.alto = 50;
         this.barra = new UiBarra(Ajustes.valor(d.id()), d.min(), d.max(), d.paso());
      }

      private boolean modificado() {
         return Math.abs(Ajustes.valor(this.d.id()) - this.d.def()) > 1.0E-6;
      }

      private void enviar(double v) {
         v = Ajustes.limpiar(this.d, v);
         Ajustes.poner(this.d.id(), v);
         if (PantallaAjustes.this.puedeEditar) {
            RedCliente.enviar(new AjustePayload(this.d.id(), v, false));
         }
      }

      private int xc(int x, int w) {
         return x + w - 12 - 176;
      }

      @Override
      public void dibujar(GuiGraphicsExtractor g, Font f, int x, int y, int w, int mx, int my, boolean hover) {
         boolean ed = PantallaAjustes.this.puedeEditar;
         float h = this.hover.hacia(hover ? 1.0F : 0.0F, 14.0F);
         double actual = Ajustes.valor(this.d.id());
         if (!this.barra.arrastrando) {
            this.barra.v = actual;
         }

         double mostrado = this.barra.arrastrando ? this.barra.v : actual;
         boolean mod = Math.abs(mostrado - this.d.def()) > 1.0E-6;
         Ui.cajaBorde(g, x, y, w, this.alto, 6, Ui.c(1711656, 0.95F), Ui.c(Ui.mezcla(3028037, 7113983, h * 0.45F)));
         int xt = x + 14;
         if (mod) {
            Ui.caja(g, x + 6, y + 12, 4, 4, 2, Ui.c(7113983));
         }

         int anchoTexto = w - 176 - 40;
         String titulo = this.d.nombre() + (PantallaAjustes.this.buscar.getValue().isEmpty() ? "" : "  ·  " + this.d.categoria());
         Ui.textoN(g, f, Ui.recortar(f, titulo, anchoTexto), xt, y + 9, 15330549, 1.0F);
         List<String> ls = Ui.lineas(f, this.d.ayuda(), anchoTexto, 2);

         for (int i = 0; i < ls.size(); i++) {
            Ui.texto(g, f, ls.get(i), xt, y + 22 + i * 10, 9344683, 1.0F);
         }

         int cx = this.xc(x, w);
         Ui.textoDerecha(g, f, PantallaAjustes.formato(this.d, mostrado), cx + 176, y + 8, mod ? 7113983 : 15330549, 1.0F);
         float rr = this.reset.hacia(mod && ed && Ui.dentro(mx, my, cx, y + 4, 60, 14) ? 1.0F : 0.0F, 16.0F);
         if (mod && ed) {
            Iconos.dibujar(g, Iconos.REINICIAR, cx, y + 8, Ui.mezcla(9344683, 15330549, rr), 1.0F);
            Ui.texto(g, f, "Original", cx + 11, y + 8, Ui.mezcla(9344683, 15330549, rr), 1.0F);
         }

         int y2 = y + 28;
         if (this.d.tipo() == Ajustes.Tipo.BOOL) {
            boolean on = actual >= 0.5;
            if (this.primera) {
               this.interr.fijar(on ? 1.0F : 0.0F);
            }

            float p = this.interr.hacia(on ? 1.0F : 0.0F, 14.0F);
            int sx = cx + 176 - 30;
            float a = ed ? 1.0F : 0.45F;
            Ui.cajaBorde(g, sx, y2, 30, 16, 8, Ui.c(Ui.mezcla(2369846, 7113983, p), a), Ui.c(Ui.mezcla(3028037, 7113983, p * 0.6F), a));
            Ui.caja(g, sx + 3 + Math.round(14.0F * p), y2 + 3, 10, 10, 5, Ui.c(Ui.mezcla(9344683, 16777215, p), a));
         } else {
            float a = ed ? 1.0F : 0.45F;
            boolean sm = ed && Ui.dentro(mx, my, cx, y2, 16, 16);
            boolean sp = ed && Ui.dentro(mx, my, cx + 160, y2, 16, 16);
            float hm = this.menos.hacia(sm ? 1.0F : 0.0F, 16.0F);
            float hp = this.mas.hacia(sp ? 1.0F : 0.0F, 16.0F);
            Ui.cajaBorde(g, cx, y2, 16, 16, 4, Ui.c(Ui.mezcla(2369846, 7113983, hm * 0.5F), a), Ui.c(3028037, a));
            Ui.cajaBorde(g, cx + 160, y2, 16, 16, 4, Ui.c(Ui.mezcla(2369846, 7113983, hp * 0.5F), a), Ui.c(3028037, a));
            Ui.textoCentro(g, f, "−", cx + 8, y2 + 4, 15330549, a, false);
            Ui.textoCentro(g, f, "+", cx + 168, y2 + 4, 15330549, a, false);
            this.barra.dibujar(g, cx + 24, y2 + 5, 128, mx, my, ed, 7113983);
         }

         this.primera = false;
      }

      @Override
      public boolean clic(double mx, double my, int boton, int x, int y, int w) {
         if (PantallaAjustes.this.puedeEditar && boton == 0) {
            int cx = this.xc(x, w);
            int y2 = y + 28;
            double actual = Ajustes.valor(this.d.id());
            if (this.modificado() && Ui.dentro(mx, my, cx, y + 4, 60, 14)) {
               this.enviar(this.d.def());
               return true;
            }

            if (this.d.tipo() == Ajustes.Tipo.BOOL) {
               if (Ui.dentro(mx, my, cx + 100, y2 - 2, 76, 20)) {
                  this.enviar(actual >= 0.5 ? 0.0 : 1.0);
                  return true;
               } else {
                  return false;
               }
            } else if (Ui.dentro(mx, my, cx, y2, 16, 16)) {
               this.enviar(actual - this.d.paso() * (net.minecraft.client.Minecraft.getInstance().hasShiftDown() ? 5 : 1));
               return true;
            } else if (Ui.dentro(mx, my, cx + 160, y2, 16, 16)) {
               this.enviar(actual + this.d.paso() * (net.minecraft.client.Minecraft.getInstance().hasShiftDown() ? 5 : 1));
               return true;
            } else if (this.barra.sobre(mx, my, cx + 24, y2 + 5, 128)) {
               this.barra.arrastrando = true;
               this.barra.poner(mx, cx + 24, 128);
               Ajustes.poner(this.d.id(), this.barra.v);
               this.desdeArrastre = true;
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      @Override
      public boolean arrastra(double mx, double my, int x, int y, int w) {
         if (!this.barra.arrastrando) {
            return false;
         }

         this.barra.poner(mx, this.xc(x, w) + 24, 128);
         Ajustes.poner(this.d.id(), this.barra.v);
         return true;
      }

      @Override
      public void suelta() {
         if (this.barra.arrastrando) {
            this.barra.arrastrando = false;
            this.enviar(this.barra.v);
         }
      }
   }
}
