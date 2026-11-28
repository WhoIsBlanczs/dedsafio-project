package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.client.ClientEclipse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class EclipseNightVisionMixin {
   @Inject(method = "nightVisionScale", at = @At("RETURN"), cancellable = true)
   private static void eclipse$limitNightVision(LivingEntity entity, float tickDelta, CallbackInfoReturnable<Float> cir) {
      if (ClientEclipse.darkness(tickDelta) > 0.0F) {
         cir.setReturnValue(Math.min(cir.getReturnValue(), 0.05F));
      }
   }
}
