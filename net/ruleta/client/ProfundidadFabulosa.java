package net.ruleta.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;
import net.ruleta.client.compat.EventPriority;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;

public final class ProfundidadFabulosa {
   private static RenderTarget copia;
   private static boolean guardada;

   private ProfundidadFabulosa() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void alDibujar(RenderLevelStageEvent e) {
      if (!Minecraft.useShaderTransparency()) {
         guardada = false;
      } else {
         Minecraft mc = Minecraft.getInstance();
         RenderTarget principal = mc.getMainRenderTarget();
         if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            if (copia == null) {
               copia = new TextureTarget(principal.width, principal.height, true, Minecraft.ON_OSX);
            } else if (copia.width != principal.width || copia.height != principal.height) {
               copia.resize(principal.width, principal.height, Minecraft.ON_OSX);
            }

            copia.copyDepthFrom(principal);
            principal.bindWrite(false);
            guardada = true;
         } else if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL && guardada) {
            principal.copyDepthFrom(copia);
            principal.bindWrite(false);
            guardada = false;
         }
      }
   }
}
