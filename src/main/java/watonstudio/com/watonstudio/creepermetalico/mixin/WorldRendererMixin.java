package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.client.ClientEclipse;
import com.watonstudio.creepermetalico.client.EclipseSkyRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
   @ModifyArg(
      method = "renderSky",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V"),
      index = 1
   )
   private ResourceLocation eclipse$hideVanillaSun(ResourceLocation original) {
      return ClientEclipse.isRunning() && ClientEclipse.inOverworld() && original.getPath().equals("textures/environment/sun.png")
         ? ClientEclipse.blankTexture()
         : original;
   }

   @ModifyConstant(method = "renderSky", constant = @Constant(floatValue = 30.0F), require = 0)
   private float eclipse$vanillaSunSize(float original) {
      return ClientEclipse.isRunning() && ClientEclipse.inOverworld() ? 0.0F : original;
   }

   @Inject(method = "renderSky", at = @At("TAIL"))
   private void eclipse$renderEclipse(
      Matrix4f frustumMatrix, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci
   ) {
      EclipseSkyRenderer.render(frustumMatrix, tickDelta, camera, thickFog);
   }
}
