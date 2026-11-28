package com.watonstudio.creepermetalico.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.watonstudio.creepermetalico.client.ClientEclipse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin {
   @ModifyExpressionValue(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"
      ),
      require = 0
   )
   private static Vec3 eclipse$darkFog(Vec3 original) {
      float e = ClientEclipse.darkness(1.0F);
      return e <= 0.0F ? original : original.lerp(new Vec3(0.006, 0.0035, 0.0025), e);
   }
}
