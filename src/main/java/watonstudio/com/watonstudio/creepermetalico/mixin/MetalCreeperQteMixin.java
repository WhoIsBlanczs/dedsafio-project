package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.CreeperQteManager;
import com.watonstudio.creepermetalico.MetalCreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MetalCreeperEntity.class)
public abstract class MetalCreeperQteMixin {
   @Inject(method = "detonate", remap = false, at = @At("HEAD"), cancellable = true)
   private void watonstudio$iniciarQte(CallbackInfo ci) {
      MetalCreeperEntity creeper = (MetalCreeperEntity)this;
      if (CreeperQteManager.interceptExplosion(creeper, () -> ((MetalCreeperQteInvoker)creeper).watonstudio$invokeDetonate())) {
         ci.cancel();
      }
   }
}
