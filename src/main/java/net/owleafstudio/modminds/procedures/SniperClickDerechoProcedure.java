package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SniperClickDerechoProcedure {
   public static boolean execute(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 10, 20, false, false));
         }

         return (
               entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(MobEffects.SLOWNESS)
                  ? _livEnt.getEffect(MobEffects.SLOWNESS).getAmplifier()
                  : 0
            )
            == 20;
      }
   }
}
