package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.CreeperNuclearEntity;
import com.watonstudio.creepermetalico.CreeperQteManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperNuclearEntity.class)
public abstract class CreeperNuclearQteMixin {
   @Inject(method = "detonate", remap = false, at = @At("HEAD"), cancellable = true)
   private void watonstudio$iniciarQte(CallbackInfo ci) {
      CreeperNuclearEntity creeper = (CreeperNuclearEntity)this;
      if (CreeperQteManager.interceptExplosion(creeper, () -> ((CreeperNuclearQteInvoker)creeper).watonstudio$invokeDetonate())) {
         ci.cancel();
      }
   }
}
