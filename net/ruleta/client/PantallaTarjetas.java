package net.ruleta.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.ruleta.client.ui.Suave;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiPantalla;

abstract class PantallaTarjetas extends UiPantalla {
   private static final int COLUMNAS = 3;
   private static final int ESPACIO = 10;
   private final List<PantallaTarjetas.Hueco> huecos = new ArrayList<>();
   private final Map<String, Suave[]> anim = new HashMap<>();
   private final Suave scroll = new Suave();
   private float objetivo;
   private String claveCalculada = null;
   private int altoTotal;
   protected int top;
   protected int bottom;
   private int cardX;
   private int cardW;

   protected PantallaTarjetas(String titulo, Screen padre) {
      super(titulo, padre);
   }

   protected abstract List<PantallaTarjetas.Carta> cartas();

   protected abstract PantallaTarjetas.Estado estado(PantallaTarjetas.Carta var1);

   protected abstract String etiqueta(PantallaTarjetas.Carta var1);

   protected abstract void pulsar(PantallaTarjetas.Carta var1);

   protected int colorElegida() {
      return 7113983;
   }

   protected String clave() {
      return String.valueOf(this.cartas().size());
   }

   protected void reiniciarScroll() {
      this.objetivo = 0.0F;
      this.scroll.fijar(0.0F);
      this.claveCalculada = null;
   }

   protected int altoEncabezado() {
      return 0;
   }

   protected void encabezado(GuiGraphics g) {
   }

   protected void zona(int altoPie) {
      this.cardX = this.px + 16;
      this.cardW = this.pw - 32;
      this.top = this.py + 52 + this.altoEncabezado();
      this.bottom = this.py + this.ph - 14 - altoPie;
      this.claveCalculada = null;
   }

   private void armar() {
      String k = this.clave() + "|" + this.cardW;
      if (!k.equals(this.claveCalculada)) {
         this.claveCalculada = k;
         this.huecos.clear();
         List<PantallaTarjetas.Carta> cartas = this.cartas();
         int col = (this.cardW - 20 - 6) / 3;
         int interior = col - 28;
         int y = 0;

         for (int fila = 0; fila * 3 < cartas.size(); fila++) {
            int altoFila = 0;
            List<List<String>> textos = new ArrayList<>();
            int n = Math.min(3, cartas.size() - fila * 3);

            for (int c = 0; c < n; c++) {
               PantallaTarjetas.Carta carta = cartas.get(fila * 3 + c);
               List<String> ls = Ui.lineas(this.font, carta.texto(), interior, 9);
               textos.add(ls);
               int lt = Ui.lineasN(this.font, carta.titulo(), interior, 3).size();
               altoFila = Math.max(altoFila, 26 + lt * 11 + 6 + ls.size() * 10 + 12);
            }

            for (int c = 0; c < n; c++) {
               this.huecos.add(new PantallaTarjetas.Hueco(cartas.get(fila * 3 + c), textos.get(c), this.cardX + c * (col + 10), y, col, altoFila));
            }

            y += altoFila + 10;
         }

         this.altoTotal = Math.max(0, y - 10);
         this.objetivo = Math.max(0.0F, Math.min(this.objetivo, (float)this.maxScroll()));
      }
   }

   private int maxScroll() {
      return Math.max(0, this.altoTotal - (this.bottom - this.top));
   }

