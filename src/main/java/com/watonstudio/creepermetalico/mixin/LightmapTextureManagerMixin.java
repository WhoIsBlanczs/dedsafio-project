package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.client.ClientEclipse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Lightmap.class)
public abstract class LightmapTextureManagerMixin {
   @Inject(method = "getBrightness", at = @At("RETURN"), cancellable = true)
   private static void watonstudio$darkenResidualLights(DimensionType type, int lightLevel, CallbackInfoReturnable<Float> cir) {
      float darkness = ClientEclipse.ambientDarkness(1.0F);
      if (!(darkness <= 0.001F) && lightLevel == 1) {
         float remaining = 1.0F - 0.9995F * darkness;
         cir.setReturnValue(cir.getReturnValue() * remaining);
      }
   }
}
