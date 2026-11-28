package com.watonstudio.creepermetalico.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.watonstudio.creepermetalico.client.ClientEclipse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class EclipseFogDistanceMixin {
   @ModifyVariable(method = "applyFog", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private static float eclipse$limitTerrainFog(float viewDistance, @Local(argsOnly = true) FogMode fogType) {
      return ClientEclipse.isRunning() && ClientEclipse.inOverworld() && fogType == FogMode.FOG_TERRAIN ? Math.min(viewDistance, 50.0F) : viewDistance;
   }
}
