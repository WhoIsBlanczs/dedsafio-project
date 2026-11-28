package com.watonstudio.creepermetalico.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.watonstudio.creepermetalico.client.ClientEclipse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LightTexture.class)
public abstract class LightmapTextureManagerMixin {
   @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyBrightness(F)F"), require = 0)
   private float eclipse$darkTerrain(float original) {
      float darkness = ClientEclipse.ambientDarkness(1.0F);
      return darkness <= 0.0F ? original : Mth.lerp(darkness, original, 0.0F);
   }

   @Inject(method = "getBrightness", at = @At("RETURN"), cancellable = true)
   private static void watonstudio$darkenResidualLights(DimensionType type, int lightLevel, CallbackInfoReturnable<Float> cir) {
      float darkness = ClientEclipse.ambientDarkness(1.0F);
      if (!(darkness <= 0.001F) && lightLevel == 1) {
         float remaining = 1.0F - 0.9995F * darkness;
         cir.setReturnValue((Float)cir.getReturnValue() * remaining);
      }
   }
}
