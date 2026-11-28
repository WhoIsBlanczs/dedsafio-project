package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TeOtorgaDosCorazonesProcedure {
   public static void execute(Entity entity) {
      if (entity != null && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
         _entity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 2400, 0));
      }
   }
}
