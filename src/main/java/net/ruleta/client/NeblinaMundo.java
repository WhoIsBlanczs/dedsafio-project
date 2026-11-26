package net.ruleta.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.FogRenderer.FogMode;
import net.minecraft.util.Mth;
import net.ruleta.client.compat.EventPriority;
import net.ruleta.client.compat.RenderGuiEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.ViewportEvent;
import net.ruleta.client.meteoritos.MeteoritosCliente;
import net.ruleta.client.tornado.TornadoCliente;

public final class NeblinaMundo {
   private static final int ROJO = 9177616;
   private static final int AZUL = 5664906;

   private NeblinaMundo() {
   }

   private static float rojo() {
      return Math.min(1.0F, MeteoritosCliente.fuerza());
   }

   private static float azul() {
      return Math.min(1.0F, TornadoCliente.nivel() * (0.45F + 0.55F * TornadoCliente.cercania()));
   }

   @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
   public static void alNiebla(ViewportEvent.RenderFog e) {
      if (e.getMode() == FogMode.FOG_TERRAIN) {
         float rojo = rojo();
         float azul = azul();
         float k = Math.max(rojo, azul);
         if (!(k <= 0.01F)) {
            float lejos = e.getFarPlaneDistance();
            float meta = Math.max(56.0F, lejos * (rojo > azul ? 0.42F : 0.36F));
            e.setFarPlaneDistance(Mth.lerp(k * 0.85F, lejos, Math.min(lejos, meta)));
            e.setNearPlaneDistance(Mth.lerp(k, e.getNearPlaneDistance(), 0.0F));
            e.setCanceled(true);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGH)
   public static void alDibujarGui(RenderGuiEvent.Pre e) {
      float rojo = rojo();
      float azul = azul();
      if ((!(rojo <= 0.01F) || !(azul <= 0.01F)) && Minecraft.getInstance().level != null) {
         int w = e.getWindow().getGuiScaledWidth();
         int h = e.getWindow().getGuiScaledHeight();
         if (rojo > 0.01F) {
            e.getGuiGraphics().fill(0, 0, w, h, Math.round(rojo * 0.22F * 255.0F) << 24 | 9177616);
         }

         if (azul > 0.01F) {
            e.getGuiGraphics().fill(0, 0, w, h, Math.round(azul * 0.2F * 255.0F) << 24 | 5664906);
         }
      }
   }
}
