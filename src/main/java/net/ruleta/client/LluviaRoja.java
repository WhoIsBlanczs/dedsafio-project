package net.ruleta.client;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.ruleta.Ruleta;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.SubscribeEvent;

public final class LluviaRoja {
   private static final Identifier LLUVIA = Identifier.fromNamespaceAndPath("minecraft", "textures/environment/rain.png");
   private static boolean puesta;

   private LluviaRoja() {
   }

   public static void poner(boolean roja) { }

   private static NativeImage leer(Minecraft mc) {
      try {
         Resource r = mc.getResourceManager().getResourceOrThrow(LLUVIA);

         try (InputStream in = r.open()) {
            return NativeImage.read(in);
         }
      } catch (Exception ex) {
         Ruleta.LOG.warn("[lluvia roja] no se pudo leer la textura de la lluvia", ex);
         return null;
      }
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      poner(false);
   }
}
