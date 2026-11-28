package com.ghostsafio.mitomod.client.mixin;

import com.ghostsafio.mitomod.client.SpaceClient;
import com.ghostsafio.mitomod.client.SupernovaClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
   private static final Vec3 SUPERNOVA_SKY = new Vec3(0.4, 0.3, 0.35);
   private static final double STRENGTH = 1.0;

   @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
   private void ghostsafio$tintSky(Vec3 cameraPos, float partialTick, CallbackInfoReturnable<Vec3> cir) {
      Vec3 color = (Vec3)cir.getReturnValue();
      float dark = SpaceClient.atmosphereDarkness();
      if (dark > 0.0F) {
         color = color.scale(1.0 - dark);
      }

      float tint = SupernovaClient.skyTint();
      if (tint > 0.0F) {
         float pulse = SupernovaClient.skyPulse();
         Vec3 target = SUPERNOVA_SKY.lerp(new Vec3(0.78, 0.45, 0.6), pulse * 0.6);
         color = color.lerp(target, tint * 1.0);
      }

      if (dark > 0.0F || tint > 0.0F) {
         cir.setReturnValue(color);
      }
   }
}
