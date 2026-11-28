package com.novastudio.dedsafio4.mixin.client;

import com.novastudio.dedsafio4.CreeperExtrasClient;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Pone el efecto de blanco y negro / colores invertidos antes de aplicar los efectos de pantalla. */
@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererInvoker {
   @Shadow
   @Final
   private List<Identifier> requestedPostEffects;

   @Inject(method = "applyPostEffects", at = @At("HEAD"))
   private void nova$efectoCreeper(CallbackInfo ci) {
      Identifier efecto = CreeperExtrasClient.active;
      if (efecto != null && !this.requestedPostEffects.contains(efecto)) {
         this.requestedPostEffects.add(efecto);
      }
   }
}
