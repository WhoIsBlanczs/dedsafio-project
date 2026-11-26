package net.ruleta.client.ui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class EscalaGui {
   private static double original = -1.0;
   private static double aplicada = -1.0;

   private EscalaGui() {
   }

   public static void ajustar(Screen pantalla, int anchoNec, int altoNec) {
      Window w = Minecraft.getInstance().getWindow();
      double actual = w.getGuiScale();
      if (aplicada > 0.0 && Math.abs(actual - aplicada) > 1.0E-6) {
         original = -1.0;
         aplicada = -1.0;
      }

      double base = original > 0.0 ? original : actual;
      int ideal = Math.max(1, Math.min(w.getWidth() / Math.max(1, anchoNec), w.getHeight() / Math.max(1, altoNec)));
      double objetivo = Math.min(base, ideal);
      if (Math.abs(objetivo - actual) > 1.0E-6) {
         if (original < 0.0) {
            original = base;
         }

         w.setGuiScale(objetivo);
         aplicada = objetivo;
         if (Math.abs(objetivo - original) < 1.0E-6) {
            original = -1.0;
            aplicada = -1.0;
         }

         pantalla.width = w.getGuiScaledWidth();
         pantalla.height = w.getGuiScaledHeight();
      }
   }

   public static void restaurar() {
      if (original > 0.0) {
         Minecraft.getInstance().getWindow().setGuiScale(original);
      }

      original = -1.0;
      aplicada = -1.0;
   }
}
