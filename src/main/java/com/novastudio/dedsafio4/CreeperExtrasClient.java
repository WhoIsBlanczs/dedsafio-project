package com.novastudio.dedsafio4;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;

@Environment(EnvType.CLIENT)
public final class CreeperExtrasClient {
   private static final Identifier GRAY = Identifier.fromNamespaceAndPath("nexo", "blanco_y_negro");
   private static final Identifier INVERT = Identifier.fromNamespaceAndPath("minecraft", "invert");
   /** Efecto de pantalla que hay que mantener puesto (lo lee GameRendererInvoker en cada frame). */
   public static Identifier active;

   private CreeperExtrasClient() {
   }

   public static void register() {
      ClientTickEvents.END_CLIENT_TICK.register(CreeperExtrasClient::tick);
   }

   private static void tick(Minecraft mc) {
      LocalPlayer player = mc.player;
      Identifier want = null;
      if (player != null && CreeperExtras.BLANCO_Y_NEGRO != null) {
         if (player.hasEffect(CreeperExtras.COLORES_INVERTIDOS)) {
            want = INVERT;
         } else if (player.hasEffect(CreeperExtras.BLANCO_Y_NEGRO)) {
            want = GRAY;
         }
      }

      GameRenderer renderer = mc.gameRenderer;
      if (active != null && active != want) {
         renderer.getRequestedPostEffects().remove(active);
      }
      active = want;
   }
}