   public boolean mouseScrolled(double mx, double my, double deltaX, double delta) {
      this.objetivo = Math.max(0.0F, Math.min(this.objetivo - (float)delta * 34.0F, (float)this.maxScroll()));
      return true;
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      if (super.mouseClicked(mx, my, boton)) {
         return true;
      } else {
         if (boton == 0 && my >= this.top && my < this.bottom && mx >= this.cardX && mx < this.cardX + this.cardW) {
            for (PantallaTarjetas.Hueco h : this.huecos) {
               int y = this.top - Math.round(this.scroll.v) + h.y();
               if (my >= y && my < y + h.alto() && mx >= h.x() && mx < h.x() + h.ancho()) {
                  if (this.estado(h.carta()) != PantallaTarjetas.Estado.BLOQUEADA && this.estado(h.carta()) != PantallaTarjetas.Estado.ACTIVA) {
                     this.pulsar(h.carta());
                     Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.2F, 0.5F));
                  }

                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void alDibujar() {
   }

   @Override
   protected void contenido(GuiGraphics g, int mx, int my, float pt) {
      this.alDibujar();
      this.armar();
      this.encabezado(g);
      float sc = this.scroll.hacia(this.objetivo, 16.0F);
      g.enableScissor(this.cardX - 4, this.top, this.cardX + this.cardW + 4, this.bottom);

      for (PantallaTarjetas.Hueco h : this.huecos) {
         int y = this.top - Math.round(sc) + h.y();
         if (y + h.alto() >= this.top && y <= this.bottom) {
            this.dibujar(g, h, y, mx, my);
         }
      }

      g.disableScissor();
      int visible = this.bottom - this.top;
      if (this.altoTotal > visible) {
         int barra = Math.max(16, visible * visible / this.altoTotal);
         int by = this.top + Math.round((visible - barra) * (sc / this.maxScroll()));
         Ui.caja(g, this.cardX + this.cardW + 2, this.top, 2, visible, 1, Ui.c(2369846, 0.5F));
         Ui.caja(g, this.cardX + this.cardW + 2, by, 2, barra, 1, Ui.c(9344683));
      }
   }

   private void dibujar(GuiGraphics g, PantallaTarjetas.Hueco h, int y, int mx, int my) {
      PantallaTarjetas.Carta c = h.carta();
      PantallaTarjetas.Estado e = this.estado(c);
      int x = h.x();
      int w = h.ancho();
      Suave[] a = this.anim.computeIfAbsent(c.id(), k -> new Suave[]{new Suave(), new Suave(), new Suave()});
      boolean clicable = e == PantallaTarjetas.Estado.NORMAL || e == PantallaTarjetas.Estado.ELEGIDA;
      boolean encima = clicable && my >= this.top && my < this.bottom && mx >= x && mx < x + w && my >= y && my < y + h.alto();
      float hv = a[0].hacia(encima ? 1.0F : 0.0F, 14.0F);
      float el = a[1].hacia(e == PantallaTarjetas.Estado.ELEGIDA ? 1.0F : 0.0F, 12.0F);
      float ac = a[2].hacia(e == PantallaTarjetas.Estado.ACTIVA ? 1.0F : 0.0F, 12.0F);
      y -= Math.round(hv * 2.0F);
      float opaco = e == PantallaTarjetas.Estado.BLOQUEADA ? 0.5F : 1.0F;
      int sel = this.colorElegida();
      Ui.sombra(g, x, y, w, h.alto(), 8, 0.4F + hv * 0.5F + el * 0.4F);
      int borde = Ui.mezcla(Ui.mezcla(Ui.mezcla(3028037, 7113983, hv * 0.8F), sel, el), 4906624, ac);
      int relleno = Ui.mezcla(Ui.mezcla(Ui.mezcla(1711656, 2369846, hv * 0.8F), Ui.mezcla(sel, 1711656, 0.88F), el), Ui.mezcla(4906624, 1711656, 0.9F), ac);
      Ui.cajaBorde(g, x, y, w, h.alto(), 8, Ui.c(relleno, opaco), Ui.c(borde, opaco));
      int tx = x + 14;
      int ty = y + 12;
      String et = this.etiqueta(c);
      if (et != null) {
         int col = e == PantallaTarjetas.Estado.ACTIVA ? 4906624 : (e == PantallaTarjetas.Estado.ELEGIDA ? sel : 9344683);
         Ui.caja(g, tx, ty + 1, 6, 6, 3, Ui.c(col, opaco));
         Ui.textoN(g, this.font, Ui.recortarN(this.font, et, w - 44 - 6), tx + 12, ty, col, opaco);
      }

      ty += 14;

      for (String l : Ui.lineasN(this.font, c.titulo(), w - 28, 3)) {
         Ui.textoN(g, this.font, l, tx, ty, 16762967, opaco);
         ty += 11;
      }

      ty += 6;

      for (String l : h.lineas()) {
         Ui.texto(g, this.font, l, tx, ty, e == PantallaTarjetas.Estado.BLOQUEADA ? 9344683 : 15330549, opaco * 0.92F);
         ty += 10;
      }
   }

   record Carta(String id, String titulo, String texto) {
   }

   static enum Estado {
      NORMAL,
      ELEGIDA,
      ACTIVA,
      BLOQUEADA;
   }

   private record Hueco(PantallaTarjetas.Carta carta, List<String> lineas, int x, int y, int ancho, int alto) {
   }
}
