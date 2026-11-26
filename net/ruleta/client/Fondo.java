package net.ruleta.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.ruleta.mixin.GameRendererInvoker;

public final class Fondo {
   private static boolean puesto;

   private Fondo() {
   }

   public static void poner() {
      Minecraft mc = Minecraft.getInstance();

      try {
         if (mc.gameRenderer.currentEffect() == null) {
            ((GameRendererInvoker)mc.gameRenderer).ruleta$cargarEfecto(ResourceLocation.parse("shaders/post/blur.json"));
            puesto = true;
         }
      } catch (Exception var2) {
         puesto = false;
      }
   }

   public static void quitar() {
      if (puesto) {
         puesto = false;

         try {
            Minecraft.getInstance().gameRenderer.shutdownEffect();
         } catch (Exception var1) {
         }
      }
   }
}
