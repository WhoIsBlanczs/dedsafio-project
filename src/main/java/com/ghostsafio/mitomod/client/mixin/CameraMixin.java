package com.ghostsafio.mitomod.client.mixin;

import com.ghostsafio.mitomod.NaveEntity;
import com.ghostsafio.mitomod.client.OrbitFx;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CameraMixin {
   @Inject(method = "getMaxZoom", at = @At("HEAD"), cancellable = true, require = 0)
   private void ghostsafio$farCamera(float maxZoom, CallbackInfoReturnable<Float> cir) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.player.getVehicle() instanceof NaveEntity) {
         cir.setReturnValue(maxZoom * OrbitFx.cameraZoom());
      }
   }
}
