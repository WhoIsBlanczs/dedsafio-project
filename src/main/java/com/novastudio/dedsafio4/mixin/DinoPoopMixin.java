package com.novastudio.dedsafio4.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"com/novastudio/dedsafio4/DinoBeastEntity"},
   remap = false
)
public abstract class DinoPoopMixin {
   @Inject(
      method = {"poop"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nova$noPoop(ServerLevel var1, CallbackInfo var2) {
      var2.cancel();
   }
}
