package net.ruleta.client.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class UiFila {
   public int alto = 40;

   public abstract void dibujar(GuiGraphicsExtractor var1, Font var2, int var3, int var4, int var5, int var6, int var7, boolean var8);

   public boolean clic(double mx, double my, int boton, int x, int y, int w) {
      return false;
   }

   public boolean arrastra(double mx, double my, int x, int y, int w) {
      return false;
   }

   public void suelta() {
   }
}
