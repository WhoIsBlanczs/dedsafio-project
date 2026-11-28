package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.CreeperQteManager;
import com.watonstudio.creepermetalico.FloralCreeperEntity;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperQteMixin {
   @Inject(method = "explode", at = @At("HEAD"))
   private void watonstudio$prepararExplosionFloral(CallbackInfo ci) {
      Creeper creeper = (Creeper)this;
      if (creeper instanceof FloralCreeperEntity && !creeper.level().isClientSide) {
         CreeperQteManager.beginFloralExplosionDamage();
      }
   }

   @Inject(method = "explode", at = @At("TAIL"))
   private void watonstudio$iniciarQteDespuesDeExplosion(CallbackInfo ci) {
      Creeper creeper = (Creeper)this;
      if (creeper instanceof FloralCreeperEntity && !creeper.level().isClientSide) {
         CreeperQteManager.endFloralExplosionDamage();
         CreeperQteManager.startAfterExplosion(creeper);
      }
   }
}
