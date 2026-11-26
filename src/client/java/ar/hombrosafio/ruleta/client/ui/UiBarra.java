package ar.hombrosafio.ruleta.client.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class UiBarra {
   public double v;
   public final double min;
   public final double max;
   public final double paso;
   public boolean arrastrando;
   private final Suave hover = new Suave();
   private final Suave visible = new Suave();
   private boolean primera = true;

   public UiBarra(double v, double min, double max, double paso) {
      this.v = v;
      this.min = min;
      this.max = max;
      this.paso = paso;
   }

   public double limitar(double x) {
      double r = Math.max(this.min, Math.min(this.max, x));
      if (this.paso > 0.0) {
         r = this.min + Math.round((r - this.min) / this.paso) * this.paso;
         r = Math.max(this.min, Math.min(this.max, r));
      }

      return Math.round(r * 1000.0) / 1000.0;
   }

   public void dibujar(GuiGraphicsExtractor g, int x, int y, int w, int mx, int my, boolean activo, int acento) {
      if (this.primera) {
         this.visible.fijar((float)this.fraccion());
         this.primera = false;
      }

      boolean sobre = activo && (this.arrastrando || Ui.dentro(mx, my, x - 4, y - 6, w + 8, 14));
      float h = this.hover.hacia(sobre ? 1.0F : 0.0F, 16.0F);
      float f = this.visible.hacia((float)this.fraccion(), 22.0F);
      float a = activo ? 1.0F : 0.4F;
      int ty = y + 1;
      Ui.caja(g, x, ty, w, 4, 2, Ui.c(2369846, a));
      int relleno = Math.round(w * f);
      if (relleno > 0) {
         Ui.caja(g, x, ty, Math.max(4, relleno), 4, 2, Ui.c(acento, a));
      }

      int r = 4 + Math.round(h * 1.5F);
      int cx = x + Math.round((w - 1) * f);
      Ui.caja(g, cx - r - 2, ty + 2 - r - 2, (r + 2) * 2, (r + 2) * 2, r + 2, Ui.c(acento, 0.18F * h * a));
      Ui.cajaBorde(g, cx - r, ty + 2 - r, r * 2, r * 2, r, Ui.c(16777215, a), Ui.c(acento, a));
   }

   public double fraccion() {
      return this.max <= this.min ? 0.0 : (this.v - this.min) / (this.max - this.min);
   }

   public boolean sobre(double mx, double my, int x, int y, int w) {
      return Ui.dentro(mx, my, x - 6, y - 7, w + 12, 16);
   }

   public void poner(double mx, int x, int w) {
      this.v = this.limitar(this.min + (mx - x) / Math.max(1.0, w - 1.0) * (this.max - this.min));
   }
}
