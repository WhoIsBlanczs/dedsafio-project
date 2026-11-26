package net.ruleta.client.ui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public final class UiLista {
   private final List<UiFila> filas = new ArrayList<>();
   public int x;
   public int y;
   public int w;
   public int h;
   private int separacion = 6;
   private final Suave scroll = new Suave();
   private float objetivo;
   private UiFila arrastrando;
   private long desde = Util.getMillis();

   public void poner(List<UiFila> nuevas, boolean animar) {
      this.filas.clear();
      this.filas.addAll(nuevas);
      this.objetivo = Math.min(this.objetivo, this.maximo());
      if (animar) {
         this.desde = Util.getMillis();
      }
   }

   public void volverArriba() {
      this.objetivo = 0.0F;
      this.scroll.fijar(0.0F);
   }

   public int tamano() {
      return this.filas.size();
   }

   private int total() {
      int t = 0;

      for (UiFila f : this.filas) {
         t += f.alto + this.separacion;
      }

      return Math.max(0, t - this.separacion);
   }

   private float maximo() {
      return Math.max(0, this.total() - this.h);
   }

   public void dibujar(GuiGraphics g, Font f, int mx, int my) {
      float sc = this.scroll.hacia(this.objetivo, 16.0F);
      long ahora = Util.getMillis() - this.desde;
      float guardado = Ui.alfa;
      g.enableScissor(this.x, this.y, this.x + this.w, this.y + this.h);
      int yy = this.y - Math.round(sc);

      for (int i = 0; i < this.filas.size(); i++) {
         UiFila fila = this.filas.get(i);
         if (yy + fila.alto >= this.y && yy <= this.y + this.h) {
            float t = Ui.ease((float)(ahora - i * 30L) / 240.0F);
            int desplaza = Math.round((1.0F - t) * 14.0F);
            Ui.alfa = guardado * t;
            boolean hover = Ui.dentro(mx, my, this.x, Math.max(this.y, yy), this.w, Math.min(fila.alto, this.y + this.h - yy));
            fila.dibujar(g, f, this.x + desplaza, yy, this.w - 8, mx, my, hover);
            Ui.alfa = guardado;
         }

         yy += fila.alto + this.separacion;
      }

      g.disableScissor();
      float max = this.maximo();
      if (max > 0.0F) {
         int barraH = Math.max(16, Math.round(this.h * (this.h / (this.h + max))));
         int barraY = this.y + Math.round((this.h - barraH) * (sc / max));
         Ui.caja(g, this.x + this.w - 3, this.y, 2, this.h, 1, Ui.c(2369846, 0.5F));
         Ui.caja(g, this.x + this.w - 3, barraY, 2, barraH, 1, Ui.c(9344683, 0.9F));
      }
   }

   private boolean dentro(double mx, double my) {
      return Ui.dentro(mx, my, this.x, this.y, this.w, this.h);
   }

   public boolean mouseScrolled(double mx, double my, double deltaX, double delta) {
      if (!this.dentro(mx, my)) {
         return false;
      } else {
         this.objetivo = (float)Math.max(0.0, Math.min((double)this.maximo(), this.objetivo - delta * 34.0));
         return true;
      }
   }

   public boolean mouseClicked(double mx, double my, int boton) {
      if (!this.dentro(mx, my)) {
         return false;
      } else {
         int yy = this.y - Math.round(this.scroll.v);

         for (UiFila fila : this.filas) {
            if (my >= yy && my < yy + fila.alto) {
               if (fila.clic(mx, my, boton, this.x, yy, this.w - 8)) {
                  this.arrastrando = fila;
                  return true;
               }

               return false;
            }

            yy += fila.alto + this.separacion;
         }

         return false;
      }
   }

   public boolean mouseDragged(double mx, double my) {
      if (this.arrastrando == null) {
         return false;
      } else {
         int yy = this.y - Math.round(this.scroll.v);

         for (UiFila fila : this.filas) {
            if (fila == this.arrastrando) {
               return fila.arrastra(mx, my, this.x, yy, this.w - 8);
            }

            yy += fila.alto + this.separacion;
         }

         return false;
      }
   }

   public boolean mouseReleased() {
      if (this.arrastrando != null) {
         this.arrastrando.suelta();
         this.arrastrando = null;
         return true;
      } else {
         return false;
      }
   }
}
