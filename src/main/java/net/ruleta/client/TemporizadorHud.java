package net.ruleta.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class TemporizadorHud {
   static final int Y = 6;
   private static long finNanos = -1L;
   private static boolean saliendo;
   private static final Fundido fundido = new Fundido(0.2F, 1.5F);

   private TemporizadorHud() {
   }

   public static void poner(int segundos) {
      if (segundos < 0) {
         if (finNanos >= 0L) {
            saliendo = true;
         }
      } else {
         finNanos = System.nanoTime() + segundos * 1000000000L;
         saliendo = false;
      }
   }

   static void limpiar() {
      finNanos = -1L;
      saliendo = false;
      fundido.cero();
   }

   public static boolean activo() {
      return finNanos >= 0L;
   }

   public static boolean saliendo() {
      return saliendo;
   }

   static void render(GuiGraphicsExtractor ctx, Font tr) { }
}
