package com.novastudio.dedsafio4;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@Environment(EnvType.CLIENT)
final class TimerBar {
   static final int WIDTH = 182;
   static final int HEIGHT = 16;
   private static final int BLACK = -16777216;
   private static final int WHITE = -657931;

   private TimerBar() {
   }

   static void draw(GuiGraphicsExtractor var0, int var1, int var2, float var3) {
      int var4 = var1 - 91;
      int var5 = var4 + 182;
      int var6 = var2 + 16;
      var0.fill(var4, var2, var5, var6, -16777216);
      var0.fill(var4 + 2, var2 + 2, var5 - 2, var6 - 2, -657931);
      var0.fill(var4 + 3, var2 + 3, var5 - 3, var6 - 3, -16777216);
      int var7 = var5 - 4 - (var4 + 4);
      var0.fill(var4 + 4, var2 + 4, var4 + 4 + Math.round(var7 * Math.max(0.0F, Math.min(1.0F, var3))), var6 - 4, -657931);
   }
}
