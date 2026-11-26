package net.ruleta.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.ruleta.client.eclipse.EfectosEclipse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public abstract class NivelRenderMixin {
   @Inject(
      method = {"renderSky"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void ruleta$sinCielo(CallbackInfo ci) {
      if (EfectosEclipse.omitirCielo()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderClouds"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void ruleta$sinNubes(CallbackInfo ci) {
      if (EfectosEclipse.omitirNubes()) {
         ci.cancel();
      }
   }
}
