package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.client.ClientEclipse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin {
   @Inject(method = "setupFog", at = @At("RETURN"))
   private void eclipse$darkFog(Camera camera, int renderDistance, DeltaTracker delta, float darken, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
      float e = ClientEclipse.darkness(1.0F);
      org.joml.Vector4f c = cir.getReturnValue().color;
      if (e > 0.0F && c != null) {
         c.set(Mth.lerp(e, c.x, 0.006F), Mth.lerp(e, c.y, 0.0035F), Mth.lerp(e, c.z, 0.0025F), c.w);
      }
   }
}
