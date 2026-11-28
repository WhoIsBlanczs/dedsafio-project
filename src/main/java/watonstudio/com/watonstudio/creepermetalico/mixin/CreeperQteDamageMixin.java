package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.CreeperQteManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class CreeperQteDamageMixin {
   @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private float watonstudio$duplicarDanoFloral(float amount) {
      if (!(this instanceof ServerPlayer)) {
         return amount;
      } else {
         return CreeperQteManager.isFloralExplosionDamageActive() ? amount * 2.0F : amount;
      }
   }
}
