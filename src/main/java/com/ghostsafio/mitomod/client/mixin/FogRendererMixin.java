package com.ghostsafio.mitomod.client.mixin;

import com.ghostsafio.mitomod.client.SpaceClient;
import com.ghostsafio.mitomod.client.SupernovaClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
   @Inject(method = "setupFog", at = @At("RETURN"))
   private void ghostsafio$tintFog(Camera camera, int renderDistance, net.minecraft.client.DeltaTracker delta, float darken, ClientLevel level, CallbackInfoReturnable<net.minecraft.client.renderer.fog.FogData> cir) {
      org.joml.Vector4f c = cir.getReturnValue().color;
      if (c == null) {
         return;
      }
      float fogRed = c.x, fogGreen = c.y, fogBlue = c.z;
      if (SpaceClient.isSpaceDimension()) {
         fogRed = 0.0F;
         fogGreen = 0.0F;
         fogBlue = 0.0F;
      } else if (SpaceClient.isRedPlanet()) {
         fogRed = 0.7F;
         fogGreen = 0.28F;
         fogBlue = 0.3F;
      } else if (camera.getFluidInCamera() == FogType.NONE) {
         float haze = SpaceClient.cloudHaze();
         if (haze > 0.0F) {
            fogRed = Mth.lerp(haze, fogRed, 0.6F);
            fogGreen = Mth.lerp(haze, fogGreen, 0.68F);
            fogBlue = Mth.lerp(haze, fogBlue, 0.84F);
         }

         float dark = SpaceClient.atmosphereDarkness();
         if (dark > 0.0F) {
            fogRed = Mth.lerp(dark, fogRed, 0.0F);
            fogGreen = Mth.lerp(dark, fogGreen, 0.0F);
            fogBlue = Mth.lerp(dark, fogBlue, 0.02F);
         }

         float tint = SupernovaClient.skyTint();
         if (tint > 0.0F) {
            float pulse = SupernovaClient.skyPulse();
            fogRed = Mth.lerp(tint, fogRed, 0.74F + 0.2F * pulse);
            fogGreen = Mth.lerp(tint, fogGreen, 0.43F + 0.12F * pulse);
            fogBlue = Mth.lerp(tint, fogBlue, 0.37F + 0.15F * pulse);
         }
      }
      c.set(fogRed, fogGreen, fogBlue, c.w);
   }
}
