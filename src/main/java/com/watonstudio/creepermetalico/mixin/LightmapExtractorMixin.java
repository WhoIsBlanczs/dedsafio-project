package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.client.ClientEclipse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** En 1.21.1 el eclipse bajaba el brillo del cielo del lightmap; en 26.3 se hace sobre el estado extraído. */
@Environment(EnvType.CLIENT)
@Mixin(LightmapRenderStateExtractor.class)
public abstract class LightmapExtractorMixin {
   @Inject(method = "extract", at = @At("TAIL"))
   private void eclipse$darkTerrain(LightmapRenderState state, float partialTick, CallbackInfo ci) {
      float darkness = ClientEclipse.ambientDarkness(1.0F);
      if (darkness > 0.0F) {
         state.skyFactor = Mth.lerp(darkness, state.skyFactor, 0.0F);
         state.needsUpdate = true;
      }
   }
}
