package com.ghostsafio.mitomod.client.mixin;

import com.ghostsafio.mitomod.client.SpaceClient;
import com.ghostsafio.mitomod.client.SupernovaClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
   private static final float FOG_R = 0.74F;
   private static final float FOG_G = 0.43F;
   private static final float FOG_B = 0.37F;
   @Shadow
   private static float fogRed;
   @Shadow
   private static float fogGreen;
   @Shadow
   private static float fogBlue;

   @Inject(method = "setupColor", at = @At("TAIL"))
   private static void ghostsafio$tintFog(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenAmount, CallbackInfo ci) {
      if (SpaceClient.isSpaceDimension()) {
         fogRed = 0.0F;
         fogGreen = 0.0F;
         fogBlue = 0.0F;
         RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
      } else if (SpaceClient.isRedPlanet()) {
         fogRed = 0.7F;
         fogGreen = 0.28F;
         fogBlue = 0.3F;
         RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
      } else {
         float haze = SpaceClient.cloudHaze();
         if (haze > 0.0F && camera.getFluidInCamera() == FogType.NONE) {
            fogRed = Mth.lerp(haze, fogRed, 0.6F);
            fogGreen = Mth.lerp(haze, fogGreen, 0.68F);
            fogBlue = Mth.lerp(haze, fogBlue, 0.84F);
            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
         }

         float dark = SpaceClient.atmosphereDarkness();
         if (dark > 0.0F && camera.getFluidInCamera() == FogType.NONE) {
            fogRed = Mth.lerp(dark, fogRed, 0.0F);
            fogGreen = Mth.lerp(dark, fogGreen, 0.0F);
            fogBlue = Mth.lerp(dark, fogBlue, 0.02F);
            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
         }

         float tint = SupernovaClient.skyTint();
         if (!(tint <= 0.0F)) {
            if (camera.getFluidInCamera() == FogType.NONE) {
               float pulse = SupernovaClient.skyPulse();
               fogRed = Mth.lerp(tint, fogRed, 0.74F + 0.2F * pulse);
               fogGreen = Mth.lerp(tint, fogGreen, 0.43F + 0.12F * pulse);
               fogBlue = Mth.lerp(tint, fogBlue, 0.37F + 0.15F * pulse);
               RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
            }
         }
      }
   }
}
