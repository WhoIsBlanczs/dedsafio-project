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

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void alDibujar(RenderLevelStageEvent e) { }
}
